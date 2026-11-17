package app.mousike.android.playback.services.automotive.browse.handlers

import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastMap

import app.mousike.android.playback.services.automotive.models.AutoAccessPolicy
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.offline.Download
import app.mousike.android.enums.MaxTopPlaylistItems
import app.mousike.android.enums.SongSortBy
import app.mousike.android.enums.SortOrder
import app.mousike.android.utils.MaxTopPlaylistItemsCustomValueKey
import app.mousike.android.utils.MaxTopPlaylistItemsKey
import app.mousike.android.utils.Preference
import app.mousike.android.utils.asSong
import app.mousike.android.utils.getEnum
import app.mousike.android.utils.preferences
import app.mousike.android.core.database.Database
import app.mousike.android.playback.services.automotive.browse.loadAutoPlaylistSongs
import app.mousike.android.download.utils.MyDownloadHelper
import app.mousike.android.playback.services.automotive.models.CancellableAutoMediaItemMapper
import app.mousike.android.playback.services.automotive.session.AutoMediaIdContract
import app.mousike.android.playback.services.automotive.session.AutoSessionConstants
import app.mousike.android.playback.services.PlayerServiceModern
import app.mousike.android.playback.services.isLocal
import app.mousike.android.playback.services.automotive.models.AutoSearchState
import app.mousike.innertube.Innertube
import app.mousike.innertube.requests.playlistPage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import app.mousike.android.enums.OnDeviceSongSortBy
import app.mousike.android.components.ui.screens.home.onDevice.getProfileLocalSongs
import app.mousike.android.MODIFIED_PREFIX

class PlaylistDetailHandler : BrowseHandler {
    override fun handles(parentId: String): Boolean {
        if (!parentId.startsWith("${PlayerServiceModern.PLAYLIST}/")) return false
        val parts = parentId.split("/")
        return parts.size == 2
    }

    override suspend fun getChildren(
        parentId: String,
        context: Context,
        database: Database,
        downloadHelper: MyDownloadHelper,
        binder: PlayerServiceModern.Binder?
    ): List<MediaItem> {
        val parts = parentId.split("/")
        val playlistId = parts[1]
        // The shuffle item must carry the playlist id so queue resolution can
        // find the right song list (issue #777).
        val shuffleItem = AutoSessionConstants.shuffleItem(context, AutoMediaIdContract.playlistShuffle(playlistId))
        val listFlow = when (playlistId) {
            AutoSessionConstants.ID_FAVORITES -> {
                val sortBy = context.preferences.getEnum(Preference.HOME_SONGS_FAVORITES_SORT_BY.key, SongSortBy.Title)
                val sortOrder = context.preferences.getEnum(Preference.HOME_SONGS_FAVORITES_SORT_ORDER.key, SortOrder.Ascending)
                database.songTable.sortFavorites(sortBy, sortOrder)
            }
            AutoSessionConstants.ID_CACHED -> {
                val sortBy = context.preferences.getEnum(Preference.HOME_SONGS_OFFLINE_SORT_BY.key, SongSortBy.Title)
                val sortOrder = context.preferences.getEnum(Preference.HOME_SONGS_OFFLINE_SORT_ORDER.key, SortOrder.Ascending)
                database.formatTable.sortAllWithSongs(sortBy, sortOrder).map { flist ->
                    flist.fastFilter { itf -> val contentLength = itf.format.contentLength; contentLength != null && (if (binder != null) binder.cache.isCached(itf.song.id, 0L, contentLength) else false) }.fastMap { itf -> itf.song }
                }
            }
            AutoSessionConstants.ID_TOP -> {
                val sortBy = context.preferences.getEnum(Preference.HOME_SONGS_TOP_SORT_BY.key, SongSortBy.Title)
                val sortOrder = context.preferences.getEnum(Preference.HOME_SONGS_TOP_SORT_ORDER.key, SortOrder.Ascending)
                val topIds = database.eventTable.findSongsMostPlayedBetween(from = 0, limit = context.preferences.getEnum(MaxTopPlaylistItemsKey, MaxTopPlaylistItems.`10`).toInt(context.preferences.getInt(MaxTopPlaylistItemsCustomValueKey, 10))).first().map { it.id }.toSet()
                database.songTable.sortAll(sortBy, sortOrder, excludeHidden = true).map { songs -> songs.filter { it.id in topIds } }
            }
            AutoSessionConstants.ID_ONDEVICE -> {
                val sortBy = context.preferences.getEnum(Preference.HOME_ON_DEVICE_SONGS_SORT_BY.key, OnDeviceSongSortBy.Title)
                val sortOrder = context.preferences.getEnum(Preference.HOME_ON_DEVICE_SONGS_SORT_ORDER.key, SortOrder.Ascending)
                context.getProfileLocalSongs(sortBy, sortOrder).map { songs -> songs.keys.toList() }
            }
            AutoSessionConstants.ID_DOWNLOADED -> {
                val sortBy = context.preferences.getEnum(Preference.HOME_SONGS_DOWNLOADED_SORT_BY.key, SongSortBy.Title)
                val sortOrder = context.preferences.getEnum(Preference.HOME_SONGS_DOWNLOADED_SORT_ORDER.key, SortOrder.Ascending)
                val downloads = downloadHelper.downloads.value
                database.songTable.sortAll(sortBy, sortOrder, excludeHidden = false).map { songs ->
                    songs.fastFilter { song -> downloads[song.id]?.state == Download.STATE_COMPLETED }
                }
            }
            else -> {
                if (playlistId.toLongOrNull() != null) {
                    flowOf(loadAutoPlaylistSongs(context, database, downloadHelper, playlistId.toLong()))
                } else {
                    val playlistPage = if (AutoAccessPolicy(context, database, downloadHelper, binder).online) Innertube.playlistPage(browseId = playlistId.removePrefix(MODIFIED_PREFIX))?.getOrNull() else null
                    val songs = playlistPage?.songsPage?.items?.map { item -> item.asSong } ?: emptyList()
                    // Scope the songs to their own playlist id so playback queues
                    // never mix songs from previously opened playlists (issue #777).
                    AutoSearchState.playlistSongsById = AutoSearchState.playlistSongsById + (playlistId to songs.distinctBy { s -> s.id })
                    flowOf(songs)
                }
            }
        }
        return listOf(shuffleItem) + listFlow.first().map { song -> CancellableAutoMediaItemMapper.mapSongToMediaItem(song, parentId, loadArtwork = song.isLocal) }
    }
}
