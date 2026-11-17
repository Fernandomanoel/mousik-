package app.mousike.android.components.tab

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import app.mousike.android.R
import app.mousike.android.components.shuffleButtonIconRes
import app.mousike.android.LocalPlayerServiceBinder
import app.mousike.android.models.Song
import app.mousike.android.playback.services.PlayerServiceModern
import app.mousike.android.playback.utils.Shuffler
import app.mousike.android.ui.components.tab.toolbar.Descriptive
import app.mousike.android.ui.components.tab.toolbar.MenuIcon
import app.mousike.android.core.utils.Toaster
import app.mousike.android.utils.coroutines.MousikeDispatchers
import kotlinx.coroutines.flow.Flow
import app.mousike.android.ui.components.LocalMenuState
import app.mousike.android.ui.components.MenuState

@UnstableApi
class SongShuffler private constructor(
    private val binder: PlayerServiceModern.Binder?,
    private val menuState: MenuState?,
    private val songs: () -> List<Song>
): MenuIcon, Descriptive {

    companion object {
        @Composable
        operator fun invoke( songs: () -> List<Song> ) =
            SongShuffler( LocalPlayerServiceBinder.current, LocalMenuState.current, songs )

        @Composable
        operator fun invoke(
            databaseCall: (Int) -> Flow<List<Song>>,
            vararg key: Any?
        ): SongShuffler {
            val songsToShuffle by remember( key ) {
                databaseCall( Int.MAX_VALUE )
            }.collectAsStateWithLifecycle( initialValue = emptyList(), context = MousikeDispatchers.DATA )

            return SongShuffler( LocalPlayerServiceBinder.current, LocalMenuState.current ) { songsToShuffle }
        }

        fun playShuffled(
            binder: PlayerServiceModern.Binder,
            songs: List<Song>
        ) {
            if( songs.isEmpty() ) {
                Toaster.i( R.string.player_shuffle_empty_message )
                return
            }
            Shuffler.play( binder, songs )
        }
    }

    // Issue #866 (gh-866): app-wide shuffle confirmation flash — the icon follows the binder's
    // flash state (shuffle_ok ~1 s), triggered in onShortClick below.
    override val iconId: Int get() = shuffleButtonIconRes( binder )
    override val messageId: Int = R.string.player_action_shuffle_hint
    override val menuIconTitle: String
        @Composable
        get() = stringResource( R.string.player_action_shuffle )

    override fun onShortClick() {
        playShuffled(
            this.binder ?: return,
            this.songs()
        )
        this.binder?.triggerShuffleOkFlash()
        menuState?.hide()
    }
}
