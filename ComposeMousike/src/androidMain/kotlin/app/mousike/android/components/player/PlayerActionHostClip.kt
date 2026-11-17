package app.mousike.android.components.player

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import app.mousike.android.components.glass.GlassArea
import app.mousike.android.components.glass.GlassScope
import app.mousike.android.components.glass.GlassSettings
import app.mousike.android.components.glass.inScope
import app.mousike.android.components.glass.usesGlassMaterial

/** Material panes own their corners; a shared host mask would cut their outer corners again. */
fun Modifier.playerActionHostClip(settings: GlassSettings, nativeShape: Shape): Modifier =
    if (settings.inScope(GlassScope.PlayerActions).usesGlassMaterial(GlassArea.ActionButtons)) this
    else clip(nativeShape)
