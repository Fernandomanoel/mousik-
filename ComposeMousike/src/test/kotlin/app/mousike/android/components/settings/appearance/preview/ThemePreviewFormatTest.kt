package app.mousike.android.components.settings.appearance.preview

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import app.mousike.android.enums.FontType
import app.mousike.android.ui.styling.typographyOf
import app.mousike.android.utils.fontTypeKey
import app.mousike.android.utils.actionExpandedKey
import app.mousike.android.utils.showNextSongsInPlayerKey
import app.mousike.android.utils.useSystemFontKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

import app.mousike.android.components.glass.GlassArea
import app.mousike.android.components.glass.GlassQuality
import app.mousike.android.components.glass.glassProfileDefaults
import app.mousike.android.components.glass.glassSettingsFromPreferences
import app.mousike.android.components.glass.glassEnabledKey
import app.mousike.android.utils.colorPaletteModeKey
import app.mousike.android.utils.colorPaletteNameKey
import app.mousike.android.utils.hideStatusBarKey
import app.mousike.android.utils.customThemeDark_Background0Key
import app.mousike.android.utils.lyricsFontSizeKey
import app.mousike.android.utils.lyricsSizeKey
import app.mousike.android.utils.lyricsTypeKey
import app.mousike.android.utils.miniPlayerButtonOrderKey
import app.mousike.android.utils.nowPlayingIndicatorKey
import app.mousike.android.utils.playlistindicatorKey
import app.mousike.android.utils.Preference
import app.mousike.android.utils.playerBackdropKey
import app.mousike.android.utils.floatActionIconOffsetXkey
import app.mousike.android.utils.showMiniPlayerAudioOutputKey
import app.mousike.android.utils.uiRoundnessDpKey
import app.mousike.android.utils.homeTabsOrderKey
import app.mousike.android.utils.homeSongsOrderKey
import app.mousike.android.utils.playerActionBarButtonOrderKey
import app.mousike.android.themed.mousike.screen.player.resolvePlayerActionBarOrder
import app.mousike.android.utils.thumbnailSizeDpKey
import app.mousike.android.utils.thumbnailSizeLDpKey
import app.mousike.android.utils.DataStoreUtils
import app.mousike.android.components.navigation.dock.DockToolbarPreferences
import app.mousike.android.components.glass.glassBlurKey
import app.mousike.android.components.glass.glassJellyEnabledKey
import app.mousike.android.utils.navigationBarPositionKey

class ThemePreviewFormatTest {
    @Test
    fun `system font preference delegates font choice to Android`() {
        val document = encodeThemeV1(
            mapOf(useSystemFontKey to true, fontTypeKey to FontType.Poppins.name),
            emptyMap<String, Any>(),
        )
        for (glass in listOf(false, true)) {
            val preferences = themePreviewPreferences(document, glass).plain
            val systemFont = preferences.getValue(useSystemFontKey) as Boolean
            val selectedFont = FontType.valueOf(preferences.getValue(fontTypeKey) as String)
            assertTrue(systemFont)
            assertEquals(FontType.Poppins, selectedFont)
            assertEquals(FontFamily.Default, typographyOf(Color.White, systemFont, false, selectedFont).m.fontFamily)
        }
    }

    @Test
    fun `v1 appearance maps to isolated preview values`() {
        val theme = parseThemeV1("""
            {"schemaVersion":1,"categories":{
              "colors":{"palette":"dynamic","mode":"system"},
              "navigation":{"position":"bottom-floating","labels":"icon-only"},
              "glass":{"enabled":true,"tintSource":"theme-accent","navigation":"glass-only",
                "miniPlayer":"disabled","nowPlaying":"glass-and-contour"}
            }}
        """.trimIndent())
        assertEquals("Dynamic", theme.getValue(ThemeCategory.COLORS).plain[colorPaletteNameKey])
        assertEquals("System", theme.getValue(ThemeCategory.COLORS).plain[colorPaletteModeKey])
        assertEquals("BottomFloating", theme.getValue(ThemeCategory.NAVIGATION).plain[navigationBarPositionKey])
        val glass = theme.getValue(ThemeCategory.GLASS)
        assertEquals("GlassOnly", glass.glass[GlassArea.Nav.preferenceKey])
        assertFalse(glass.plain.containsKey(GlassArea.Nav.preferenceKey))
    }

    @Test
    fun `v1 appearance rejects non appearance fields`() {
        assertThrows(IllegalArgumentException::class.java) {
            parseThemeV1("""{"schemaVersion":1,"categories":{"colors":{
                "palette":"dynamic","mode":"system","activeProfile":"other"
            }}}""")
        }
    }

    @Test
    fun `preview variants preserve every decoded setting except the Glass master switch`() {
        val document = encodeThemeV1(
            mapOf(hideStatusBarKey to true, glassEnabledKey to false),
            mapOf(GlassArea.Nav.preferenceKey to "GlassOnly", GlassArea.MiniPlayer.preferenceKey to "Disabled"),
        )
        val theme = parseThemeV1(document)
        val plain = theme.values.flatMap { it.plain.entries }.associate { it.toPair() }
        val optics = theme.getValue(ThemeCategory.GLASS).glass

        val normal = themePreviewPreferences(document, glass = false)
        val glass = themePreviewPreferences(document, glass = true)

        assertEquals(true, normal.plain[hideStatusBarKey])
        assertEquals(plain + (glassEnabledKey to false), normal.plain)
        assertEquals(plain + (glassEnabledKey to true), glass.plain)
        assertEquals(optics, normal.glass)
        assertEquals(optics, glass.glass)
    }

    @Test
    fun `empty preferences export creates an importable default theme`() {
        val document = encodeThemeV1(emptyMap<String, Any>(), emptyMap<String, Any>())
        val groups = parseThemeV1(document).toStoreGroups()
        assertEquals(StoreGroup.entries.toSet(), groups.keys)
        assertEquals("System", groups.getValue(StoreGroup.Colors).plain[colorPaletteModeKey])
        assertEquals(8f, groups.getValue(StoreGroup.PageLayout).plain[uiRoundnessDpKey])
        assertEquals("Auto", groups.getValue(StoreGroup.Lyrics).plain[lyricsTypeKey])
        assertEquals(true, groups.getValue(StoreGroup.PlayerAndQueue).plain[showMiniPlayerAudioOutputKey])
    }

    @Test
    fun `customization round trips through the same document used for previews`() {
        val plain = mapOf(
            colorPaletteNameKey to "Customized",
            customThemeDark_Background0Key to -101,
            uiRoundnessDpKey to 26f,
            nowPlayingIndicatorKey to "CrazyBars",
            playlistindicatorKey to true,
            playerBackdropKey to 32f,
            floatActionIconOffsetXkey to -63.25f,
            Preference.HOME_ARTIST_ITEM_SIZE.key to "BIG",
            Preference.SEARCH_RESULT_GRID_STATES.key to "1010101",
            lyricsFontSizeKey to "Medium",
            lyricsSizeKey to 42f,
            lyricsTypeKey to "Karaoke",
            actionExpandedKey to false,
            showNextSongsInPlayerKey to true,
            miniPlayerButtonOrderKey to """["lyrics","play_pause"]""",
            showMiniPlayerAudioOutputKey to false,
            "githubAccessToken" to "not-a-theme-setting",
            "prevNextSongs" to "onesong",
        )
        val glass = mapOf(
            glassBlurKey to 11f,
            glassJellyEnabledKey to false,
            GlassArea.Header.preferenceKey to "ContourOnly",
        )
        val document = encodeThemeV1(plain, glass)
        val preview = themePreviewPreferences(document, glass = true)
        assertEquals(plain - setOf("githubAccessToken", "prevNextSongs"), preview.plain.filterKeys { it in plain })
        assertEquals(glass, preview.glass.filterKeys { it in glass })
        assertFalse(document.contains("not-a-theme-setting"))
        assertEquals(plain.filterKeys { it in setOf(lyricsFontSizeKey, lyricsSizeKey, lyricsTypeKey, actionExpandedKey) },
            parseThemeV1(document).toStoreGroups().getValue(StoreGroup.Lyrics).plain.filterKeys { it in plain })
    }

    @Test
    fun `landscape cover follows portrait until explicitly customized`() {
        fun decode(plain: Map<String, Any>) =
            parseThemeV1(encodeThemeV1(plain, emptyMap<String, Any>()))
                .getValue(ThemeCategory.PLAYER_AND_QUEUE).plain

        assertEquals(UseThemeDefault, decode(mapOf(thumbnailSizeDpKey to 83f))[thumbnailSizeLDpKey])
        assertEquals(52f, decode(mapOf(thumbnailSizeDpKey to 83f, thumbnailSizeLDpKey to 52f))[thumbnailSizeLDpKey])
    }

    @Test
    fun `nested orders preserve app identifiers and repeated menu separators`() {
        val plain = mapOf(
            homeTabsOrderKey to """["playlists","songs"]""",
            DockToolbarPreferences.orderKey to """["musical_notes","cloud_download","time_outline"]""",
            homeSongsOrderKey to """["on_device","downloaded"]""",
            playerActionBarButtonOrderKey to """["add_to_playlist","lyrics"]""",
            DataStoreUtils.KEY_BURGER_MENU_ORDER to """["history","separator","profiles","separator","settings"]""",
            DataStoreUtils.burgerMenuToggleKey("history") to false,
        )
        val document = encodeThemeV1(plain, emptyMap<String, Any>())
        assertEquals(plain, themePreviewPreferences(document, false).plain.filterKeys { it in plain })
    }

    @Test
    fun `sparse previews fill from Stock without adding settings to import`() {
        val partial = """{"schemaVersion":1,"categories":{"colors":{"palette":"dynamic","mode":"light"}}}"""
        val imported = parseThemeV1(partial)
        val demo = themePreviewPreferences(partial, glass = true)

        assertEquals(setOf(ThemeCategory.COLORS), imported.keys)
        assertEquals("Light", demo.plain[colorPaletteModeKey])
        assertEquals(8f, demo.plain[uiRoundnessDpKey])
        assertEquals("1111111", demo.plain[Preference.SEARCH_RESULT_GRID_STATES.key])
        assertEquals("Auto", demo.plain[lyricsTypeKey])
        assertEquals(true, demo.plain[glassEnabledKey])
        assertEquals(emptyMap<String, Any>(), demo.glass)
    }

    @Test
    fun `sparse Glass previews keep the chosen quality defaults`() {
        val partial = """
            {"schemaVersion":1,"categories":{"glass":{"enabled":true,"tintSource":"none",
                "navigation":"glass-and-contour","miniPlayer":"glass-and-contour",
                "nowPlaying":"glass-and-contour","quality":"quality"}}}
        """.trimIndent()
        val demo = themePreviewPreferences(partial, glass = true)
        val settings = glassSettingsFromPreferences(demo.glass, GlassQuality.Balanced)

        assertEquals(GlassQuality.Quality, settings.quality)
        assertEquals(glassProfileDefaults(GlassQuality.Quality).blur, settings.blur)
    }

    @Test
    fun `default exported action order retains the live player toolbar`() {
        val partial = """{"schemaVersion":1,"categories":{"colors":{"palette":"dynamic","mode":"light"}}}"""
        val preview = themePreviewPreferences(partial, glass = true)
        val serialized = preview.plain.getValue(playerActionBarButtonOrderKey) as String

        assertEquals(resolvePlayerActionBarOrder(""), resolvePlayerActionBarOrder(serialized))
        assertTrue("add_to_playlist" in resolvePlayerActionBarOrder(serialized))
        assertEquals(listOf("lyrics", "shuffle"), resolvePlayerActionBarOrder("""["lyrics","shuffle"]"""))
    }
}
