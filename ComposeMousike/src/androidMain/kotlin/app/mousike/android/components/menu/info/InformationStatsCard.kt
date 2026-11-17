package app.mousike.android.components.menu.info

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.mousike.android.colorPalette
import app.mousike.android.components.glass.GlassScope
import app.mousike.android.components.glass.GlassArea
import app.mousike.android.components.glass.GlassSettings
import app.mousike.android.components.glass.LocalGlassContainerBackdrop
import app.mousike.android.components.glass.LocalGlassMaterialSource
import app.mousike.android.components.glass.backdrop.backdrops.rememberCanvasBackdrop
import app.mousike.android.components.glass.backdrop.backdrops.rememberCombinedBackdrop
import app.mousike.android.components.glass.inScope
import app.mousike.android.components.glass.rememberGlassSettings
import app.mousike.android.components.glass.usesGlassMaterial
import app.mousike.android.components.menu.LocalMenuSheetSurface
import app.mousike.android.components.settings.common.SettingsCardSurface
import app.mousike.android.uiRoundnessShape

/** The existing media statistics card shares the Details card policy and native layout. */
@Composable
fun InformationStatsCard(
    modifier: Modifier = Modifier,
    settings: GlassSettings = rememberGlassSettings(GlassScope.Details),
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = colorPalette()
    val bodyColor = informationMenuBodyColor()
    val body = rememberCanvasBackdrop { drawRect(bodyColor) }
    val menu = LocalGlassContainerBackdrop.current
    val opticalMenu = LocalMenuSheetSurface.current &&
        rememberGlassSettings().usesGlassMaterial(GlassArea.Menus)
    // The information body paints its band outside this card, after the shared menu material.
    val source = if (opticalMenu && menu != null) rememberCombinedBackdrop(menu, body) else body
    CompositionLocalProvider(
        LocalGlassContainerBackdrop provides source,
        LocalGlassMaterialSource provides source,
    ) {
        SettingsCardSurface(
            modifier = modifier,
            shape = uiRoundnessShape(),
            containerColor = palette.accent.copy(alpha = .2f),
            backgroundColor = palette.background0,
            shadowColor = Color.Transparent,
            shadowElevation = 0.dp,
            settings = settings.inScope(GlassScope.Details),
            content = content,
        )
    }
}
