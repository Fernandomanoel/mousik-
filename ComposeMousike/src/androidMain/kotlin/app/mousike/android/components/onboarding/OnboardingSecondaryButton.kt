package app.mousike.android.components.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.mousike.android.colorPalette
import app.mousike.android.components.glass.GlassArea
import app.mousike.android.components.glass.PageButton
import app.mousike.android.components.glass.GlassNativeSurfaceBounds
import app.mousike.android.components.glass.NativeButtonSurfaceProbe
import app.mousike.android.components.glass.NativePageButtonFeedback
import app.mousike.android.components.glass.PageTextButton
import app.mousike.android.components.glass.glassAreaContour
import app.mousike.android.components.glass.rememberGlassSettings
import app.mousike.android.components.glass.usesGlassContour
import app.mousike.android.components.glass.usesGlassMaterial
import app.mousike.android.uiRoundnessShape

/** Permission actions keep their native variant and replace its surface only in Glass. */
@Composable
internal fun OnboardingSecondaryButton(
    onClick: () -> Unit,
    outlined: Boolean = false,
    content: @Composable RowScope.() -> Unit,
) {
    val settings = rememberGlassSettings()
    val material = settings.usesGlassMaterial(GlassArea.ActionButtons)
    val shape = uiRoundnessShape()
    if (outlined && !material) {
        val nativeBounds = remember { GlassNativeSurfaceBounds() }
        NativePageButtonFeedback(false, true, shape, null, nativeBounds, settings) { feedback, source ->
            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.then(feedback).glassAreaContour(settings, GlassArea.ActionButtons, shape),
                shape = shape,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colorPalette().text),
                border = if (settings.usesGlassContour(GlassArea.ActionButtons)) null
                    else BorderStroke(1.dp, colorPalette().textSecondary),
                interactionSource = source,
            ) {
                content()
                NativeButtonSurfaceProbe(nativeBounds, contentRows = 1)
            }
        }
    } else if (outlined) {
        // PageButton keeps Material input and uses one shared Glass highlight.
        PageButton(
            onClick = onClick,
            shape = shape,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colorPalette().text),
            border = BorderStroke(1.dp, colorPalette().textSecondary),
            elevation = null,
            settings = settings,
            content = content,
        )
    } else {
        PageTextButton(
            onClick = onClick,
            shape = shape,
            colors = ButtonDefaults.buttonColors(contentColor = colorPalette().text),
            settings = settings,
            content = content,
        )
    }
}
