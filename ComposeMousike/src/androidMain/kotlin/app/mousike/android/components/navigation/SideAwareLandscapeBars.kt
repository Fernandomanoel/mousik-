package app.mousike.android.components.navigation

import androidx.compose.runtime.Composable
import app.mousike.android.enums.NavigationBarPosition
import app.mousike.android.utils.LandscapeBarsToggleButton
import app.mousike.android.utils.landscapeBarsToggleButton

/** A side rail remains visible in landscape, so it never needs a reveal control. */
@Composable
internal fun sideAwareLandscapeBarsToggleButton(): LandscapeBarsToggleButton? =
    if (sideNavigationOwnsHeader(NavigationBarPosition.current())) null else landscapeBarsToggleButton()
