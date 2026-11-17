package app.mousike.android.enums

import androidx.annotation.StringRes
import app.mousike.android.R
import app.mousike.android.core.enums.TextView

enum class ArtistsType(
    @field:StringRes override val textId: Int
): TextView {

    Favorites( R.string.library_favorites_title ),
    Library( R.string.library_title ),
    Disliked( R.string.library_disliked_title ),
    OnDevice( R.string.library_on_device_title );
}



