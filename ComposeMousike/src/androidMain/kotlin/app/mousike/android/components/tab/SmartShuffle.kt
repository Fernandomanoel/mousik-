package app.mousike.android.components.tab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import app.mousike.android.components.ui.UiIconButton as IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.mousike.android.R
import app.mousike.android.uiRoundnessShape
import app.mousike.android.colorPalette
import app.mousike.android.components.glass.GlassToolbarIcon
import app.mousike.android.components.glass.GlassToolbarLoading
import app.mousike.android.components.ui.InlineCircularLoader
import app.mousike.android.ui.components.tab.toolbar.Descriptive
import app.mousike.android.ui.components.tab.toolbar.MenuIcon
import app.mousike.android.utils.isRecommendationEnabledKey
import app.mousike.android.utils.rememberPreference
import app.mousike.android.ui.components.tab.toolbar.DynamicColor

class SmartShuffle private constructor(
    private val isRecommendationEnabled: () -> Boolean,
    private val isRecommendationsLoading: () -> Boolean,
    private val onToggleRecommendation: () -> Unit
): MenuIcon, Descriptive, DynamicColor {

    override var isFirstColor: Boolean
        get() = isRecommendationEnabled()
        set(value) {}

    companion object {
        @Composable
        operator fun invoke(
            isRecommendationEnabled: () -> Boolean,
            isRecommendationsLoading: () -> Boolean,
            onToggleRecommendation: () -> Unit
        ): SmartShuffle = SmartShuffle(
            isRecommendationEnabled,
            isRecommendationsLoading,
            onToggleRecommendation
        )
    }

    override val iconId: Int = R.drawable.smart_shuffle
    override val messageId: Int = R.string.recommendations_action_smart_shuffle
    override val menuIconTitle: String
        @Composable
        get() = stringResource(R.string.recommendations_action_smart_shuffle)

    override fun onShortClick() {
        onToggleRecommendation()
    }

    @Composable
    override fun ToolBarButton() {
        if (isRecommendationsLoading()) {
            if (GlassToolbarLoading()) return
        } else if (GlassToolbarIcon(
            painterResource(iconId),
            if (isRecommendationEnabled()) colorPalette().text else colorPalette().textDisabled,
            32.dp, true, onClick = ::onShortClick, contentDescription = menuIconTitle,
        )) return
        Box(
            modifier = Modifier.size(48.dp), // Standard IconButton size
            contentAlignment = Alignment.Center
        ) {
            if (isRecommendationsLoading()) {
                InlineCircularLoader(
                    modifier = Modifier.size(24.dp),
                    color = colorPalette().text,
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(onClick = { onToggleRecommendation() }, shape = uiRoundnessShape()) {
                    Icon(
                        painter = painterResource(R.drawable.smart_shuffle),
                        contentDescription = stringResource(R.string.recommendations_action_smart_shuffle),
                        tint = if (isRecommendationEnabled()) colorPalette().text else colorPalette().textDisabled
                    )
                }
            }
        }
    }
} 

