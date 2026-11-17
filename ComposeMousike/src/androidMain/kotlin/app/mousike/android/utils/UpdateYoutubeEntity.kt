package app.mousike.android.utils

import app.mousike.android.core.database.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.media3.common.util.UnstableApi
import app.mousike.android.compose.persist.persist
import app.mousike.innertube.Innertube
import app.mousike.innertube.requests.albumPage
import app.mousike.innertube.requests.artistPage
import app.mousike.android.core.database.Database
import app.mousike.android.models.Album
import app.mousike.android.models.Artist
import app.mousike.android.models.SongAlbumMap
import app.mousike.android.playback.services.mergePageValue
import app.mousike.android.utils.coroutines.MousikeDispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import app.mousike.android.MODIFIED_PREFIX

@Composable
fun UpdateYoutubeArtist(browseId: String) {

    var artistPage by persist<Innertube.ArtistInfoPage?>("artist/$browseId/artistPage")
    var artist by persist<Artist?>("artist/$browseId/artist")
    val tabIndex by rememberPreference(artistScreenTabIndexKey, defaultValue = 0)

    LaunchedEffect(browseId) {
        if (browseId.startsWith("LOCAL_ARTIST_")) return@LaunchedEffect
        
        Database.artistTable
                .findById( browseId )
                .combine(snapshotFlow { tabIndex }.map { it != 4 }) { artist, mustFetch -> artist to mustFetch }
                .distinctUntilChanged()
                .collect { (currentArtist, mustFetch) ->
                    artist = currentArtist

                    if (artistPage == null && (currentArtist?.timestamp == null || mustFetch)) {
                        withContext(MousikeDispatchers.DATA) {
                            Innertube.artistPage(browseId = browseId.removePrefix(MODIFIED_PREFIX))
                                ?.onSuccess { currentArtistPage ->
                                    artistPage = currentArtistPage

                                    Database.artistTable.upsert(
                                        Artist(
                                            id = browseId,
                                            // Empty-page guard (approved legacy fix): a channel page without
                                            // a music header returns null/blank values; mergePageValue keeps

                                            // the stored name/thumbnail instead of clobbering them.
                                            name = mergePageValue(currentArtist?.name, currentArtistPage.name),
                                            thumbnailUrl = mergePageValue(currentArtist?.thumbnailUrl, currentArtistPage.thumbnail?.url),
                                            timestamp = System.currentTimeMillis(),
                                            bookmarkedAt = currentArtist?.bookmarkedAt,
                                            dislikedAt = currentArtist?.dislikedAt,
                                            lastFetch = System.currentTimeMillis()
                                        )
                                    )
                                }
                        }
                    }
                }
    }

}

@UnstableApi
@Composable
fun UpdateYoutubeAlbum (browseId: String) {
    var album by persist<Album?>("album/$browseId/album")
    var albumPage by persist<Innertube.PlaylistOrAlbumPage?>("album/$browseId/albumPage")
    val tabIndex by rememberSaveable {mutableStateOf(0)}
    LaunchedEffect(browseId) {
        if (browseId.startsWith("LOCAL_ALBUM_")) return@LaunchedEffect
        
        Database.albumTable
                .findById( browseId )
                .combine(snapshotFlow { tabIndex }) { album, tabIndex -> album to tabIndex }
                .collect { (currentAlbum, tabIndex) ->
                    album = currentAlbum

                    if (albumPage == null && (currentAlbum?.timestamp == null || tabIndex == 1)) {
                        withContext(MousikeDispatchers.DATA) {
                            Innertube.albumPage(browseId = browseId.removePrefix(MODIFIED_PREFIX))
                                ?.onSuccess { currentAlbumPage ->
                                    albumPage = currentAlbumPage

                                    Database.songAlbumMapTable.clear( browseId )

                                    Database.albumTable.upsert(
                                        Album(
                                            id = browseId,
                                            // Empty-page guard (approved legacy fix): mergePageValue keeps
                                            // the stored value whenever the fetched page value is null/blank.
                                            title = mergePageValue(album?.title, currentAlbumPage.title),
                                            thumbnailUrl = mergePageValue(album?.thumbnailUrl, currentAlbumPage.thumbnail?.url),
                                            year = mergePageValue(album?.year, currentAlbumPage.year),
                                            authorsText = mergePageValue(album?.authorsText, currentAlbumPage.authors.artistEntryNames().joinToString(", ").takeIf { it.isNotBlank() }),
                                            shareUrl = mergePageValue(album?.shareUrl, currentAlbumPage.url),
                                            timestamp = System.currentTimeMillis(),
                                            bookmarkedAt = album?.bookmarkedAt,
                                            dislikedAt = album?.dislikedAt
                                        )
                                    )
                                    currentAlbumPage.songsPage
                                                    ?.items
                                                    ?.onEach { Database.upsert(it) }
                                                    ?.map(Innertube.SongItem::asMediaItem)
                                                    ?.mapIndexed { position, mediaItem ->
                                                        SongAlbumMap(
                                                            songId = mediaItem.mediaId,
                                                            albumId = browseId,
                                                            position = position
                                                        )
                                                    }
                                                   ?.also( Database.songAlbumMapTable::upsert )
                                }
                        }

                    }
                }
    }
}


