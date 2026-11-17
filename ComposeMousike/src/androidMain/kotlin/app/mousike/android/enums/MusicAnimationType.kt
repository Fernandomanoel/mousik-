package app.mousike.android.enums

import androidx.annotation.StringRes
import app.mousike.android.R
import app.mousike.android.core.enums.TextView

enum class MusicAnimationType(
    @field:StringRes override val textId: Int
): TextView {

    Disabled( R.string.common_option_disabled ),

    Bars( R.string.visualizer_animation_bars ),

    CrazyBars( R.string.visualizer_animation_crazy_bars ),

    CrazyPoints( R.string.visualizer_animation_crazy_points ),

    Bubbles( R.string.visualizer_animation_bubbles );
}



