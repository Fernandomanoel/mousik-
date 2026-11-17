package app.mousike.android.components.glass

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import app.mousike.android.colorPalette
import app.mousike.android.surfaceColor

/** Retains the native elevated theme film, as navigation and header glass do. */
@Composable
internal fun glassActionFilmColor(): Color = surfaceColor(colorPalette().background1)
