package app.mousike.android.enums

import androidx.annotation.StringRes
import app.mousike.android.R
import app.mousike.android.core.enums.TextView

enum class MenuStyle(
    @field:StringRes override val textId: Int
): TextView {

    List( R.string.library_layout_list_label ),
    Grid( R.string.library_layout_grid_label );
}




