package app.mousike.android.enums

import androidx.annotation.StringRes
import app.mousike.android.R
import app.mousike.android.core.enums.TextView

enum class AudioQualityFormat(
    @field:StringRes override val textId: Int
): TextView {

    Auto( R.string.audio_quality_automatic ),

    High( R.string.audio_quality_format_high ),

    Low( R.string.audio_quality_format_low );
}



