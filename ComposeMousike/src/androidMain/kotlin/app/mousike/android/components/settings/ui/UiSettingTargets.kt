package app.mousike.android.components.settings.ui

import app.mousike.android.utils.albumSwipeLeftActionKey
import app.mousike.android.utils.albumSwipeRightActionKey
import app.mousike.android.utils.applyFontPaddingKey
import app.mousike.android.utils.colorPaletteModeKey
import app.mousike.android.utils.colorPaletteNameKey
import app.mousike.android.utils.customColorKey
import app.mousike.android.utils.customThemeDark_accentKey
import app.mousike.android.utils.customThemeDark_Background0Key
import app.mousike.android.utils.customThemeDark_Background1Key
import app.mousike.android.utils.customThemeDark_Background2Key
import app.mousike.android.utils.customThemeDark_Background3Key
import app.mousike.android.utils.customThemeDark_Background4Key
import app.mousike.android.utils.customThemeDark_iconButtonPlayerKey
import app.mousike.android.utils.customThemeDark_textDisabledKey
import app.mousike.android.utils.customThemeDark_TextKey
import app.mousike.android.utils.customThemeDark_textSecondaryKey
import app.mousike.android.utils.customThemeLight_accentKey
import app.mousike.android.utils.customThemeLight_Background0Key
import app.mousike.android.utils.customThemeLight_Background1Key
import app.mousike.android.utils.customThemeLight_Background2Key
import app.mousike.android.utils.customThemeLight_Background3Key
import app.mousike.android.utils.customThemeLight_Background4Key
import app.mousike.android.utils.customThemeLight_iconButtonPlayerKey
import app.mousike.android.utils.customThemeLight_textDisabledKey
import app.mousike.android.utils.customThemeLight_TextKey
import app.mousike.android.utils.customThemeLight_textSecondaryKey
import app.mousike.android.utils.disableNavigationBackStackKey
import app.mousike.android.utils.fontTypeKey
import app.mousike.android.utils.hideStatusBarKey
import app.mousike.android.utils.rightToLeftInterfaceKey
import app.mousike.android.utils.indexNavigationTabKey
import app.mousike.android.utils.isSwipeToActionEnabledKey
import app.mousike.android.utils.menuStyleKey
import app.mousike.android.utils.navigationBarPositionKey
import app.mousike.android.utils.navigationBarTypeKey
import app.mousike.android.utils.playerPositionKey
import app.mousike.android.utils.playlistSwipeLeftActionKey
import app.mousike.android.utils.playlistSwipeRightActionKey
import app.mousike.android.utils.queueSwipeLeftActionKey
import app.mousike.android.utils.queueSwipeRightActionKey
import app.mousike.android.utils.rememberQueueScrollPositionKey
import app.mousike.android.utils.searchDisplayOrderKey
import app.mousike.android.utils.showFloatingIconKey
import app.mousike.android.utils.transitionEffectKey
import app.mousike.android.utils.useSystemFontKey
import app.mousike.android.components.theme.dynamicThemeRespectBrightnessKey

/** Local inventory only; it does not activate global search or change navigation. */
internal object UiSettingTargets {
    val cards: List<UiCardTargets> = listOf(
        UiCardTargets("ui.card.theme",
            preferenceKeys = listOf(
                colorPaletteNameKey,
                dynamicThemeRespectBrightnessKey,
                customColorKey,
                customThemeLight_Background0Key,
                customThemeLight_Background1Key,
                customThemeLight_Background2Key,
                customThemeLight_Background3Key,
                customThemeLight_Background4Key,
                customThemeLight_TextKey,
                customThemeLight_textSecondaryKey,
                customThemeLight_textDisabledKey,
                customThemeLight_iconButtonPlayerKey,
                customThemeLight_accentKey,
                customThemeDark_Background0Key,
                customThemeDark_Background1Key,
                customThemeDark_Background2Key,
                customThemeDark_Background3Key,
                customThemeDark_Background4Key,
                customThemeDark_TextKey,
                customThemeDark_textSecondaryKey,
                customThemeDark_textDisabledKey,
                customThemeDark_iconButtonPlayerKey,
                customThemeDark_accentKey,
                colorPaletteModeKey,
            ),
            actions = listOf(
                "resetLightPalette",
                "resetDarkPalette",
            ),
        ),
        UiCardTargets("ui.card.interface",
            preferenceKeys = listOf(
                rightToLeftInterfaceKey,
                navigationBarPositionKey,
                navigationBarTypeKey,
                playerPositionKey,
                menuStyleKey,
                indexNavigationTabKey,
                transitionEffectKey,
                disableNavigationBackStackKey,
                searchDisplayOrderKey,
                showFloatingIconKey,
                rememberQueueScrollPositionKey,
            ),
            actions = listOf(
                "HomeTabsSettingsDialog",
                "searchHistoryPages",
            ),
        ),
        UiCardTargets("ui.card.font",
            preferenceKeys = listOf(
                fontTypeKey,
                useSystemFontKey,
                applyFontPaddingKey,
                hideStatusBarKey,
            ),
        ),
        UiCardTargets("ui.card.swipe",
            preferenceKeys = listOf(
                isSwipeToActionEnabledKey,
                queueSwipeLeftActionKey,
                playlistSwipeLeftActionKey,
                playlistSwipeRightActionKey,
                albumSwipeLeftActionKey,
                albumSwipeRightActionKey,
                queueSwipeRightActionKey,
            ),
        ),
        UiCardTargets("ui.card.home-tabs",
            actions = listOf(
                "QuickPicksContentSettingsDialog",
                "HomeSongsSettingsDialog",
                "HomeArtistsSettingsDialog",
                "HomeAlbumsSettingsDialog",
                "HomePlaylistsSettingsDialog",
            ),
        ),
        UiCardTargets("ui.card.sort-menus",
            actions = listOf(
                "HomeSongsSortSettingsDialog",
                "HomeArtistsSortSettingsDialog",
                "HomeAlbumsSortSettingsDialog",
                "HomeLibrarySortSettingsDialog",
                "LocalPlaylistSortSettingsDialog",
                "HistorySortSettingsDialog",
            ),
        ),
        UiCardTargets("ui.card.floating-dock",
            preferenceKeys = listOf(
                app.mousike.android.components.navigation.dock.DockToolbarPreferences.orderKey,
            ),
            actions = listOf("DockToolbarSettingsDialog"),
        ),
        UiCardTargets("ui.card.toolbars",
            actions = listOf(
                "HomeSongsToolbarSettingsDialog",
                "HomeArtistsToolbarSettingsDialog",
                "HomeAlbumsToolbarSettingsDialog",
                "HomeLibraryToolbarSettingsDialog",
                "LocalPlaylistToolbarSettingsDialog",
            ),
        ),
        UiCardTargets("ui.card.burger",
            actions = listOf(
                "BurgerMenuSettingsDialog",
            ),
        ),
        UiCardTargets("ui.card.reset",
            actions = listOf(
                "applyUiDefaults",
            ),
        ),
    )
    val ids: List<String> = cards.flatMap { card ->
        listOf(card.id) + card.preferenceKeys.map { "${card.id}.setting.$it" } +
            card.actions.map { "${card.id}.action.$it" }
    }
}

/** Semantic keys and action names remain stable across translations and display order. */
internal data class UiCardTargets(
    val id: String,
    val preferenceKeys: List<String> = emptyList(),
    val actions: List<String> = emptyList(),
)
