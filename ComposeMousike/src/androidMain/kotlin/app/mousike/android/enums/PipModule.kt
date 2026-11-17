package app.mousike.android.enums

import androidx.annotation.StringRes
import app.mousike.android.R
import app.mousike.android.core.enums.TextView

enum class PipModule(
    @field:StringRes override val textId: Int
): TextView {

    Cover( R.string.settings_picture_in_picture_artwork_module_label )
}



