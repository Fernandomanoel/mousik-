package app.mousike.android.enums

import androidx.annotation.StringRes
import app.mousike.android.R
import app.mousike.android.core.enums.TextView

enum class TransitionEffect(
    @field:StringRes override val textId: Int
): TextView {

    SlideVertical( R.string.settings_transition_slide_vertical ),

    SlideHorizontal( R.string.settings_transition_slide_horizontal ),

    Scale( R.string.settings_transition_scale ),

    Fade( R.string.settings_transition_fade ),

    Expand( R.string.settings_transition_expand ),

    None( R.string.common_option_none );
}



