package app.mousike.android.components.settings.appearance.preview

import app.mousike.android.components.dialog.settings.albumsDefaultOrder
import app.mousike.android.components.dialog.settings.albumsThemeToggleKeys
import app.mousike.android.components.dialog.settings.artistsDefaultOrder
import app.mousike.android.components.dialog.settings.artistsThemeToggleKeys
import app.mousike.android.components.dialog.settings.buildSectionDefs
import app.mousike.android.components.dialog.settings.playlistsDefaultOrder
import app.mousike.android.components.dialog.settings.playlistsThemeToggleKeys
import app.mousike.android.components.dialog.settings.songsDefaultOrder
import app.mousike.android.components.dialog.settings.songsThemeToggleKeys
import app.mousike.android.enums.DislikeMode
import app.mousike.android.utils.DataStoreUtils
import app.mousike.android.utils.excludeDislikedAlbumsKey
import app.mousike.android.utils.excludeDislikedArtistsKey
import app.mousike.android.utils.excludeDislikedSongsKey
import app.mousike.android.utils.homeAlbumsOrderKey
import app.mousike.android.utils.homeArtistsOrderKey
import app.mousike.android.utils.homePlaylistsOrderKey
import app.mousike.android.utils.homeSongsOrderKey
import app.mousike.android.utils.quickPicksSectionOrderKey

internal fun ThemeFields.homeLayouts() {
    optional("homeSections") {
        group("homeSections") {
            layout("songs", homeSongsOrderKey, songsDefaultOrder, songsThemeToggleKeys, excludeDislikedSongsKey)
            layout("albums", homeAlbumsOrderKey, albumsDefaultOrder, albumsThemeToggleKeys, excludeDislikedAlbumsKey)
            layout("artists", homeArtistsOrderKey, artistsDefaultOrder, artistsThemeToggleKeys, excludeDislikedArtistsKey)
            layout("playlists", homePlaylistsOrderKey, playlistsDefaultOrder, playlistsThemeToggleKeys)
            val quickPicks = buildSectionDefs()
            layout("quickPicks", quickPicksSectionOrderKey, quickPicks.keys, quickPicks.mapValues { it.value.preferenceKey })
        }
    }
}

private fun ThemeFields.layout(
    field: String,
    orderKey: String,
    ids: Collection<String>,
    toggleKeys: Map<String, String>,
    dislikedKey: String? = null,
) {
    optional(field) {
        group(field) {
            optional("order") { order("order", orderKey, ids.toSet()) }
            if (dislikedKey != null) {
                optional("disliked") { option<DislikeMode>("disliked", dislikedKey, DislikeMode.Enabled) }
            }
            toggleKeys.forEach { (id, key) ->
                val name = id.replace('_', '-')
                optional(name) { flag(name, key, true) }
            }
        }
    }
}

internal val portableBurgerKeys: Set<String> get() =
    DataStoreUtils.DEFAULT_BURGER_MENU_ORDER.map(DataStoreUtils::burgerMenuToggleKey).toSet() +
        DataStoreUtils.KEY_BURGER_MENU_ORDER

internal fun ThemeFields.portableBurgerMenu() {
    optional("burgerMenu") {
        group("burgerMenu") {
            val ids = DataStoreUtils.DEFAULT_BURGER_MENU_ORDER.toSet()
            optional("order") {
                order("order", DataStoreUtils.KEY_BURGER_MENU_ORDER, ids,
                    mapOf("separator" to DataStoreUtils.MAX_BURGER_MENU_SEPARATORS))
            }
            (ids - DataStoreUtils.LOCKED_BURGER_MENU_ITEMS).forEach { id ->
                val name = id.replace('_', '-')
                optional(name) { flag(name, DataStoreUtils.burgerMenuToggleKey(id), true) }
            }
        }
    }
}
