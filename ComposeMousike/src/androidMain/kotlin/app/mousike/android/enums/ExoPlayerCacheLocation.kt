package app.mousike.android.enums

import androidx.annotation.StringRes
import app.mousike.android.R
import app.mousike.android.core.enums.TextView

enum class ExoPlayerCacheLocation(
    @field:StringRes override val textId: Int
): TextView {

    System( R.string.cache_location_system ),
    Private( R.string.cache_location_private );
}



