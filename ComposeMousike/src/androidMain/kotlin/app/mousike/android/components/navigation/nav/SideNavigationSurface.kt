package app.mousike.android.components.navigation.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import app.mousike.android.enums.NavigationBarPosition
import app.mousike.android.colorPalette
import app.mousike.android.components.glass.GlassArea
import app.mousike.android.components.glass.GlassScreenEdge
import app.mousike.android.components.glass.GlassSettings
import app.mousike.android.components.glass.backdrop.backdrops.LayerBackdrop
import app.mousike.android.components.glass.backdrop.backdrops.layerBackdrop
import app.mousike.android.components.glass.extendGlassPastScreenEdge
import app.mousike.android.components.glass.forArea
import app.mousike.android.components.glass.glassAreaContour
import app.mousike.android.components.glass.glassMaterial
import app.mousike.android.components.glass.usesGlassMaterial
import app.mousike.android.surfaceColor
import app.mousike.android.uiRoundnessShape

/** One side-panel silhouette for full navigation and header-only rails, with or without Glass. */
@Composable
internal fun SideNavigationSurface(
    settings: GlassSettings,
    modifier: Modifier = Modifier,
    surfaceBackdrop: LayerBackdrop? = null,
) {
    val right = NavigationBarPosition.Right.isCurrent()
    val shape = sideNavigationBarShape(uiRoundnessShape(), right, LocalLayoutDirection.current)
    val film = surfaceColor(colorPalette().background1)
    val density = LocalDensity.current
    val safeInsets = WindowInsets.safeDrawing
    val edgeInset = if (right) safeInsets.getRight(density, LayoutDirection.Ltr)
        else safeInsets.getLeft(density, LayoutDirection.Ltr)
    val surface = if (settings.usesGlassMaterial(GlassArea.Nav)) {
        modifier.then(if (edgeInset == 0) Modifier.extendGlassPastScreenEdge(
            if (right) GlassScreenEdge.Right else GlassScreenEdge.Left
        ) else Modifier)
            .then(if (surfaceBackdrop != null) Modifier.layerBackdrop(surfaceBackdrop) else Modifier)
            .glassMaterial(settings.forArea(GlassArea.Nav), shape, filmColor = film).clip(shape)
    } else modifier
        .then(if (surfaceBackdrop != null) Modifier.layerBackdrop(surfaceBackdrop) else Modifier)
        .clip(shape).background(film).glassAreaContour(settings, GlassArea.Nav, shape)
    Box(surface)
}
