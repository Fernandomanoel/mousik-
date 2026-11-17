package app.mousike.android.ui.components.tab

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.mousike.android.R
import app.mousike.android.components.ui.iconBadgeBackground
import app.mousike.android.colorPalette
import app.mousike.android.uiRoundnessShape
import app.mousike.android.enums.HomeItemSize
import app.mousike.android.ui.components.LocalMenuState
import app.mousike.android.ui.components.MenuState
import app.mousike.android.ui.components.tab.toolbar.Descriptive
import app.mousike.android.ui.components.tab.toolbar.MenuIcon
import app.mousike.android.components.menu.ListMenu
import androidx.compose.material3.Icon
import app.mousike.android.utils.Preference

class ItemSize private constructor(
    val menuState: MenuState,
    private val sizeState: MutableState<HomeItemSize>
): MenuIcon, Descriptive {

    companion object {
        @JvmStatic
        @Composable
        fun init(key: Preference.Key<HomeItemSize>): ItemSize =
            ItemSize(
                LocalMenuState.current,
                Preference.remember(key)
            )
    }

    override val iconId: Int = R.drawable.resize
    override val messageId: Int = R.string.common_size_label
    override val menuIconTitle: String
        @Composable
        get() = stringResource( R.string.common_size_label )

    val size: HomeItemSize
        get() = sizeState.value

    @Composable
    fun SettingIcon(@DrawableRes icon: Int) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .iconBadgeBackground(
                    color = colorPalette().accent.copy(alpha = 0.1f),
                    shape = uiRoundnessShape()
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                tint = colorPalette().accent,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }

    @Composable
    private fun Entry( size: HomeItemSize) {
        ListMenu.Entry(
            text = size.text,
            icon = { SettingIcon(size.iconId) },
            onClick = {
                sizeState.value = size
                menuState.hide()
            }
        )
    }

    override fun onShortClick() {
        menuState.display {
            ListMenu.Menu(title = stringResource(R.string.common_size_label)) {
                HomeItemSize.entries.forEach { Entry(it) }
            }
        }
    }
}


