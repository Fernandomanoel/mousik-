package app.mousike.android.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import app.mousike.android.R
import app.mousike.android.uiRoundnessShape
import app.mousike.android.components.glass.GlassArea
import app.mousike.android.components.glass.GlassExplicitBadge
import app.mousike.android.components.glass.GlassSettings
import app.mousike.android.components.glass.rememberGlassSettings
import app.mousike.android.components.glass.usesGlassContour
import app.mousike.android.components.glass.usesGlassMaterial

/** Common explicit indicator. The caller owns placement, size and native tint. */
@Composable
fun ExplicitBadge(
    color: Color,
    modifier: Modifier = Modifier,
    settings: GlassSettings = rememberGlassSettings(),
    passive: Boolean = false,
) {
    val description = if (passive) stringResource(R.string.common_content_explicit_label) else null
    if (settings.usesGlassMaterial(GlassArea.ActionButtons) || settings.usesGlassContour(GlassArea.ActionButtons)) {
        GlassExplicitBadge(modifier, settings, passive, description)
    } else if (passive) {
        Image(
            painter = painterResource(R.drawable.explicit),
            contentDescription = description,
            colorFilter = ColorFilter.tint(color),
            modifier = modifier.clip(uiRoundnessShape()),
        )
    } else {
        app.mousike.android.ui.components.themed.IconButton(
            icon = R.drawable.explicit, color = color, onClick = {}, modifier = modifier,
        )
    }
}
