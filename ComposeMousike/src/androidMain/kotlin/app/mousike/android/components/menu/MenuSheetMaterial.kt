package app.mousike.android.components.menu

import app.mousike.android.components.glass.GlassArea
import app.mousike.android.components.glass.forArea
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import app.mousike.android.colorPalette
import app.mousike.android.components.glass.GlassRenderer
import app.mousike.android.components.glass.GlassSettings
import app.mousike.android.components.glass.LocalGlassBackdrop
import app.mousike.android.components.glass.LocalGlassMenuHost
import app.mousike.android.components.glass.backdrop.backdrops.LayerBackdrop
import app.mousike.android.components.glass.glassMaterial
import app.mousike.android.components.glass.glassFilmOpacity
import app.mousike.android.components.glass.glassRenderer

@Composable
private fun canSampleMenuBackdrop(): Boolean {
    val host = LocalGlassMenuHost.current
    return host?.dialogs?.windowRoot === LocalView.current.rootView && LocalGlassBackdrop.current != null
}

@Composable
internal fun menuSheetFilmOpacity(settings: GlassSettings): Float = glassFilmOpacity(
    configured = settings.film,
    overlay = true,
    fallback = !canSampleMenuBackdrop() || glassRenderer(settings) == GlassRenderer.Tint,
    dark = colorPalette().isDark,
)

/** Never samples a graphics layer from a different Android window. */
@Composable
internal fun Modifier.menuSheetMaterial(settings: GlassSettings, shape: Shape, drawFilm: Boolean = true,
    exportedBackdrop: LayerBackdrop? = null): Modifier {
    return glassMaterial(
        settings.forArea(GlassArea.Menus),
        shape,
        separateWindow = !canSampleMenuBackdrop(),
        menu = true,
        drawFilm = drawFilm,
        exportedBackdrop = exportedBackdrop,
    )
}
