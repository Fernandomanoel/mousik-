package app.mousike.android.enums

import androidx.annotation.StringRes
import app.mousike.android.R
import app.mousike.android.core.enums.TextView

enum class ThumbnailType(
    @field:StringRes override val textId: Int
): TextView {

    Essential( R.string.player_controls_style_essential ),
    Modern( R.string.player_controls_style_modern );
}



