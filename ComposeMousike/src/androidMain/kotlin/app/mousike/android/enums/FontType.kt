package app.mousike.android.enums

import androidx.compose.runtime.Composable
import app.mousike.android.core.enums.TextView

enum class FontType: TextView {
    Rubik,
    Poppins;

    override val text: String
        @Composable
        get() = this.name
}



