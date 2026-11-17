package app.mousike.android.components.settings.appearance.preview

import app.mousike.android.components.dialog.settings.HistorySortSettingsDialog
import app.mousike.android.components.dialog.settings.HomeAlbumsSortSettingsDialog
import app.mousike.android.components.dialog.settings.HomeAlbumsToolbarSettingsDialog
import app.mousike.android.components.dialog.settings.HomeArtistsSortSettingsDialog
import app.mousike.android.components.dialog.settings.HomeArtistsToolbarSettingsDialog
import app.mousike.android.components.dialog.settings.HomeLibrarySortSettingsDialog
import app.mousike.android.components.dialog.settings.HomeLibraryToolbarSettingsDialog
import app.mousike.android.components.dialog.settings.HomeSongsSortSettingsDialog
import app.mousike.android.components.dialog.settings.HomeSongsToolbarSettingsDialog
import app.mousike.android.components.dialog.settings.LocalPlaylistSortSettingsDialog
import app.mousike.android.components.dialog.settings.LocalPlaylistToolbarSettingsDialog
import app.mousike.android.components.navigation.dock.DockToolbarPreferences

internal data class PortableControlList(
    val orderKey: String,
    val ids: List<String>,
    val visibilityPrefix: String,
    val visibilitySuffix: String,
)

internal fun ThemeFields.toolbars() {
    optional("toolbars") {
        group("toolbars") {
            controlLists("songs", HomeSongsToolbarSettingsDialog.portableLists())
            controlLists("artists", HomeArtistsToolbarSettingsDialog.portableLists())
            controlLists("albums", HomeAlbumsToolbarSettingsDialog.portableLists())
            controlLists("library", HomeLibraryToolbarSettingsDialog.portableLists())
            controlList("localPlaylist", LocalPlaylistToolbarSettingsDialog.portableList())
            optional("dockOrder") {
                order("dockOrder", DockToolbarPreferences.orderKey)
            }
        }
    }
    optional("sortMenus") {
        group("sortMenus") {
            controlLists("songs", HomeSongsSortSettingsDialog.portableLists())
            controlLists("artists", HomeArtistsSortSettingsDialog.portableLists())
            controlLists("albums", HomeAlbumsSortSettingsDialog.portableLists())
            controlLists("library", HomeLibrarySortSettingsDialog.portableLists())
            controlList("localPlaylist", LocalPlaylistSortSettingsDialog.portableList())
            controlList("history", HistorySortSettingsDialog.portableList())
        }
    }
}

private fun ThemeFields.controlLists(field: String, lists: Map<String, PortableControlList>) {
    optional(field) {
        group(field) {
            lists.forEach { (name, list) -> controlList(name, list) }
        }
    }
}

private fun ThemeFields.controlList(field: String, list: PortableControlList) {
    optional(field) {
        group(field) {
            optional("order") {
                order("order", list.orderKey, list.ids.toSet())
            }
            optional("visibility") {
                group("visibility") {
                    list.ids.forEach { id ->
                        optional(id) {
                            flag(id, list.visibilityPrefix + id + list.visibilitySuffix, true)
                        }
                    }
                }
            }
        }
    }
}
