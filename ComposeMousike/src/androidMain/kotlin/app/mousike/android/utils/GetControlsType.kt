@file:OptIn(UnstableApi::class)

package app.mousike.android.utils

import androidx.media3.common.Player
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.mousike.android.components.player.playerTransportLayout
import app.mousike.android.components.player.PlayerTransportCenter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import app.mousike.android.enums.PlayerBackgroundColors
import app.mousike.android.enums.PlayerControlsType
import app.mousike.android.enums.PlayerPlayButtonType
import app.mousike.android.playback.services.PlayerServiceModern
import app.mousike.android.components.menu.player.PlaybackSettingsMenu
import app.mousike.android.ui.saveable.TolerantBoolStateSaver
import app.mousike.android.enums.MenuStyle
import app.mousike.android.ui.components.LocalMenuState
import app.mousike.android.ui.screens.player.components.controls.ControlsEssential
import app.mousike.android.ui.screens.player.components.controls.ControlsModern
import kotlin.math.roundToInt
import app.mousike.android.ui.styling.ColorPalette

@Composable
fun GetControls(
    binder: PlayerServiceModern.Binder?,
    player: Player,
    position: () -> Long,
    shouldBePlaying: Boolean,
    isBuffering: Boolean,
    likedAt: Long?,
    mediaId: String,
    onBlurScaleChange: (Float) -> Unit,
    dynamicColorPalette: ColorPalette,
    timelineHorizontalInset: Dp = 0.dp,
) {
    val playerControlsType by rememberPreference(playerControlsTypeKey, PlayerControlsType.Essential)
    val playerPlayButtonType by rememberPreference(
        playerPlayButtonTypeKey,
        PlayerPlayButtonType.CircularRibbed
    )
    // Issue #881 (gh-881): tolerant saver (spec M5).
    var isRotated by rememberSaveable(saver = TolerantBoolStateSaver("getControls.isRotated")) { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isRotated) 360F else 0f,
        animationSpec = tween(durationMillis = 200), label = ""
    )
    val playerBackgroundColors by rememberPreference(
        playerBackgroundColorsKey,
        PlayerBackgroundColors.AnimatedGradient
    )

    val isGradientBackgroundEnabled = playerBackgroundColors == PlayerBackgroundColors.ThemeColorGradient ||
            playerBackgroundColors == PlayerBackgroundColors.CoverColorGradient

    var playbackSpeed by rememberPreference(playbackSpeedKey, 1f)
    var playbackDuration by rememberPreference(playbackDurationKey, 0f)
    var setPlaybackDuration by remember { mutableStateOf(false) }

    val menuState = LocalMenuState.current
    val menuStyle = rememberPreference(menuStyleKey, MenuStyle.List)

    val playbackSettingsMenu = remember(binder, menuState, menuStyle) {
        binder?.let { PlaybackSettingsMenu.create(
            binder = binder,
            menuState = menuState,
            styleState = menuStyle,
            onDismiss = { menuState.hide() },
            onBlurScaleChange = onBlurScaleChange
        ) }
    }


        if (binder != null) MedleyMode(
            binder = binder,
            seconds = if (playbackDuration < 1f) 0 else playbackDuration.roundToInt()
        )

    PlayerTransportCenter {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .playerTransportLayout(timelineHorizontalInset)
            .fillMaxWidth()
    ) {

        if (playerControlsType == PlayerControlsType.Essential)
            ControlsEssential(
                binder = binder,
                player = player,
                position = position,
                playbackSpeed = playbackSpeed,
                shouldBePlaying = shouldBePlaying,
                isBuffering = isBuffering,
                likedAt = likedAt,
                mediaId = mediaId,
                playerPlayButtonType = playerPlayButtonType,
                isGradientBackgroundEnabled = isGradientBackgroundEnabled,
                onShowSpeedPlayerDialog = { playbackSettingsMenu?.let { menuState.display { it.MenuComponent() } } },
                dynamicColorPalette = dynamicColorPalette
            )

        if (playerControlsType == PlayerControlsType.Modern)
            ControlsModern(
                binder = binder,
                player = player,
                position = position,
                playbackSpeed = playbackSpeed,
                shouldBePlaying = shouldBePlaying,
                isBuffering = isBuffering,
                playerPlayButtonType = playerPlayButtonType,
                isGradientBackgroundEnabled = isGradientBackgroundEnabled,
                onShowSpeedPlayerDialog = { playbackSettingsMenu?.let { menuState.display { it.MenuComponent() } } },
                dynamicColorPalette = dynamicColorPalette
            )
    }
    }
}


