package app.mousike.android.components.playlist

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import app.mousike.android.components.glass.backdrop.Backdrop
import androidx.compose.ui.graphics.compositeOver
import app.mousike.android.components.glass.GlassRenderer
import app.mousike.android.components.glass.LocalGlassContainerBackdrop
import app.mousike.android.components.glass.LocalGlassMaterialSource
import app.mousike.android.components.glass.rememberInlineGlassBackdrop
import app.mousike.android.components.glass.forArea
import app.mousike.android.components.glass.glassFilmOpacity
import app.mousike.android.components.glass.glassNativeSurfaceTint
import app.mousike.android.components.glass.glassOpticalShape
import app.mousike.android.components.glass.glassRenderer
import app.mousike.android.components.glass.backdrop.backdrops.LayerBackdrop
import app.mousike.android.components.glass.backdrop.backdrops.rememberCanvasBackdrop
import app.mousike.android.components.glass.backdrop.backdrops.rememberLayerBackdrop
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import app.mousike.android.colorPalette
import app.mousike.android.components.glass.GlassArea
import app.mousike.android.components.glass.GlassSettings
import app.mousike.android.components.glass.glassSettingsCardBackground
import app.mousike.android.components.glass.glassSettingsCardRim
import app.mousike.android.components.glass.glassTint
import app.mousike.android.components.glass.usesGlassMaterial
import app.mousike.android.thumbnailShape
import app.mousike.android.uiRoundnessShape

private val LocalPlaylistHeaderInput = staticCompositionLocalOf<Backdrop?> { null }

/** Styles the existing playlist header bounds without changing its content or spacing. */
@Composable
internal fun Modifier.localPlaylistHeaderSurface(settings: GlassSettings, exported: LayerBackdrop? = null): Modifier {
    val palette = colorPalette()
    if (settings.usesGlassMaterial(GlassArea.SettingsCards)) {
        val shape = uiRoundnessShape()
        return clip(shape).glassSettingsCardBackground(
            backgroundColor = palette.background0,
            cardColor = palette.background1,
            shape = shape,
            settings = settings,
            exportedBackdrop = exported,
            sourceBackdrop = LocalPlaylistHeaderInput.current,
        )
    }
    val shape = thumbnailShape()
    return background(palette.background1, shape)
        .glassSettingsCardRim(settings, shape, glassTint(settings))
}

/** Optical export for buttons inside the existing panel; the neighboring cover is excluded. */
@Composable
internal fun LocalPlaylistHeaderSurfaceScope(
    settings: GlassSettings,
    content: @Composable (LayerBackdrop?) -> Unit,
) {
    val palette = colorPalette()
    val material = settings.usesGlassMaterial(GlassArea.SettingsCards)
    val area = settings.forArea(GlassArea.SettingsCards)
    val fallback = glassRenderer(area) == GlassRenderer.Tint || glassOpticalShape(uiRoundnessShape()) == null
    val export = rememberLayerBackdrop()
    val film = glassNativeSurfaceTint(area, palette.background1)
    val visibleFill = if (material) film.copy(alpha = film.alpha * glassFilmOpacity(
        area.film, overlay = false, fallback = fallback, dark = palette.isDark,
    )).compositeOver(palette.background0) else palette.background1
    val uniform = rememberCanvasBackdrop { drawRect(visibleFill) }
    // The Tint renderer has no optical layer, so export its exact composite fill.
    val source = if (material && !fallback) export else uniform
    val materialSource = if (material) rememberInlineGlassBackdrop(palette.background0) else uniform
    // The modifier below is built inside this scope. Keep its input separate from
    // the rendered export offered to its buttons, otherwise the header samples itself.
    CompositionLocalProvider(LocalGlassContainerBackdrop provides source,
        LocalPlaylistHeaderInput provides materialSource,
        LocalGlassMaterialSource provides source) {
        content(export.takeIf { material && !fallback })
    }
}
