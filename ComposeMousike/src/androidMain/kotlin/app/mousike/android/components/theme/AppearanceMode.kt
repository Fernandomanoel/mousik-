package app.mousike.android.components.theme

import app.mousike.android.enums.ColorPaletteMode
import app.mousike.android.ui.styling.Appearance
import app.mousike.android.ui.styling.applyPitchBlack

/** Reapply the selected mode after initialization, restoration or dynamic palette extraction. */
fun Appearance.forPaletteMode(mode: ColorPaletteMode): Appearance {
    if (mode != ColorPaletteMode.PitchBlack) return this
    val normalized = colorPalette.applyPitchBlack
    if (normalized == colorPalette) return this
    return copy(colorPalette = normalized, typography = typography.withColor(normalized.text))
}
