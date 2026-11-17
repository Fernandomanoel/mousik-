package app.mousike.android.components.menu

import app.mousike.android.components.glass.GlassArea
import app.mousike.android.components.glass.glassOverlayPaint
import app.mousike.android.components.glass.usesGlassMaterial
import app.mousike.android.components.glass.usesGlassContour
import app.mousike.android.components.glass.glassAreaContour
import androidx.compose.foundation.background
import app.mousike.android.components.glass.GlassMaterialContainerColumn
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.mousike.android.colorPalette
import app.mousike.android.components.glass.rememberGlassSettings
import app.mousike.android.components.glass.glassNativeSurfaceTint
import app.mousike.android.recessedSurfaceColor
import app.mousike.android.topUiRoundnessShape

/** A custom media header and its list/grid share one optical surface and outer rim. */
internal val LocalMenuSheetSurface = staticCompositionLocalOf { false }

@Composable
internal fun MenuSheetSurface(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val settings = rememberGlassSettings()
    val useGlass = settings.usesGlassMaterial(GlassArea.Menus)
    GlassMaterialContainerColumn(
        modifier.fillMaxWidth().glassOverlayPaint(if (useGlass) 0f else recessedSurfaceColor(colorPalette().background0).alpha).then(
            if (useGlass) Modifier else Modifier.background(recessedSurfaceColor(colorPalette().background0))
                .glassAreaContour(settings, GlassArea.Menus, topUiRoundnessShape())
        ),
        sectionFilm = if (useGlass) null else recessedSurfaceColor(colorPalette().background0),
        material = if (useGlass) ({ exported -> Modifier.menuSheetMaterial(
            settings, topUiRoundnessShape(), drawFilm = false, exportedBackdrop = exported,
        ) }) else null,
    ) {
        CompositionLocalProvider(LocalMenuSheetSurface provides (useGlass || settings.usesGlassContour(GlassArea.Menus))) {
            content()
        }
    }
}

/** Preserve the original section colour while letting the shared material show through. */
@Composable
internal fun menuSheetSectionColor(color: Color): Color {
    if (!LocalMenuSheetSurface.current || !rememberGlassSettings().usesGlassMaterial(GlassArea.Menus)) return color
    val settings = rememberGlassSettings()
    return glassNativeSurfaceTint(settings, color)
        .copy(alpha = color.alpha * menuSheetFilmOpacity(settings))
}

/** The custom media header owns this paint, separately from the list/grid body below it. */
@Composable
internal fun Modifier.menuSheetSectionBackground(color: Color): Modifier {
    val film = menuSheetSectionColor(color)
    return background(film).glassOverlayPaint(film.alpha)
}
