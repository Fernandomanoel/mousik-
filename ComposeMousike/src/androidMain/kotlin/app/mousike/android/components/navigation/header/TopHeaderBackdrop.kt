package app.mousike.android.components.navigation.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.mousike.android.components.glass.GlassArea
import app.mousike.android.components.glass.GlassSettings
import app.mousike.android.components.glass.GlassScreenEdge
import app.mousike.android.components.glass.extendGlassPastScreenEdge
import app.mousike.android.components.glass.LocalGlassBackdrop
import app.mousike.android.components.glass.forArea
import app.mousike.android.components.glass.glassMaterial
import app.mousike.android.components.glass.usesGlassMaterial
import app.mousike.android.components.navigation.appHeaderOverlayTopPx
import app.mousike.android.components.navigation.nav.topNavigationBarShape
import app.mousike.android.uiRoundnessShape
import app.mousike.android.components.ui.screens.home.LocalHomeGlassInAppHeader
import app.mousike.android.components.ui.screens.home.LocalSharedHomeHeaderGlass
import app.mousike.android.components.glass.backdrop.Backdrop
import app.mousike.android.components.glass.backdrop.backdrops.LayerBackdrop
import app.mousike.android.components.glass.backdrop.backdrops.layerBackdrop
import app.mousike.android.components.glass.backdrop.backdrops.rememberLayerBackdrop

/** The shared material recording, used by Top selectors instead of an empty local surface. */
internal val LocalUnifiedTopGlassSurface = staticCompositionLocalOf<LayerBackdrop?> { null }

/** One pane extends above the nav row; only foreground row height participates in layout. */
@Composable
internal fun UnifiedTopNavigationGlass(
    settings: GlassSettings,
    filmColor: Color,
    pageBackdrop: Backdrop?,
    navigationTopPx: () -> Int,
    content: @Composable () -> Unit,
) {
    val appHeaderOwnsPane = LocalHomeGlassInAppHeader.current
    val shared = LocalSharedHomeHeaderGlass.current?.takeIf {
        it.topNavigation && (appHeaderOwnsPane || it.owner != null)
    }
    if (shared != null) {
        DisposableEffect(shared) {
            onDispose { shared.navigationBottomPx = 0 }
        }
        // The Home owns the material below its foreground. Root navigation contributes
        // only its measured boundary and controls, avoiding a second pane over the toolbar.
        Layout(content = {
            CompositionLocalProvider(LocalUnifiedTopGlassSurface provides shared.surface) {
                Box { content() }
            }
        }) { children, constraints ->
            val foreground = children.single().measure(constraints)
            shared.navigationBottomPx = navigationTopPx().coerceAtLeast(0) + foreground.height
            layout(foreground.width, foreground.height) { foreground.placeRelative(0, 0) }
        }
        return
    }
    val surfaceBackdrop = LocalUnifiedTopGlassSurface.current ?: rememberLayerBackdrop()
    val shape = topNavigationBarShape(uiRoundnessShape())
    val material = Modifier.extendGlassPastScreenEdge(GlassScreenEdge.Top).layerBackdrop(surfaceBackdrop).glassMaterial(
        settings = settings.forArea(GlassArea.Nav),
        shape = shape,
        ownBackdrop = pageBackdrop,
        filmColor = filmColor,
        drawShadow = false,
    )
    Layout(content = {
        Box(material)
        CompositionLocalProvider(LocalUnifiedTopGlassSurface provides surfaceBackdrop) {
            Box { content() }
        }
    }) { children, constraints ->
        val foreground = children[1].measure(constraints)
        val top = navigationTopPx().coerceAtLeast(0)
        val panel = children[0].measure(Constraints.fixed(foreground.width, foreground.height + top))
        layout(foreground.width, foreground.height) {
            panel.placeRelative(0, -top)
            foreground.placeRelative(0, 0)
        }
    }
}

/** Stationary Top material: changing its clip never resizes the page or the optical capture. */
@Composable
internal fun TopHeaderBackdrop(
    settings: GlassSettings,
    filmColor: Color,
    statusBarTopPx: Float,
    headerRowHeightPx: Float,
    headerOffsetPx: () -> Float,
) {
    val density = LocalDensity.current
    val material = if (settings.usesGlassMaterial(GlassArea.Nav)) Modifier.glassMaterial(
        settings = settings.forArea(GlassArea.Nav),
        shape = RoundedCornerShape(0.dp),
        ownBackdrop = LocalGlassBackdrop.current,
        filmColor = filmColor,
        applyEdgeEffects = false,
        highlightAlpha = 0f,
        drawShadow = false,
    ) else Modifier.background(filmColor)
    Box(Modifier.fillMaxWidth()
        .height(with(density) { (statusBarTopPx + headerRowHeightPx).toDp() })
        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
        .graphicsLayer {
            shape = TopHeaderBackingShape(appHeaderOverlayTopPx(headerRowHeightPx, statusBarTopPx, headerOffsetPx()))
            clip = true
        }
        .then(material))
}

private data class TopHeaderBackingShape(val bottom: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Rectangle(Rect(0f, 0f, size.width, bottom.coerceIn(0f, size.height)))
}
