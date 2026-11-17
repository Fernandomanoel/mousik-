package app.mousike.android.components.player

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.mousike.android.colorPalette
import app.mousike.android.components.glass.GlassArea
import app.mousike.android.components.glass.GlassRenderer
import app.mousike.android.components.glass.GlassSettings
import app.mousike.android.components.glass.LiquidButton
import app.mousike.android.components.glass.backdrop.Backdrop
import app.mousike.android.components.glass.defaultMiniPlayerControlBackdrop
import app.mousike.android.components.glass.forArea
import app.mousike.android.components.glass.glassAreaContour
import app.mousike.android.components.glass.glassFilmOpacity
import app.mousike.android.components.glass.glassRenderer
import app.mousike.android.components.glass.rememberGlassSettings
import app.mousike.android.components.glass.usesGlassMaterial
import app.mousike.android.components.navigation.dock.LocalCompactMiniPlayerProgress
import app.mousike.android.components.navigation.dock.LocalCompactMiniPlayerProgressProvider
import app.mousike.android.uiRoundnessShape

/** Uses a compact glass seat while preserving the native shape and centered content. */
@Composable
fun GlassMiniPlayerPlayPauseButton(
    onClick: () -> Unit,
    backgroundColor: Color,
    contentDescription: String,
    settings: GlassSettings = rememberGlassSettings(),
    backdrop: Backdrop? = defaultMiniPlayerControlBackdrop(settings),
    content: @Composable BoxScope.() -> Unit,
) {
    val fold = LocalCompactMiniPlayerProgress.current
    val dockPresentation = LocalCompactMiniPlayerProgressProvider.current != null
    // Compose clickable retains the 48dp minimum touch target around this visual control.
    val glass = settings.usesGlassMaterial(GlassArea.MiniPlayer)
    val controlSize = miniPlayerControlSize(fold, dockPresentation, glass)
    val shape = uiRoundnessShape()
    val modifier = Modifier.semantics { this.contentDescription = contentDescription }
    val interactionSource = remember { MutableInteractionSource() }
    if (glass) {
        val controlSettings = settings.forArea(GlassArea.MiniPlayer).copy(film = glassFilmOpacity(
            configured = settings.film,
            overlay = true,
            fallback = backdrop == null || glassRenderer(settings) == GlassRenderer.Tint,
            dark = colorPalette().isDark,
        ))
        LiquidButton(
            onClick = onClick,
            backdrop = backdrop,
            modifier = modifier.playerControlPressMotion(interactionSource).size(controlSize),
            interactionSource = interactionSource,
            settings = controlSettings,
            filmColor = backgroundColor,
            contentHeight = controlSize,
            contentPadding = 0.dp,
            shape = shape,
            backdropScale = 1f,
        ) {
            Box(Modifier.fillMaxSize(), content = content)
        }
    } else {
        Box(
            modifier.playerControlPressMotion(interactionSource).clip(shape)
                .clickable(interactionSource = interactionSource, indication = LocalIndication.current,
                    role = Role.Button, onClick = onClick)
                .background(backgroundColor).glassAreaContour(settings, GlassArea.MiniPlayer, shape).size(controlSize),
            content = content,
        )
    }
}
