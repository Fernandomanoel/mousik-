package app.mousike.android.enums

import androidx.annotation.StringRes
import app.mousike.android.R
import app.mousike.android.core.enums.TextView

/**
 * Audio quality used for downloads, independent from the streaming quality.
 * Maps to the same InnerTubeX qualities as [app.mousike.android.enums.AudioQualityFormat].
 */
enum class DownloadQualityFormat(
    @field:StringRes override val textId: Int
): TextView {

    Auto( R.string.audio_quality_automatic ),

    High( R.string.audio_quality_format_high ),

    Low( R.string.audio_quality_format_low );
}

/**
 * Preference key storing the selected [DownloadQualityFormat] name.
 */
const val downloadQualityFormatKey = "downloadQualityFormat"
