package app.mousike.android.enums

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import app.mousike.android.R
import app.mousike.android.utils.navigationBarPositionKey
import app.mousike.android.utils.rememberPreference
import app.mousike.android.core.enums.TextView

enum class NavigationBarPosition(
    @field:StringRes override val textId: Int
): TextView {

    Left( R.string.lyrics_alignment_left_label ),
    Right( R.string.lyrics_alignment_right_label ),
    Top( R.string.navigation_position_top_label ),
    Bottom( R.string.navigation_position_bottom_label ),
    BottomFloating( R.string.navigation_position_bottom_floating_label );


    companion object {

        @Composable
        fun current() = rememberPreference( navigationBarPositionKey, NavigationBarPosition.BottomFloating ).value

    }

    @Composable
    fun isCurrent(): Boolean = current() == this

    val isHorizontal: Boolean
        get() = this == Top || this == Bottom || this == BottomFloating

    val isBottomType: Boolean
        get() = this == Bottom || this == BottomFloating

    val isFloating: Boolean
        get() = this == BottomFloating
}




