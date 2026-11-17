package app.mousike.android.components.podcast

import androidx.media3.common.MediaItem
import app.mousike.android.models.Playlist
import app.mousike.android.utils.asMediaItem
import app.mousike.android.core.utils.Toaster
import app.mousike.android.core.database.Database
import app.mousike.android.core.database.isPodcastBrowseId
import app.mousike.android.core.database.canRefreshPlaylist
import app.mousike.android.components.playlist.refreshPlaylistSource
import app.mousike.android.components.playlist.loadCompletePodcastEpisodes
import app.mousike.android.R
import app.mousike.android.utils.coroutines.MousikeDispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/** Removes a saved snapshot without recreating a row deleted by another surface. */
internal suspend fun removeSavedPodcastBookmark(playlistId: Long): PodcastBookmarkOperation =
    withContext(MousikeDispatchers.DATA) {
        var operation = PodcastBookmarkOperation.Remove
        Database.transaction {
            val latest = playlistTable.findById(playlistId).first()
            if (latest != null) {
                operation = if (isPodcastBrowseId(latest.browseId)) podcastBookmarkOperation(latest)
                    else PodcastBookmarkOperation.ExistingImport
                if (operation == PodcastBookmarkOperation.Remove) playlistTable.delete(latest)
            }
        }
        operation
    }

/** Handles local podcast bookmarking without changing remote account-library flags. */
fun launchSavedPodcastBookmarkToggle(
    playlist: Playlist?,
    scope: CoroutineScope,
    onRemoved: () -> Unit,
    onExistingImport: () -> Unit,
): Boolean {
    if (playlist == null || !isPodcastBrowseId(playlist.browseId)) return false
    scope.launch(MousikeDispatchers.UI) {
        try {
            if (removeSavedPodcastBookmark(playlist.id) == PodcastBookmarkOperation.ExistingImport) {
                onExistingImport()
            } else {
                Toaster.done()
                onRemoved()
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Timber.tag("PodcastBookmark").e(error, "Unable to remove saved podcast bookmark")
        }
    }
    return true
}

/** Replaces only this snapshot's mappings after a successful exact-ID podcast fetch. */
internal suspend fun refreshPodcastPlaylist(
    playlistId: Long,
    browseId: String,
    loadEpisodes: suspend (String) -> List<MediaItem>? = { id ->
        loadCompletePodcastEpisodes(id).map { it.asMediaItem }
    },
): Boolean = withContext(MousikeDispatchers.DATA) {
    if (!isPodcastBrowseId(browseId)) return@withContext false
    val latest = Database.playlistTable.findById(playlistId).first()
        ?.takeIf { it.browseId == browseId && !it.isEditable } ?: return@withContext false
    refreshPlaylistSource(latest, loadEpisodes)
}

/** Routes podcast refresh to its browse endpoint and keeps failures visible in logs. */
fun launchSavedPodcastRefresh(playlist: Playlist?, scope: CoroutineScope): Boolean {
    val browseId = playlist?.browseId ?: return false
    if (!isPodcastBrowseId(browseId)) return false
    if (!canRefreshPlaylist(playlist)) return true
    scope.launch(MousikeDispatchers.DATA) {
        try {
            if (refreshPodcastPlaylist(playlist.id, browseId)) Toaster.done()
            else {
                Timber.tag("PodcastRefresh").w("Podcast snapshot refresh did not complete")
                Toaster.e(R.string.common_error_title)
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            Timber.tag("PodcastRefresh").e(error, "Unable to refresh saved podcast episodes")
            Toaster.e(R.string.common_error_title)
        }
    }
    return true
}
