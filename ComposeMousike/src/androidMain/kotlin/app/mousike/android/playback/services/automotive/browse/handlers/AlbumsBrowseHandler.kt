package app.mousike.android.playback.services.automotive.browse.handlers

import android.content.Context
import app.mousike.android.core.ondevice.isDeviceLibraryEnabled
import app.mousike.android.core.ondevice.isDeviceAlbumId
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import app.mousike.android.enums.SortOrder
import app.mousike.android.utils.Preference
import app.mousike.android.utils.getEnum
import app.mousike.android.utils.homeAlbumsOrderKey
import app.mousike.android.utils.preferences
import app.mousike.android.utils.showFavoritesAlbumKey
import app.mousike.android.utils.showOnDeviceAlbumsKey
import app.mousike.android.R
import app.mousike.android.core.database.Database
import app.mousike.android.download.utils.MyDownloadHelper
import app.mousike.android.playback.services.automotive.models.CancellableAutoMediaItemMapper
import app.mousike.android.playback.services.automotive.session.AutoSessionConstants
import app.mousike.android.playback.services.PlayerServiceModern
import app.mousike.android.playback.services.automotive.models.AutoMediaItemMapper.browsableMediaItem
import app.mousike.android.playback.services.automotive.models.AutoMediaItemMapper.drawableUri
import kotlinx.coroutines.flow.first
import app.mousike.android.enums.AlbumSortBy

class AlbumsBrowseHandler internal constructor(
    private val onDevice: AutoDeviceGroupsBrowseHandler = AutoDeviceGroupsBrowseHandler(),
) : BrowseHandler {
    override fun handles(parentId: String): Boolean = parentId == PlayerServiceModern.ALBUM ||
            parentId.startsWith("ALBUMS_") || parentId.startsWith("ID_ALBUMS_")

    override suspend fun getChildren(
        parentId: String,
        context: Context,
        database: Database,
        downloadHelper: MyDownloadHelper,
        binder: PlayerServiceModern.Binder?
    ): List<MediaItem> {
        return when (parentId) {
            PlayerServiceModern.ALBUM -> {
                // Mirror the phone's HomeAlbum tabs: same order (homeAlbumsOrderKey)
                // and visibility (showFavoritesAlbumKey) as the in-app settings
//
                // dialog. Disliked albums stay always hidden from All/Favorites
                // and always shown in their dedicated folder — opening one + the
//
                // DISLIKE_ALBUM command removes its dislike (toggle).
                val showFavoritesAlbum = try { context.preferences.getBoolean(showFavoritesAlbumKey, true) } catch (e: Exception) { true }
                val onDeviceCount = if (context.isDeviceLibraryEnabled() && context.preferences.getBoolean(showOnDeviceAlbumsKey, true)) {
                    onDevice.albums(context, database).size
                } else null
                val libraryCount = database.albumTable.allInLibrary().first().count { it.dislikedAt == null && !isDeviceAlbumId(it.id) }
                val favoritesCount = database.albumTable.allBookmarked().first().count { it.dislikedAt == null && !isDeviceAlbumId(it.id) }
                val dislikedCount = database.albumTable.allDisliked().first().size
                val categoryItems = mutableMapOf(
                    "all" to browsableMediaItem(AutoSessionConstants.ID_ALBUMS_LIBRARY, context.getString(R.string.library_title), libraryCount.toString(), drawableUri(context, R.drawable.album), MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS),
                    "favorites" to browsableMediaItem(AutoSessionConstants.ID_ALBUMS_FAVORITES, context.getString(R.string.library_favorites_title), favoritesCount.toString(), drawableUri(context, R.drawable.heart), MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS),
                    "disliked" to browsableMediaItem(AutoSessionConstants.ID_ALBUMS_DISLIKED, context.getString(R.string.library_disliked_title), dislikedCount.toString(), drawableUri(context, R.drawable.heart_dislike), MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS)
                )
                if (onDeviceCount != null) {
                    categoryItems["on_device"] = browsableMediaItem(
                        AutoSessionConstants.ID_ALBUMS_ONDEVICE, context.getString(R.string.library_on_device_title),
                        onDeviceCount.toString(), drawableUri(context, R.drawable.folder), MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS,
                    )
                }
                homeCategoryOrder(context, homeAlbumsOrderKey, listOf("all", "favorites", "disliked", "on_device"))
                    .mapNotNull { id -> if (id == "favorites" && !showFavoritesAlbum) null else categoryItems[id] }
            }
            AutoSessionConstants.ID_ALBUMS_LIBRARY -> {
                val shuffleItem = AutoSessionConstants.shuffleItem(context, AutoSessionConstants.ID_ALBUMS_LIBRARY_SHUFFLE)
                val sortBy = context.preferences.getEnum(Preference.HOME_ALBUMS_LIBRARY_SORT_BY.key, AlbumSortBy.Title)
                val sortOrder = context.preferences.getEnum(Preference.HOME_ALBUMS_LIBRARY_SORT_ORDER.key, SortOrder.Ascending)
                val albums = database.albumTable.sortInLibrary(sortBy, sortOrder).first().filter { it.dislikedAt == null && !isDeviceAlbumId(it.id) }.map { album -> CancellableAutoMediaItemMapper.mapAlbumToMediaItem(PlayerServiceModern.ALBUM, album.id, album.title ?: "", album.authorsText, album.thumbnailUrl) }
                listOf(shuffleItem) + albums
            }
            AutoSessionConstants.ID_ALBUMS_FAVORITES -> {
                val shuffleItem = AutoSessionConstants.shuffleItem(context, AutoSessionConstants.ID_ALBUMS_FAVORITES_SHUFFLE)
                val sortBy = context.preferences.getEnum(Preference.HOME_ALBUMS_FAVORITES_SORT_BY.key, AlbumSortBy.Title)
                val sortOrder = context.preferences.getEnum(Preference.HOME_ALBUMS_FAVORITES_SORT_ORDER.key, SortOrder.Ascending)
                val albums = database.albumTable.sortBookmarked(sortBy, sortOrder).first().filter { it.dislikedAt == null && !isDeviceAlbumId(it.id) }.map { album -> CancellableAutoMediaItemMapper.mapAlbumToMediaItem(PlayerServiceModern.ALBUM, album.id, album.title ?: "", album.authorsText, album.thumbnailUrl) }
                listOf(shuffleItem) + albums
            }
            AutoSessionConstants.ID_ALBUMS_DISLIKED -> {
                val shuffleItem = AutoSessionConstants.shuffleItem(context, AutoSessionConstants.ID_ALBUMS_DISLIKED_SHUFFLE)
                val albums = database.albumTable.allDisliked().first()
                listOf(shuffleItem) + albums.map { album -> CancellableAutoMediaItemMapper.mapAlbumToMediaItem(PlayerServiceModern.ALBUM, album.id, album.title ?: "", album.authorsText, album.thumbnailUrl) }
            }
            else -> emptyList()
        }
    }
}
