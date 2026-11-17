package app.mousike.android.components.navigation

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.mousike.android.utils.LandscapeBars
import app.mousike.android.utils.hidesBarsInLandscape
import app.mousike.android.enums.NavigationBarPosition
import app.mousike.android.core.navigation.currentPageNavigationEntry
import app.mousike.android.extensions.discord.DiscordUiState
import app.mousike.android.utils.coroutines.MousikeDispatchers

/** Keeps outgoing page geometry tied to its own entry during navigation transitions. */
@Composable
internal fun hidePageBarsInLandscapeMobile(): Boolean {
    if (sideNavigationOwnsHeader(NavigationBarPosition.current())) return false
    val configuration = LocalConfiguration.current
    val entry = currentPageNavigationEntry()
    val route = if (entry != null) entry.destination.route else {
        val currentRoute by DiscordUiState.currentRoute.collectAsStateWithLifecycle(context = MousikeDispatchers.DATA)
        currentRoute
    }
    val homeTab by DiscordUiState.currentHomeTab.collectAsStateWithLifecycle(context = MousikeDispatchers.DATA)
    return hidesBarsInLandscape(
        isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE,
        smallestScreenWidthDp = configuration.smallestScreenWidthDp,
        route = route,
        homeTab = homeTab,
    ) && !LandscapeBars.revealed
}
