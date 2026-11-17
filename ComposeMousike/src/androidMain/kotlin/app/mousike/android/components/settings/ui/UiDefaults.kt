package app.mousike.android.components.settings.ui

import app.mousike.android.utils.effectRotationDefault
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.edit
import app.mousike.android.enums.AlbumSwipeAction
import app.mousike.android.enums.BackgroundProgress
import app.mousike.android.enums.CarouselSize
import app.mousike.android.enums.ColorPaletteMode
import app.mousike.android.enums.ColorPaletteName
import app.mousike.android.enums.FontType
import app.mousike.android.enums.HomeScreenTabs
import app.mousike.android.enums.IconLikeType
import app.mousike.android.enums.MenuStyle
import app.mousike.android.enums.MiniPlayerType
import app.mousike.android.enums.NavigationBarPosition
import app.mousike.android.enums.NavigationBarType
import app.mousike.android.enums.PlayerBackgroundColors
import app.mousike.android.enums.PlayerControlsType
import app.mousike.android.enums.PlayerInfoType
import app.mousike.android.enums.PlayerPlayButtonType
import app.mousike.android.enums.PlayerPosition
import app.mousike.android.enums.PlayerTimelineSize
import app.mousike.android.enums.PlayerTimelineType
import app.mousike.android.enums.PlayerType
import app.mousike.android.enums.PlaylistSwipeAction
import app.mousike.android.enums.QueueSwipeAction
import app.mousike.android.enums.QueueType
import app.mousike.android.enums.SearchDisplayOrder
import app.mousike.android.enums.ThumbnailType
import app.mousike.android.enums.TransitionEffect
import app.mousike.android.ui.styling.DefaultDarkColorPalette
import app.mousike.android.ui.styling.DefaultLightColorPalette
import app.mousike.android.utils.actionspacedevenlyKey
import app.mousike.android.utils.albumSwipeLeftActionKey
import app.mousike.android.utils.albumSwipeRightActionKey
import app.mousike.android.utils.applyFontPaddingKey
import app.mousike.android.utils.backgroundProgressKey
import app.mousike.android.utils.blackgradientKey
import app.mousike.android.utils.buttonzoomoutKey
import app.mousike.android.utils.carouselKey
import app.mousike.android.utils.carouselSizeKey
import app.mousike.android.utils.clickOnLyricsTextKey
import app.mousike.android.utils.colorPaletteModeKey
import app.mousike.android.utils.colorPaletteNameKey
import app.mousike.android.utils.customColorKey
import app.mousike.android.utils.customThemeDark_Background0Key
import app.mousike.android.utils.customThemeDark_Background1Key
import app.mousike.android.utils.customThemeDark_Background2Key
import app.mousike.android.utils.customThemeDark_Background3Key
import app.mousike.android.utils.customThemeDark_Background4Key
import app.mousike.android.utils.customThemeDark_TextKey
import app.mousike.android.utils.customThemeDark_accentKey
import app.mousike.android.utils.customThemeDark_iconButtonPlayerKey
import app.mousike.android.utils.customThemeDark_textDisabledKey
import app.mousike.android.utils.customThemeDark_textSecondaryKey
import app.mousike.android.utils.customThemeLight_Background0Key
import app.mousike.android.utils.customThemeLight_Background1Key
import app.mousike.android.utils.customThemeLight_Background2Key
import app.mousike.android.utils.customThemeLight_Background3Key
import app.mousike.android.utils.customThemeLight_Background4Key
import app.mousike.android.utils.customThemeLight_TextKey
import app.mousike.android.utils.customThemeLight_accentKey
import app.mousike.android.utils.customThemeLight_iconButtonPlayerKey
import app.mousike.android.utils.customThemeLight_textDisabledKey
import app.mousike.android.utils.customThemeLight_textSecondaryKey
import app.mousike.android.utils.disableIconButtonOnTopKey
import app.mousike.android.utils.disableNavigationBackStackKey
import app.mousike.android.utils.disablePlayerHorizontalSwipeKey
import app.mousike.android.utils.disableScrollingTextKey
import app.mousike.android.utils.effectRotationKey
import app.mousike.android.utils.expandedplayertoggleKey
import app.mousike.android.utils.fadingedgeKey
import app.mousike.android.utils.fontTypeKey
import app.mousike.android.utils.hideStatusBarKey
import app.mousike.android.utils.rightToLeftInterfaceKey
import app.mousike.android.utils.iconLikeTypeKey
import app.mousike.android.utils.indexNavigationTabKey
import app.mousike.android.utils.isSwipeToActionEnabledKey
import app.mousike.android.utils.keepPlayerMinimizedKey
import app.mousike.android.utils.lastPlayerPlayButtonTypeKey
import app.mousike.android.utils.lastPlayerTimelineTypeKey
import app.mousike.android.utils.lastThumbnailSizeDpKey
import app.mousike.android.utils.menuStyleKey
import app.mousike.android.utils.miniPlayerTypeKey
import app.mousike.android.utils.navigationBarPositionKey
import app.mousike.android.utils.navigationBarTypeKey
import app.mousike.android.utils.playerBackgroundColorsKey
import app.mousike.android.utils.playerControlsTypeKey
import app.mousike.android.utils.playerEnableLyricsPopupMessageKey
import app.mousike.android.utils.playerInfoShowIconsKey
import app.mousike.android.utils.playerInfoTypeKey
import app.mousike.android.utils.playerPlayButtonTypeKey
import app.mousike.android.utils.playerPositionKey
import app.mousike.android.utils.playerSwapControlsWithTimelineKey
import app.mousike.android.utils.playerTimelineSizeKey
import app.mousike.android.utils.playerTimelineTypeKey
import app.mousike.android.utils.playerTypeKey
import app.mousike.android.utils.playlistSwipeLeftActionKey
import app.mousike.android.utils.playlistSwipeRightActionKey
import app.mousike.android.utils.preferences
import app.mousike.android.utils.putEnum
import app.mousike.android.utils.queueSwipeLeftActionKey
import app.mousike.android.utils.queueSwipeRightActionKey
import app.mousike.android.utils.queueTypeKey
import app.mousike.android.utils.rememberQueueScrollPositionKey
import app.mousike.android.utils.searchDisplayOrderKey
import app.mousike.android.utils.showButtonPlayerAddToPlaylistKey
import app.mousike.android.utils.showButtonPlayerArrowKey
import app.mousike.android.utils.showButtonPlayerDiscoverKey
import app.mousike.android.utils.showButtonPlayerDownloadKey
import app.mousike.android.utils.showButtonPlayerLoopKey
import app.mousike.android.utils.showButtonPlayerLyricsKey
import app.mousike.android.utils.showButtonPlayerMenuKey
import app.mousike.android.utils.showButtonPlayerShuffleKey
import app.mousike.android.utils.showButtonPlayerSleepTimerKey
import app.mousike.android.utils.showButtonPlayerSystemEqualizerKey
import app.mousike.android.utils.showCachedPlaylistKey
import app.mousike.android.utils.showDownloadedPlaylistKey
import app.mousike.android.utils.showFavoritesPlaylistKey
import app.mousike.android.utils.showFloatingIconKey
import app.mousike.android.utils.showNextSongsInPlayerKey
import app.mousike.android.utils.showOnDevicePlaylistKey
import app.mousike.android.utils.showPinnedPlaylistsKey
import app.mousike.android.utils.showRemainingSongTimeKey
import app.mousike.android.utils.showSearchTabKey
import app.mousike.android.utils.showStatsInNavbarKey
import app.mousike.android.utils.showTopActionsBarKey
import app.mousike.android.utils.showTotalTimeQueueKey
import app.mousike.android.utils.showthumbnailKey
import app.mousike.android.utils.swipeUpQueueKey
import app.mousike.android.utils.tapqueueKey
import app.mousike.android.utils.thumbnailSizeDpKey
import app.mousike.android.utils.thumbnailSizeLDpKey
import app.mousike.android.utils.thumbnailTapEnabledKey
import app.mousike.android.utils.thumbnailTypeKey
import app.mousike.android.utils.transitionEffectKey
import app.mousike.android.utils.transparentBackgroundPlayerActionBarKey
import app.mousike.android.utils.useSystemFontKey
import app.mousike.android.utils.visualizerEnabledKey
import app.mousike.android.components.dialog.settings.BurgerMenuSettingsDialog
import app.mousike.android.components.dialog.settings.HistorySortSettingsDialog
import app.mousike.android.components.dialog.settings.HomeAlbumsSettingsDialog
import app.mousike.android.components.dialog.settings.HomeAlbumsSortSettingsDialog
import app.mousike.android.components.dialog.settings.HomeAlbumsToolbarSettingsDialog
import app.mousike.android.components.dialog.settings.HomeArtistsSettingsDialog
import app.mousike.android.components.dialog.settings.HomeArtistsSortSettingsDialog
import app.mousike.android.components.dialog.settings.HomeArtistsToolbarSettingsDialog
import app.mousike.android.components.dialog.settings.HomeLibrarySortSettingsDialog
import app.mousike.android.components.dialog.settings.HomeLibraryToolbarSettingsDialog
import app.mousike.android.components.dialog.settings.HomePlaylistsSettingsDialog
import app.mousike.android.components.dialog.settings.HomeSongsSettingsDialog
import app.mousike.android.components.dialog.settings.HomeSongsSortSettingsDialog
import app.mousike.android.components.dialog.settings.HomeSongsToolbarSettingsDialog
import app.mousike.android.components.dialog.settings.HomeTabsSettingsDialog
import app.mousike.android.components.dialog.settings.LocalPlaylistSortSettingsDialog
import app.mousike.android.components.dialog.settings.LocalPlaylistToolbarSettingsDialog
import app.mousike.android.components.dialog.settings.MiniPlayerButtonsSettingsDialog
import app.mousike.android.components.dialog.settings.PlayerActionBarSettingsDialog
import app.mousike.android.components.dialog.settings.QuickPicksContentSettingsDialog
import app.mousike.android.components.settings.common.resetInterfaceReaderDefaults
import app.mousike.android.components.theme.DYNAMIC_THEME_RESPECT_BRIGHTNESS_DEFAULT
import app.mousike.android.components.theme.dynamicThemeRespectBrightnessKey
import app.mousike.android.core.settings.DEFAULT_THUMBNAIL_SIZE_DP

/** Applies the legacy Interface reset once to the active profile and dialog stores. */
internal fun applyUiDefaults(context: Context) {
    resetInterfaceReaderDefaults(context.preferences)
    context.preferences.edit().remove(app.mousike.android.components.navigation.dock.DockToolbarPreferences.orderKey).apply()
    context.preferences.edit {
        putBoolean(hideStatusBarKey, false)
        putBoolean(rightToLeftInterfaceKey, false)
        putBoolean(keepPlayerMinimizedKey, true)
        putBoolean(disableIconButtonOnTopKey, false)
        putEnum(lastPlayerTimelineTypeKey, PlayerTimelineType.Default)
        putFloat(lastThumbnailSizeDpKey, 30f)
        putBoolean(disablePlayerHorizontalSwipeKey, false)
        putEnum(lastPlayerPlayButtonTypeKey, PlayerPlayButtonType.Rectangular)
        putEnum(colorPaletteNameKey, ColorPaletteName.PureBlack)
        putBoolean(dynamicThemeRespectBrightnessKey, DYNAMIC_THEME_RESPECT_BRIGHTNESS_DEFAULT)
        putEnum(colorPaletteModeKey, ColorPaletteMode.System)
        putEnum(indexNavigationTabKey, HomeScreenTabs.Default)
        putEnum(fontTypeKey, FontType.Rubik)
        putBoolean(useSystemFontKey, false)
        putBoolean(applyFontPaddingKey, false)
        putBoolean(isSwipeToActionEnabledKey, true)
        putBoolean(showSearchTabKey, false)
        putBoolean(showStatsInNavbarKey, false)
        putEnum(navigationBarPositionKey, NavigationBarPosition.BottomFloating)
        putEnum(navigationBarTypeKey, NavigationBarType.IconOnly)
        putBoolean(showFavoritesPlaylistKey, true)
        putBoolean(showDownloadedPlaylistKey, true)
        putBoolean(showOnDevicePlaylistKey, true)
        putBoolean(showFloatingIconKey, false)
        putEnum(menuStyleKey, MenuStyle.List)
        putEnum(transitionEffectKey, TransitionEffect.Fade)
        putBoolean(showPinnedPlaylistsKey, true)
        putInt(customThemeLight_Background0Key, DefaultLightColorPalette.background0.hashCode())
        putInt(customThemeLight_Background1Key, DefaultLightColorPalette.background1.hashCode())
        putInt(customThemeLight_Background2Key, DefaultLightColorPalette.background2.hashCode())
        putInt(customThemeLight_Background3Key, DefaultLightColorPalette.background3.hashCode())
        putInt(customThemeLight_Background4Key, DefaultLightColorPalette.background4.hashCode())
        putInt(customThemeLight_TextKey, DefaultLightColorPalette.text.hashCode())
        putInt(customThemeLight_textSecondaryKey, DefaultLightColorPalette.textSecondary.hashCode())
        putInt(customThemeLight_textDisabledKey, DefaultLightColorPalette.textDisabled.hashCode())
        putInt(customThemeLight_iconButtonPlayerKey, DefaultLightColorPalette.iconButtonPlayer.hashCode())
        putInt(customThemeLight_accentKey, DefaultLightColorPalette.accent.hashCode())
        putInt(customThemeDark_Background0Key, DefaultDarkColorPalette.background0.hashCode())
        putInt(customThemeDark_Background1Key, DefaultDarkColorPalette.background1.hashCode())
        putInt(customThemeDark_Background2Key, DefaultDarkColorPalette.background2.hashCode())
        putInt(customThemeDark_Background3Key, DefaultDarkColorPalette.background3.hashCode())
        putInt(customThemeDark_Background4Key, DefaultDarkColorPalette.background4.hashCode())
        putInt(customThemeDark_TextKey, DefaultDarkColorPalette.text.hashCode())
        putInt(customThemeDark_textSecondaryKey, DefaultDarkColorPalette.textSecondary.hashCode())
        putInt(customThemeDark_textDisabledKey, DefaultDarkColorPalette.textDisabled.hashCode())
        putInt(customThemeDark_iconButtonPlayerKey, DefaultDarkColorPalette.iconButtonPlayer.hashCode())
        putInt(customThemeDark_accentKey, DefaultDarkColorPalette.accent.hashCode())
        putEnum(playerPositionKey, PlayerPosition.Bottom)
        putBoolean(showTopActionsBarKey, true)
        putEnum(playerControlsTypeKey, PlayerControlsType.Essential)
        putEnum(playerInfoTypeKey, PlayerInfoType.Modern)
        putEnum(playerTypeKey, PlayerType.Essential)
        putEnum(queueTypeKey, QueueType.Modern)
        putBoolean(fadingedgeKey, false)
        putBoolean(carouselKey, true)
        putEnum(carouselSizeKey, CarouselSize.Biggest)
        putEnum(thumbnailTypeKey, ThumbnailType.Modern)
        putEnum(playerTimelineTypeKey, PlayerTimelineType.Wavy)
        putFloat(thumbnailSizeDpKey, DEFAULT_THUMBNAIL_SIZE_DP)
        remove(thumbnailSizeLDpKey)
        putEnum(playerTimelineSizeKey, PlayerTimelineSize.Biggest)
        putBoolean(playerInfoShowIconsKey, true)
        putEnum(miniPlayerTypeKey, MiniPlayerType.Essential)
        putBoolean(playerSwapControlsWithTimelineKey, false)
        putEnum(playerPlayButtonTypeKey, PlayerPlayButtonType.CircularRibbed)
        putBoolean(buttonzoomoutKey, true)
        putEnum(iconLikeTypeKey, IconLikeType.Essential)
        putEnum(playerBackgroundColorsKey, PlayerBackgroundColors.AnimatedGradient)
        putBoolean(blackgradientKey, false)
        putBoolean(showTotalTimeQueueKey, true)
        putBoolean(showNextSongsInPlayerKey, false)
        putBoolean(showRemainingSongTimeKey, true)
        putBoolean(disableScrollingTextKey, false)
        putBoolean(effectRotationKey, effectRotationDefault)
        putBoolean(thumbnailTapEnabledKey, true)
        putBoolean(clickOnLyricsTextKey, true)
        putEnum(backgroundProgressKey, BackgroundProgress.MiniPlayer)
        putBoolean(transparentBackgroundPlayerActionBarKey, true)
        putBoolean(actionspacedevenlyKey, true)
        putBoolean(tapqueueKey, true)
        putBoolean(swipeUpQueueKey, true)
        putBoolean(showButtonPlayerAddToPlaylistKey, true)
        putBoolean(showButtonPlayerArrowKey, true)
        putBoolean(showButtonPlayerDownloadKey, true)
        putBoolean(showButtonPlayerLoopKey, false)
        putBoolean(showButtonPlayerLyricsKey, true)
        putBoolean(expandedplayertoggleKey, false)
        putBoolean(showButtonPlayerShuffleKey, true)
        putBoolean(showButtonPlayerSleepTimerKey, false)
        putBoolean(showButtonPlayerMenuKey, false)
        putBoolean(showButtonPlayerSystemEqualizerKey, false)
        putEnum(queueSwipeLeftActionKey, QueueSwipeAction.RemoveFromQueue)
        putEnum(queueSwipeRightActionKey, QueueSwipeAction.PlayNext)
        putEnum(playlistSwipeLeftActionKey, PlaylistSwipeAction.Favourite)
        putEnum(playlistSwipeRightActionKey, PlaylistSwipeAction.PlayNext)
        putEnum(albumSwipeLeftActionKey, AlbumSwipeAction.PlayNext)
        putEnum(albumSwipeRightActionKey, AlbumSwipeAction.Bookmark)
        putBoolean(showButtonPlayerDiscoverKey, false)
        putBoolean(playerEnableLyricsPopupMessageKey, true)
        putBoolean(visualizerEnabledKey, false)
        putBoolean(showthumbnailKey, true)
        putEnum(searchDisplayOrderKey, SearchDisplayOrder.SuggestionsFirst)
        putInt(customColorKey, Color.Green.hashCode())
        putBoolean(disableNavigationBackStackKey, false)
        putBoolean(showCachedPlaylistKey, true)
        putBoolean(rememberQueueScrollPositionKey, true)
    }
    HomeTabsSettingsDialog.reset(context)
    HomeSongsSettingsDialog.reset(context)
    HomeArtistsSettingsDialog.reset(context)
    HomeAlbumsSettingsDialog.reset(context)
    HomePlaylistsSettingsDialog.reset(context)
    QuickPicksContentSettingsDialog.reset(context)
    HomeSongsToolbarSettingsDialog.reset(context)
    HomeArtistsToolbarSettingsDialog.reset(context)
    HomeAlbumsToolbarSettingsDialog.reset(context)
    HomeLibraryToolbarSettingsDialog.reset(context)
    LocalPlaylistToolbarSettingsDialog.reset(context)
    BurgerMenuSettingsDialog.reset(context)
    PlayerActionBarSettingsDialog.reset(context)
    MiniPlayerButtonsSettingsDialog.reset(context)
    HomeSongsSortSettingsDialog.reset(context)
    HomeAlbumsSortSettingsDialog.reset(context)
    HomeArtistsSortSettingsDialog.reset(context)
    HomeLibrarySortSettingsDialog.reset(context)
    LocalPlaylistSortSettingsDialog.reset(context)
    HistorySortSettingsDialog.reset(context)
}

/** Restores only the selected custom palette in the active profile. */
internal fun resetUiPalette(context: Context, dark: Boolean) {
    context.preferences.edit {
        if (dark) {
            putInt(customThemeDark_Background0Key, DefaultDarkColorPalette.background0.hashCode())
            putInt(customThemeDark_Background1Key, DefaultDarkColorPalette.background1.hashCode())
            putInt(customThemeDark_Background2Key, DefaultDarkColorPalette.background2.hashCode())
            putInt(customThemeDark_Background3Key, DefaultDarkColorPalette.background3.hashCode())
            putInt(customThemeDark_Background4Key, DefaultDarkColorPalette.background4.hashCode())
            putInt(customThemeDark_TextKey, DefaultDarkColorPalette.text.hashCode())
            putInt(customThemeDark_textSecondaryKey, DefaultDarkColorPalette.textSecondary.hashCode())
            putInt(customThemeDark_textDisabledKey, DefaultDarkColorPalette.textDisabled.hashCode())
            putInt(customThemeDark_iconButtonPlayerKey, DefaultDarkColorPalette.iconButtonPlayer.hashCode())
            putInt(customThemeDark_accentKey, DefaultDarkColorPalette.accent.hashCode())
        } else {
            putInt(customThemeLight_Background0Key, DefaultLightColorPalette.background0.hashCode())
            putInt(customThemeLight_Background1Key, DefaultLightColorPalette.background1.hashCode())
            putInt(customThemeLight_Background2Key, DefaultLightColorPalette.background2.hashCode())
            putInt(customThemeLight_Background3Key, DefaultLightColorPalette.background3.hashCode())
            putInt(customThemeLight_Background4Key, DefaultLightColorPalette.background4.hashCode())
            putInt(customThemeLight_TextKey, DefaultLightColorPalette.text.hashCode())
            putInt(customThemeLight_textSecondaryKey, DefaultLightColorPalette.textSecondary.hashCode())
            putInt(customThemeLight_textDisabledKey, DefaultLightColorPalette.textDisabled.hashCode())
            putInt(customThemeLight_iconButtonPlayerKey, DefaultLightColorPalette.iconButtonPlayer.hashCode())
            putInt(customThemeLight_accentKey, DefaultLightColorPalette.accent.hashCode())
        }
    }
}
