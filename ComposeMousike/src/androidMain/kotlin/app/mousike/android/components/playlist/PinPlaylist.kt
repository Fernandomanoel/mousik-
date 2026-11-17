package app.mousike.android.components.playlist

import app.mousike.android.core.database.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.mousike.android.R
import app.mousike.android.core.database.Database
import app.mousike.android.PINNED_PREFIX
import app.mousike.android.models.Playlist
import app.mousike.android.ui.components.tab.toolbar.Descriptive
import app.mousike.android.ui.components.tab.toolbar.DynamicColor
import app.mousike.android.ui.components.tab.toolbar.MenuIcon

class PinPlaylist(
    val playlist: Playlist?,
): MenuIcon, DynamicColor, Descriptive {

    companion object {
        @Composable
        operator fun invoke( playlist: Playlist ) = PinPlaylist(playlist)
    }

    override val iconId: Int = R.drawable.pin_filled
    override val messageId: Int = R.string.playlist_action_toggle_pinned
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override var isFirstColor: Boolean
        get() = playlist?.name?.startsWith( PINNED_PREFIX, true ) == true
        set(value) =
            throw UnsupportedOperationException("Please use Database.playlistTable.togglePin(playlistId) instead!")

    override fun onShortClick() = Database.asyncTransaction {
        val playlistId = playlist?.id ?: return@asyncTransaction
        playlistTable.togglePin( playlistId )
    }
}

