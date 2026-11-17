package app.mousike.android.ui.components.tab.toolbar

import androidx.compose.runtime.Composable
import app.mousike.android.enums.MenuStyle
import app.mousike.android.ui.components.MenuState

interface Menu {

    val menuState: MenuState

    var menuStyle:MenuStyle

    @Composable
    fun ListMenu()

    @Composable
    fun GridMenu()

    @Composable
    fun MenuComponent()

    fun openMenu() = menuState.display { MenuComponent() }

    fun closeMenu() = menuState.hide()
}


