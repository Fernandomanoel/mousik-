package app.mousike.android.enums

import androidx.compose.runtime.Composable
import app.mousike.android.core.enums.TextView
import app.mousike.android.R
import androidx.compose.ui.res.stringResource

enum class DislikeMode : TextView {
    Enabled,
    Disabled;

    override val text: String
        @Composable
        get() = when (this) {
            Enabled -> stringResource(R.string.common_state_on)
            Disabled -> stringResource(R.string.common_state_off)
        }

    val isEnabled: Boolean get() = this == Enabled
}
