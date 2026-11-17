package app.mousike.android.components.player

import androidx.compose.ui.graphics.Color
import app.mousike.android.enums.ColorPaletteName
import app.mousike.android.enums.PlayerPlayButtonType
import app.mousike.android.ui.styling.ColorPalette

/** Shaped artwork owns its fill; its surrounding container must stay transparent. */
internal fun playButtonContainerColor(
    type: PlayerPlayButtonType,
    paletteName: ColorPaletteName,
    palette: ColorPalette,
    gradient: Boolean,
): Color = when (type) {
    PlayerPlayButtonType.CircularRibbed, PlayerPlayButtonType.Disabled -> Color.Transparent
    else -> when (paletteName) {
        ColorPaletteName.PureBlack, ColorPaletteName.ModernBlack -> palette.background4
        else -> if (gradient) palette.background1 else palette.background2
    }
}
