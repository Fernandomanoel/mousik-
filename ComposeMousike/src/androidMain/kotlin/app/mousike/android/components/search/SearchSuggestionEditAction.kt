package app.mousike.android.components.search

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.mousike.android.R
import app.mousike.android.components.glass.GlassArea
import app.mousike.android.components.glass.GlassSectionAction
import app.mousike.android.components.glass.GlassSectionActions
import app.mousike.android.components.glass.rememberGlassSettings
import app.mousike.android.components.glass.usesGlassMaterial
import app.mousike.android.components.glass.glassAreaContour
import app.mousike.android.components.glass.glassGlobalIndication
import app.mousike.android.components.ui.nativeControlIndication
import app.mousike.android.components.ui.reservedIconActionCanvas
import app.mousike.android.uiRoundnessShape

/** A query suggestion keeps its native pencil or presents one independent Glass bubble. */
@Composable
fun SearchSuggestionEditAction(
    onClick: () -> Unit,
    nativeContent: @Composable () -> Unit,
) {
    val settings = rememberGlassSettings()
    if (settings.usesGlassMaterial(GlassArea.ActionButtons)) {
        GlassSectionActions(
            actions = listOf(GlassSectionAction(
                icon = R.drawable.pencil,
                contentDescription = stringResource(R.string.search_title),
                onClick = onClick,
            )),
            settings = settings,
        )
    } else {
        val shape = uiRoundnessShape()
        Box(Modifier.size(42.dp)
            .glassAreaContour(settings, GlassArea.ActionButtons, shape)
            .reservedIconActionCanvas(shape, remember { MutableInteractionSource() },
                glassGlobalIndication(nativeControlIndication()), true, onClick),
            contentAlignment = Alignment.Center,
        ) { nativeContent() }
    }
}
