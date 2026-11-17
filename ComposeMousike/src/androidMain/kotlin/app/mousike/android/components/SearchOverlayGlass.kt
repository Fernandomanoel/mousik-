package app.mousike.android.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import app.mousike.android.colorPalette
import app.mousike.android.uiRoundnessShape
import app.mousike.android.components.glass.GlassArea
import app.mousike.android.components.glass.GlassScope
import app.mousike.android.components.glass.GlassSettings
import app.mousike.android.components.glass.LocalGlassBackdrop
import app.mousike.android.components.glass.LocalGlassContainerBackdrop
import app.mousike.android.components.glass.LocalGlassMaterialSource
import app.mousike.android.components.glass.backdrop.Backdrop
import app.mousike.android.components.glass.backdrop.backdrops.LayerBackdrop
import app.mousike.android.components.glass.backdrop.backdrops.rememberCanvasBackdrop
import app.mousike.android.components.glass.backdrop.drawPlainBackdrop
import app.mousike.android.components.glass.forArea
import app.mousike.android.components.glass.glassAreaContour
import app.mousike.android.components.glass.glassButtonFeedback
import app.mousike.android.components.glass.glassGlobalIndication
import app.mousike.android.components.glass.glassMaterial
import app.mousike.android.components.glass.inScope
import app.mousike.android.components.glass.rememberGlassDimmedBackdrop
import app.mousike.android.components.glass.rememberGlassSettings
import app.mousike.android.components.glass.usesGlassContour
import app.mousike.android.components.glass.usesGlassMaterial

/** Global roundness owns ordinary seats in both modes; the playback circle is intentional. */
internal fun searchOverlayShape(native: Shape, global: Shape): Shape =
    if (native === CircleShape) native else global

/** Uses the nearest visible parent; the result card exports its plate for its own actions. */
@Composable
internal fun rememberSearchOverlayMaterialSource(): Backdrop {
    val fallbackColor = colorPalette().background0
    val fallback = rememberCanvasBackdrop { drawRect(fallbackColor) }
    val page = rememberSearchOverlayPageBackdrop() ?: LocalGlassBackdrop.current ?: fallback
    val dimmed = rememberGlassDimmedBackdrop(page, .85f)
    return LocalGlassMaterialSource.current ?: LocalGlassContainerBackdrop.current ?: dimmed ?: fallback
}

/** Child-free material includes the same black scrim as both recognition overlays. */
@Composable
internal fun Modifier.searchOverlaySurface(
    nativeShape: Shape,
    fill: Color,
    area: GlassArea = GlassArea.ActionButtons,
    nativeBorder: Color? = null,
    exportedBackdrop: LayerBackdrop? = null,
    settings: GlassSettings = rememberGlassSettings().inScope(GlassScope.Search),
): Modifier {
    val shape = searchOverlayShape(nativeShape, uiRoundnessShape())
    val material = settings.usesGlassMaterial(area)
    val backdrop = rememberSearchOverlayMaterialSource()
    val surface = if (material) {
        Modifier.glassMaterial(settings.forArea(area), shape, ownBackdrop = backdrop,
            filmColor = fill, drawShadow = false, exportedBackdrop = exportedBackdrop)
    } else {
        val native = if (exportedBackdrop == null) Modifier.background(fill) else {
            val empty = rememberCanvasBackdrop {}
            Modifier.drawPlainBackdrop(empty, { shape }, {}, exportedBackdrop = exportedBackdrop,
                onDrawSurface = { drawRect(fill) })
        }
        native.glassAreaContour(settings, area, shape)
    }
    val border = if (!material && !settings.usesGlassContour(area) && nativeBorder != null)
        Modifier.border(1.dp, nativeBorder, shape) else Modifier
    return clip(shape).then(surface).then(border)
}

/** A single clickable owns the callback; explicitly absent native indication stays absent. */
@Composable
internal fun Modifier.searchOverlayAction(
    nativeShape: Shape,
    fill: Color,
    nativeIndication: Boolean = true,
    settings: GlassSettings = rememberGlassSettings().inScope(GlassScope.Search),
    onClick: () -> Unit,
): Modifier {
    val source = remember { MutableInteractionSource() }
    val shape = searchOverlayShape(nativeShape, uiRoundnessShape())
    val material = settings.usesGlassMaterial(GlassArea.ActionButtons)
    val feedback = if (material) Modifier.glassButtonFeedback(source, shape,
        drawHighlight = nativeIndication, settings = settings) else Modifier
    return then(feedback).searchOverlaySurface(nativeShape, fill, settings = settings)
        .clickable(interactionSource = source,
            indication = if (material) null else glassGlobalIndication(if (nativeIndication) LocalIndication.current else null),
            onClick = onClick)
}
