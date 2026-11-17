package app.mousike.android.themed.mousike.component.playlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import app.mousike.android.R
import app.mousike.android.listentogether.listenTogetherGuestLock
import app.mousike.android.enums.SortOrder
import app.mousike.android.ui.components.tab.toolbar.Descriptive
import app.mousike.android.ui.components.tab.toolbar.DualIcon
import app.mousike.android.ui.components.tab.toolbar.DynamicColor
import app.mousike.android.ui.components.tab.toolbar.MenuIcon
import app.mousike.android.core.utils.Toaster

class PositionLock(
    colorState: MutableState<Boolean>
): MenuIcon, DualIcon, DynamicColor, Descriptive {

    constructor( sortOrder: SortOrder ): this(mutableStateOf( sortOrder == SortOrder.Ascending ))
    constructor(): this(mutableStateOf( true ))

    override val secondIconId: Int = R.drawable.unlocked
    override val iconId: Int = R.drawable.locked
    override val messageId: Int = R.string.playlist_reorder_lock_toggle_label
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    // This is inverted, first icon is locked icon
    override var isFirstIcon: Boolean by mutableStateOf( true )
    override var isFirstColor: Boolean by colorState

    // Guest lock (spec-listen-together-guest-lock-hardening): the reorder lock ("cadena") is a
    // host-owned stateful control — a locked guest sees it grayed (disabled) and can never toggle

    // it. The queue keeps it forced-on while locked (see Queue.kt).
    override val isEnabled: Boolean
        get() = !listenTogetherGuestLock.value

    fun isLocked(): Boolean = isFirstIcon

    override fun onShortClick() {
        // Guest lock: the reorder lock cannot be toggled by a guest (defense in depth — the
        // toolbar button is already disabled via [isEnabled]).
        if( listenTogetherGuestLock.value ) return
        if( !isFirstColor )
            Toaster.e( R.string.playlist_reorder_ascending_sort_required_message )
        else
            isFirstIcon = !isFirstIcon
    }
}
