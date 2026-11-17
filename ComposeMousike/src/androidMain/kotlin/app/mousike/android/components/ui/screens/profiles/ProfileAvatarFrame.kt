package app.mousike.android.components.ui.screens.profiles

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import app.mousike.android.artistThumbnailShape
import app.mousike.android.components.glass.GlassArea
import app.mousike.android.components.glass.GlassSettings
import app.mousike.android.components.glass.glassEdgeRim
import app.mousike.android.components.glass.glassTint
import app.mousike.android.components.glass.rememberGlassSettings
import app.mousike.android.components.glass.usesGlassContour

/** Follows the image edge at its exact bounds, without applying material effects to the photo. */
@Composable
internal fun ProfileAvatarFrame(
    size: Dp,
    area: GlassArea,
    modifier: Modifier = Modifier,
    settings: GlassSettings = rememberGlassSettings(),
    shape: Shape = artistThumbnailShape(),
    content: @Composable () -> Unit,
) {
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        content()
        if (settings.usesGlassContour(area)) {
            Box(Modifier.matchParentSize().glassEdgeRim(settings, shape, glassTint(settings)))
        }
    }
}
