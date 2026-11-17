package app.mousike.android.components.player

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.staticCompositionLocalOf
import app.mousike.android.ui.screens.player.PlayerSheetState
import app.mousike.android.components.glass.GLASS_OPTICS_FADE_MILLIS
import app.mousike.android.components.glass.GlassSettings
import app.mousike.android.components.glass.GlassRenderer
import app.mousike.android.components.glass.glassRenderer
import app.mousike.android.components.glass.glassOpticsGateProgress
import app.mousike.android.components.glass.backdrop.Backdrop

/** One clock owns the deploy plate and all player optical consumers. */
val LocalPlayerSheetGlassTransition = staticCompositionLocalOf<State<Float>?> { null }

@Composable
fun rememberPlayerSheetGlassTransition(state: PlayerSheetState): State<Float> =
    animateFloatAsState(if (state.isExpanded) 1f else 0f,
        tween(GLASS_OPTICS_FADE_MILLIS), label = "playerSheetGlass")

/** Capture stays alive until the last consumer's deployment fade has ended. */
@Composable
fun playerGlassSourceCaptureActive(settings: GlassSettings, transition: State<Float>,
    inheritedOpticsEnabled: Boolean): Boolean {
    val progress = transition.value * glassOpticsGateProgress(inheritedOpticsEnabled)
    return settings.enabled && glassRenderer(settings) != GlassRenderer.Tint && progress > 0f
}

/** Modal owners retain the completed expanded player independently of its control optics budget. */
fun playerGlassSceneCaptureActive(settings: GlassSettings, expanded: Boolean,
    backgroundCapture: Boolean): Boolean = backgroundCapture || (settings.enabled && expanded)

/** Quality fades must never introduce the deploy card's underlying application scene. */
fun rememberPlayerControlsGlassSource(player: Backdrop): Backdrop = player
