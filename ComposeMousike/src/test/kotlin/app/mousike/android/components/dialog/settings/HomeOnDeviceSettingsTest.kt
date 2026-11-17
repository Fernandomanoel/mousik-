package app.mousike.android.components.dialog.settings

import app.mousike.android.R
import app.mousike.android.components.settings.common.SettingsNestedOption
import app.mousike.android.enums.AlbumSortBy
import app.mousike.android.enums.ArtistSortBy
import app.mousike.android.enums.PlaylistSortBy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeOnDeviceSettingsTest {
    @Test
    fun onDeviceToolbarsExposeLocalActions() {
        val toolbars = listOf(
            HomeAlbumsToolbarSettingsDialog.searchOptions(),
            HomeArtistsToolbarSettingsDialog.searchOptions(),
            HomeLibraryToolbarSettingsDialog.searchOptions(),
        )
        toolbars.forEach { options ->
            val ids = onDeviceOptions(options)
            assertFalse("dev_sync" in ids)
            assertFalse("dev_export_dialog" in ids)
            assertFalse("dev_import_menu" in ids)
            assertTrue("dev_sort" in ids)
            assertTrue("dev_search" in ids)
        }
    }

    @Test
    fun onDeviceSortSettingsUseTheRuntimeSortIds() {
        assertEquals(
            AlbumSortBy.entries.map { "alb_dev_${it.name}" }.toSet(),
            onDeviceOptions(HomeAlbumsSortSettingsDialog.searchOptions()),
        )
        assertEquals(
            ArtistSortBy.entries.map { "art_dev_${it.name}" }.toSet(),
            onDeviceOptions(HomeArtistsSortSettingsDialog.searchOptions()),
        )
        assertEquals(
            PlaylistSortBy.entries.map { "lib_dev_${it.name}" }.toSet(),
            onDeviceOptions(HomeLibrarySortSettingsDialog.searchOptions()),
        )
    }

    private fun onDeviceOptions(options: List<SettingsNestedOption>): Set<String> =
        options.filter { it.sectionTitleRes == R.string.library_on_device_title }
            .mapTo(mutableSetOf()) { it.itemId }
}
