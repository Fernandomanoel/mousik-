package app.mousike.android.playback.utils

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import app.mousike.android.R
import app.mousike.android.appContext
import app.mousike.android.core.database.Database
import app.mousike.android.playback.services.PlayerServiceModern
import app.mousike.android.core.utils.Toaster
import app.mousike.android.utils.ToastRelay
import app.mousike.android.enums.MaxSongs
import app.mousike.android.models.Song
import app.mousike.android.utils.asMediaItem
import app.mousike.android.utils.forcePlayFromBeginning
import app.mousike.android.utils.getEnum
import app.mousike.android.utils.maxSongsInQueueKey
import app.mousike.android.utils.mediaItems
import app.mousike.android.utils.preferences
import app.mousike.android.utils.excludeDislikedSongsKey
import app.mousike.android.utils.excludeDislikedArtistsKey
import app.mousike.android.utils.excludeDislikedAlbumsKey
import app.mousike.android.enums.DislikeMode
import app.mousike.android.utils.coroutines.MousikeDispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(UnstableApi::class)
object Shuffler {

    /** Test seam: overridden with `UnconfinedTestDispatcher()` in `ShufflerTest` so assertions
     *  made right after [play] stay deterministic instead of racing a real background thread. */
    internal var backgroundDispatcher: CoroutineDispatcher = MousikeDispatchers.DATA
    internal val scope = MousikeDispatchers.fireAndForget(SupervisorJob())

    /**
     * [onComplete] is invoked once the real work for the given exit path is actually finished,
     * never before -- on the caller's own thread for the immediate `mediaItems.isEmpty()` early
     * return below (no coroutine involved yet), and on [MousikeDispatchers.UI] for every other exit
     * path (empty after filtering, a filtering failure, and success/failure of the final playback
     * dispatch). `play()` itself stays non-suspend (~8 of its ~13 callers invoke it from a
     * non-suspend `onClick`), so callers that need to react to completion (e.g. clearing a loading
     * flag) must do so via this callback rather than by assuming `play()` blocks until done, as it
     * used to via `runBlocking`.
     */
    fun play(binder: PlayerServiceModern.Binder, mediaItems: List<MediaItem>, onComplete: () -> Unit = {}) {
        if (mediaItems.isEmpty()) {
            ToastRelay.i(R.string.player_shuffle_empty_message)
            onComplete()
            return
        }

        scope.launch(backgroundDispatcher) {
            try {
                var filteredMediaItems = mediaItems

                // Filter disliked songs if setting is enabled
                val preferences = appContext().preferences
                val excludeDislikedSongs = preferences.getString(excludeDislikedSongsKey, DislikeMode.Enabled.name)?.let { runCatching { DislikeMode.valueOf(it) }.getOrNull() } ?: DislikeMode.Enabled
                if (excludeDislikedSongs.isEnabled) {
                    val dislikedSongIds = Database.songTable.getAllDislikedIds()
                    if (dislikedSongIds.isNotEmpty()) {
                        filteredMediaItems = filteredMediaItems.filter {
                            !dislikedSongIds.contains(it.mediaId)
                        }
                    }
                }

                // Filter songs from disliked artists if setting is enabled
                val excludeDislikedArtists = preferences.getString(excludeDislikedArtistsKey, DislikeMode.Enabled.name)?.let { runCatching { DislikeMode.valueOf(it) }.getOrNull() } ?: DislikeMode.Enabled
                if (excludeDislikedArtists.isEnabled) {
                    val dislikedArtistSongIds = Database.songTable.getSongsByDislikedArtists()
                    if (dislikedArtistSongIds.isNotEmpty()) {
                        filteredMediaItems = filteredMediaItems.filter {
                            !dislikedArtistSongIds.contains(it.mediaId)
                        }
                    }
                }

                // Filter songs from disliked albums if setting is enabled
                val excludeDislikedAlbums = preferences.getString(excludeDislikedAlbumsKey, DislikeMode.Enabled.name)?.let { runCatching { DislikeMode.valueOf(it) }.getOrNull() } ?: DislikeMode.Enabled
                if (excludeDislikedAlbums.isEnabled) {
                    val dislikedAlbumSongIds = Database.songTable.getSongsByDislikedAlbums()
                    if (dislikedAlbumSongIds.isNotEmpty()) {
                        filteredMediaItems = filteredMediaItems.filter {
                            !dislikedAlbumSongIds.contains(it.mediaId)
                        }
                    }
                }

                if (filteredMediaItems.isEmpty()) {
                    withContext(MousikeDispatchers.UI) {
                        ToastRelay.i(R.string.player_shuffle_empty_message)
                        onComplete()
                    }
                    return@launch
                }

                val max = preferences
                    .getEnum(maxSongsInQueueKey, MaxSongs.Unlimited)
                    .toInt()
                val toPlay = filteredMediaItems.shuffled().take(max)
                withContext(MousikeDispatchers.UI) {
                    try {
                        binder.stopRadio()
                        binder.player.forcePlayFromBeginning(toPlay)
                        ToastRelay.s(R.string.player_shuffled_song_count_format, toPlay.size)
                    } catch (e: Exception) {
                        ToastRelay.e(R.string.player_no_song_found)
                    } finally {
                        onComplete()
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Preference/DB lookups above (getAllDislikedIds/getSongsByDislikedArtists/
                // getSongsByDislikedAlbums) run with no caller-side try/finally to fall back on
//
                // now that this whole block is dispatched off-main -- without this catch, a
                // transient DB error would leave onComplete() (and therefore any caller's loading
//
                // flag) stuck forever instead of surfacing an error toast like the equivalent
//
                // failure a few lines below already does.
                withContext(MousikeDispatchers.UI) {
                    ToastRelay.e(R.string.player_no_song_found)
                    onComplete()
                }
            }
        }
    }

    @JvmName("playSongs")
    fun play(binder: PlayerServiceModern.Binder, songs: List<Song>, onComplete: () -> Unit = {}) {
        play(binder, songs.map(Song::asMediaItem), onComplete)
    }

    fun queue(player: Player) {
        try {
            val current = player.currentMediaItemIndex
            val total = player.mediaItemCount
            if (total <= 1) return

            val items = player.mediaItems.toMutableList().apply {
                removeAt(current)
            }
            val count = items.size
            if (count > 0) {
                if (current > 0) player.removeMediaItems(0, current)
                if (current < player.mediaItemCount - 1) player.removeMediaItems(1, player.mediaItemCount)
                player.addMediaItems(items.shuffled())
                Toaster.s(R.string.player_queue_shuffled_count_format, formatArgs = *arrayOf(count))
            }
        } catch (e: Exception) {
            Toaster.e(R.string.player_no_song_found)
        }
    }

    fun <T> shuffle(list: List<T>): List<T> = list.shuffled()

    fun positions(playlistId: Long) {
        MousikeDispatchers.fireAndForget(MousikeDispatchers.DATA).launch {
            try {
                val items = Database.songPlaylistMapTable.allSongsOf(playlistId).first()
                val count = items.size
                if (count == 0) return@launch
                val shuffled = items.shuffled()
                Database.asyncTransaction {
                    shuffled.forEachIndexed { i, song ->
                        Database.songPlaylistMapTable.updatePosition(playlistId, song.id, i)
                    }
                }
                withContext(MousikeDispatchers.UI) {
                    ToastRelay.successWithPlural(R.plurals.playlist_positions_shuffled_count, R.string.playlist_positions_shuffled, count)
                }
            } catch (e: Exception) {
                withContext(MousikeDispatchers.UI) {
                    Toaster.e(R.string.player_no_song_found)
                }
            }
        }
    }
}
