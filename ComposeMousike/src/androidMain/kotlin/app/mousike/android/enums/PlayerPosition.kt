package app.mousike.android.enums

import androidx.annotation.StringRes
import app.mousike.android.R
import app.mousike.android.core.enums.TextView

enum class PlayerPosition(
    @field:StringRes override val textId: Int
): TextView {

    Top( R.string.player_position_top_label ),
    Bottom( R.string.player_position_bottom_label );
}



