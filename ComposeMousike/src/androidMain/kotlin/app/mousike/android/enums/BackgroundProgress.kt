package app.mousike.android.enums;

import androidx.annotation.StringRes
import app.mousike.android.R
import app.mousike.android.core.enums.TextView

enum class BackgroundProgress(
    @field:StringRes override val textId: Int
): TextView {

    Player( R.string.player_title ),

    MiniPlayer( R.string.player_progress_mini_player ),

    Both( R.string.player_progress_both ),

    Disabled( R.string.common_option_disabled );
}




