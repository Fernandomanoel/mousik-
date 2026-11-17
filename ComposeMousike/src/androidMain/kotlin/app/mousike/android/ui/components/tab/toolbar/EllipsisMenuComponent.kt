package app.mousike.android.ui.components.tab.toolbar

import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.mousike.android.R
import app.mousike.android.enums.MenuStyle
import app.mousike.android.ui.components.LocalMenuState
import app.mousike.android.ui.components.MenuState
import app.mousike.android.components.menu.GridMenu
import app.mousike.android.components.menu.ListMenu
import app.mousike.android.utils.menuStyleKey
import app.mousike.android.utils.rememberPreference

class EllipsisMenuComponent private constructor(
    private val buttons: () -> List<Button>,
    override val menuState: MenuState,
    styleState: MutableState<MenuStyle>
) : Menu, Icon {

    companion object {
        @JvmStatic
        @Composable
        fun init( items: () -> List<Button> ) = EllipsisMenuComponent(
            items,
            LocalMenuState.current,
            rememberPreference( menuStyleKey, MenuStyle.List )
        )
    }

    override val iconId: Int = R.drawable.ellipsis_horizontal

    override var menuStyle: MenuStyle by styleState

    override fun onShortClick() = openMenu()

    @Composable
    override fun ListMenu() {
        ListMenu.Menu(title = "") {
            buttons().forEach {
                if( it is MenuIcon)
                    it.ListMenuItem()
            }
        }
    }

    @Composable
    override fun GridMenu() {
        GridMenu.Menu(title = "") {
            items( buttons(), Button::hashCode ) {
                if( it is MenuIcon)
                    it.GridMenuItem()
            }
        }
    }

    @Composable
    override fun MenuComponent() {
        if( menuStyle == MenuStyle.Grid )
            GridMenu()
        else
            ListMenu()
    }
}


