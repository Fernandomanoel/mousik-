package app.mousike.android.components.player

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.mousike.android.R
import app.mousike.android.colorPalette
import app.mousike.android.components.glass.glassArtworkForeground
import app.mousike.android.components.glass.GlassArea
import app.mousike.android.components.glass.GlassSettings
import app.mousike.android.components.glass.LiquidButton
import app.mousike.android.components.glass.backdrop.Backdrop
import app.mousike.android.components.glass.defaultMiniPlayerControlBackdrop
import app.mousike.android.components.glass.forArea
import app.mousike.android.components.glass.glassAreaContour
import app.mousike.android.components.glass.glassGlobalIndication
import app.mousike.android.components.glass.rememberGlassSettings
import app.mousike.android.components.glass.usesGlassMaterial
import app.mousike.android.components.navigation.dock.LocalCompactMiniPlayerProgress
import app.mousike.android.components.navigation.dock.LocalCompactMiniPlayerProgressProvider
import app.mousike.android.components.ui.nativeControlIndication
import app.mousike.android.surfaceColor
import app.mousike.android.uiRoundnessShape

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GlassMiniPlayerIconButton(
    @DrawableRes icon: Int,
    color: Color,
    onClick: () -> Unit,
    contentDescription: String,
    enabled: Boolean = true,
    rotationAngle: Float = 0f,
    alpha: Float = 1f,
    settings: GlassSettings = rememberGlassSettings(),
    backdrop: Backdrop? = defaultMiniPlayerControlBackdrop(settings),
    onLongClick: (() -> Unit)? = null,
    // Glass seats stay uniform; this option preserves the native glyph size.
    preserveNativeGeometry: Boolean = false,
) {
    MiniPlayerIconSurface(
        enabled, rotationAngle, alpha, contentDescription, onClick, onLongClick, settings, backdrop, preserveNativeGeometry,
        nativePressMotion = icon == R.drawable.play_skip_back || icon == R.drawable.play_skip_forward,
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(glassArtworkForeground(settings, color, settings.usesGlassMaterial(GlassArea.MiniPlayer))),
            modifier = Modifier.size(it).testTag("mini.player.glyph"),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GlassMiniPlayerIconButton(
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    contentDescription: String,
    enabled: Boolean = true,
    rotationAngle: Float = 0f,
    alpha: Float = 1f,
    settings: GlassSettings = rememberGlassSettings(),
    backdrop: Backdrop? = defaultMiniPlayerControlBackdrop(settings),
    onLongClick: (() -> Unit)? = null,
    // Glass seats stay uniform; this option preserves the native glyph size.
    preserveNativeGeometry: Boolean = false,
) {
    MiniPlayerIconSurface(
        enabled, rotationAngle, alpha, contentDescription, onClick, onLongClick, settings, backdrop, preserveNativeGeometry,
        nativePressMotion = false,
    ) {
        Image(
            imageVector = icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(glassArtworkForeground(settings, color, settings.usesGlassMaterial(GlassArea.MiniPlayer))),
            modifier = Modifier.size(it).testTag("mini.player.glyph"),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MiniPlayerIconSurface(
    enabled: Boolean,
    rotationAngle: Float,
    alpha: Float,
    description: String,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
    settings: GlassSettings,
    backdrop: Backdrop?,
    preserveNativeGeometry: Boolean,
    nativePressMotion: Boolean,
    icon: @Composable (androidx.compose.ui.unit.Dp) -> Unit,
) {
    val fold = LocalCompactMiniPlayerProgress.current
    val dockPresentation = LocalCompactMiniPlayerProgressProvider.current != null
    val material = settings.usesGlassMaterial(GlassArea.MiniPlayer)
    val geometry = miniPlayerIconGeometry(fold, dockPresentation, material)
    val shape = uiRoundnessShape()
    val interactionSource = remember { MutableInteractionSource() }
    MiniPlayerActionCanvas(geometry, Modifier.rotate(rotationAngle)) { canvas ->
        val modifier = canvas.alpha(alpha).semantics {
            contentDescription = description
            if (!enabled) disabled()
        }
        if (!material) {
            Box(modifier.playerControlPressMotion(interactionSource, enabled && nativePressMotion)
                .glassAreaContour(settings, GlassArea.MiniPlayer, shape).clip(shape)
                .combinedClickable(interactionSource = interactionSource,
                    indication = glassGlobalIndication(nativeControlIndication()),
                    enabled = enabled, role = Role.Button, onClick = onClick, onLongClick = onLongClick),
                contentAlignment = Alignment.Center) { icon(24.dp) }
        } else {
            LiquidButton(
                onClick = onClick,
                onLongClick = onLongClick,
                backdrop = backdrop,
                modifier = modifier.playerControlPressMotion(interactionSource, enabled && nativePressMotion),
                interactionSource = interactionSource,
                settings = settings.forArea(GlassArea.MiniPlayer),
                filmColor = surfaceColor(colorPalette().background2),
                isInteractive = enabled,
                enabled = enabled,
                contentHeight = geometry.canvas,
                contentPadding = 0.dp,
                shape = shape,
                backdropScale = 1f,
            ) { icon(if (preserveNativeGeometry) 24.dp else 22.dp) }
        }
    }
}
