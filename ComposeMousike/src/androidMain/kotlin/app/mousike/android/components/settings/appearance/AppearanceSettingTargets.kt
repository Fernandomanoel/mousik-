package app.mousike.android.components.settings.appearance

import app.mousike.android.components.dialog.settings.PlayerActionBarSettingsDialog
import app.mousike.android.components.dialog.settings.MiniPlayerButtonsSettingsDialog
import app.mousike.android.utils.actionExpandedKey
import app.mousike.android.utils.actionspacedevenlyKey
import app.mousike.android.utils.albumCoverRotationKey
import app.mousike.android.utils.animatedGradientKey
import app.mousike.android.utils.backgroundProgressKey
import app.mousike.android.utils.blackgradientKey
import app.mousike.android.utils.bottomgradientKey
import app.mousike.android.utils.buttonzoomoutKey
import app.mousike.android.utils.carouselKey
import app.mousike.android.utils.carouselSizeKey
import app.mousike.android.utils.clickOnLyricsTextKey
import app.mousike.android.utils.controlsExpandedKey
import app.mousike.android.utils.coverThumbnailAnimationKey
import app.mousike.android.utils.disablePlayerHorizontalSwipeKey
import app.mousike.android.utils.playerSwipeSensitivityKey
import app.mousike.android.utils.shakeSensitivityThemeKey
import app.mousike.android.utils.disableScrollingTextKey
import app.mousike.android.utils.effectRotationKey
import app.mousike.android.utils.fadingedgeKey
import app.mousike.android.utils.iconLikeTypeKey
import app.mousike.android.utils.miniQueueExpandedKey
import app.mousike.android.utils.noblurKey
import app.mousike.android.utils.notificationPlayerFirstIconKey
import app.mousike.android.utils.notificationPlayerSecondIconKey
import app.mousike.android.utils.playerBackgroundColorsKey
import app.mousike.android.utils.playerControlsTypeKey
import app.mousike.android.utils.playerControlsColorsKey
import app.mousike.android.utils.playerEnableLyricsPopupMessageKey
import app.mousike.android.utils.playerInfoShowIconsKey
import app.mousike.android.utils.playerInfoTypeKey
import app.mousike.android.utils.playerPlayButtonTypeKey
import app.mousike.android.utils.playerSwapControlsWithTimelineKey
import app.mousike.android.utils.playerTimelineSizeKey
import app.mousike.android.utils.playerTimelineTypeKey
import app.mousike.android.utils.playerTypeKey
import app.mousike.android.utils.queueDurationExpandedKey
import app.mousike.android.utils.queueTypeKey
import app.mousike.android.utils.showBackgroundLyricsKey
import app.mousike.android.utils.showCoverThumbnailAnimationKey
import app.mousike.android.utils.showLyricsStateKey
import app.mousike.android.utils.showNextSongsInPlayerKey
import app.mousike.android.utils.showRemainingSongTimeKey
import app.mousike.android.utils.showSkipTimeButtonsKey
import app.mousike.android.utils.showTopActionsBarKey
import app.mousike.android.utils.showTotalTimeQueueKey
import app.mousike.android.utils.showVisualizerStateKey
import app.mousike.android.utils.showalbumcoverKey
import app.mousike.android.utils.showlyricsthumbnailKey
import app.mousike.android.utils.showsongsKey
import app.mousike.android.utils.showthumbnailKey
import app.mousike.android.utils.showvisthumbnailKey
import app.mousike.android.utils.showVisualizerButtonsKey
import app.mousike.android.utils.blackBackgroundForVisThumbnailKey
import app.mousike.android.utils.statsExpandedKey
import app.mousike.android.utils.statsfornerdsKey
import app.mousike.android.utils.swipeAnimationsNoThumbnailKey
import app.mousike.android.utils.swipeUpQueueKey
import app.mousike.android.utils.tapqueueKey
import app.mousike.android.utils.textoutlineKey
import app.mousike.android.utils.thumbnailTapEnabledKey
import app.mousike.android.utils.thumbnailTypeKey
import app.mousike.android.utils.thumbnailpauseKey
import app.mousike.android.utils.cropVideoThumbnailsKey
import app.mousike.android.utils.timelineExpandedKey
import app.mousike.android.utils.titleExpandedKey
import app.mousike.android.utils.topPaddingKey
import app.mousike.android.utils.transparentBackgroundPlayerActionBarKey
import app.mousike.android.utils.transparentbarKey
import app.mousike.android.utils.visualizerEnabledKey
import app.mousike.android.utils.uiRoundnessDpKey
import app.mousike.android.utils.visualizerWhiteColorOptionKey
import app.mousike.android.utils.thumbnailSizeLDpKey
import app.mousike.android.utils.thumbnailSizeDpKey
import app.mousike.android.utils.lyricsColorKey
import app.mousike.android.utils.thumbnailRoundnessDpKey
import app.mousike.android.utils.artistThumbnailRoundnessDpKey
import app.mousike.android.utils.lyricsCustomColorKey
import app.mousike.android.utils.visualizerLineThicknessKey
import app.mousike.android.utils.visualizerCustomColorKey

/** Stable local inventory; global search activation is a separate change. */
internal object AppearanceSettingTargets {
    val cards = listOf(
        AppearanceCardTargets("appearance.card.presets", listOf(playerTypeKey, queueTypeKey, thumbnailTypeKey, playerInfoTypeKey, playerInfoShowIconsKey, playerControlsTypeKey, iconLikeTypeKey), emptyList()),
        AppearanceCardTargets("appearance.card.cover", listOf(showthumbnailKey, thumbnailSizeDpKey, thumbnailSizeLDpKey, thumbnailRoundnessDpKey, artistThumbnailRoundnessDpKey, uiRoundnessDpKey, thumbnailpauseKey, cropVideoThumbnailsKey, statsfornerdsKey, noblurKey, fadingedgeKey, carouselKey, carouselSizeKey, showCoverThumbnailAnimationKey, coverThumbnailAnimationKey, swipeAnimationsNoThumbnailKey), listOf()),
        AppearanceCardTargets("appearance.card.timeline", listOf(transparentbarKey, playerPlayButtonTypeKey, playerTimelineTypeKey, playerTimelineSizeKey, playerControlsColorsKey, iconLikeTypeKey, playerSwapControlsWithTimelineKey, buttonzoomoutKey), listOf("MiniPlayerButtonsSettingsDialog")),
        AppearanceCardTargets("appearance.card.colors", listOf(blackgradientKey, bottomgradientKey, textoutlineKey, showTotalTimeQueueKey, showRemainingSongTimeKey, showSkipTimeButtonsKey, playerBackgroundColorsKey, animatedGradientKey, shakeSensitivityThemeKey, albumCoverRotationKey), listOf()),
        AppearanceCardTargets("appearance.card.behavior", listOf(disablePlayerHorizontalSwipeKey, playerSwipeSensitivityKey, disableScrollingTextKey, visualizerEnabledKey, visualizerWhiteColorOptionKey, visualizerCustomColorKey, visualizerLineThicknessKey, effectRotationKey, backgroundProgressKey, showNextSongsInPlayerKey, showTopActionsBarKey, showvisthumbnailKey, blackBackgroundForVisThumbnailKey, showVisualizerButtonsKey, showsongsKey, showalbumcoverKey, showVisualizerStateKey, topPaddingKey), listOf()),
        AppearanceCardTargets("appearance.card.lyrics", listOf(showlyricsthumbnailKey, thumbnailTapEnabledKey, clickOnLyricsTextKey, lyricsColorKey, lyricsCustomColorKey, showBackgroundLyricsKey, playerEnableLyricsPopupMessageKey, showLyricsStateKey), listOf()),
        AppearanceCardTargets("appearance.card.actionBar", listOf(transparentBackgroundPlayerActionBarKey, actionspacedevenlyKey, tapqueueKey, swipeUpQueueKey), listOf("PlayerActionBarSettingsDialog")),
        AppearanceCardTargets("appearance.card.fullscreenLyrics", listOf(queueDurationExpandedKey, titleExpandedKey, timelineExpandedKey, controlsExpandedKey, miniQueueExpandedKey, statsExpandedKey, actionExpandedKey), listOf()),
        AppearanceCardTargets("appearance.card.notification", listOf(notificationPlayerFirstIconKey, notificationPlayerSecondIconKey), listOf()),
        AppearanceCardTargets("appearance.card.reset", listOf(), listOf("applyAppearanceDefaults")),
    )
    val ids = cards.flatMap { card ->
        listOf(card.id) + card.preferenceKeys.map { "${card.id}.setting.$it" } +
            card.actions.map { "${card.id}.action.$it" }
    }
}

internal data class AppearanceCardTargets(
    val id: String,
    val preferenceKeys: List<String>,
    val actions: List<String>,
)
