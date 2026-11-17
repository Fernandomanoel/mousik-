package app.mousike.android.components.dialog.playlist

import app.mousike.android.core.database.*

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import app.mousike.android.R
import app.mousike.innertube.YtMusic
import app.mousike.android.core.database.Database

import app.mousike.android.appContext
import app.mousike.android.colorPalette
import app.mousike.android.models.Playlist
import app.mousike.android.utils.preferences
import app.mousike.android.ui.components.tab.toolbar.Descriptive
import app.mousike.android.ui.components.tab.toolbar.MenuIcon
import app.mousike.android.components.settings.accounts.isYouTubeSyncEnabled
import app.mousike.android.utils.rememberPreference
import app.mousike.android.utils.syncPushPlaylistKey
import app.mousike.android.utils.syncDirectionKey
import app.mousike.android.utils.getSyncDirection
import app.mousike.android.utils.isNetworkConnected
import app.mousike.android.enums.SyncDirection
import app.mousike.android.appContext
import kotlinx.coroutines.CoroutineScope
import app.mousike.android.utils.coroutines.MousikeDispatchers
import kotlinx.coroutines.launch
import app.mousike.android.core.utils.Toaster
import app.mousike.android.components.dialog.common.InputDialogConstraints
import app.mousike.android.components.dialog.common.TextInputDialog
import timber.log.Timber

class NewPlaylistDialog private constructor(
    activeState: MutableState<Boolean>,
    valueState: MutableState<TextFieldValue>,
    private val coroutineScope: kotlinx.coroutines.CoroutineScope,
    private val onPlaylistCreated: (Playlist) -> Unit = {}
): TextInputDialog(InputDialogConstraints.ALL), MenuIcon, Descriptive {

    companion object {
        @Composable
        operator fun invoke(onPlaylistCreated: (Playlist) -> Unit = {}): NewPlaylistDialog {
            val coroutineScope = rememberCoroutineScope()
            return NewPlaylistDialog(
                remember { mutableStateOf(false) },
                remember {
                    mutableStateOf( TextFieldValue() )
                },
                coroutineScope,
                onPlaylistCreated
            )
        }
    }

    override val keyboardOption: KeyboardOptions = KeyboardOptions.Default
    override val iconId: Int = R.drawable.add_in_playlist
    override val messageId: Int = R.string.playlist_create_dialog_title
    override val dialogTitle: String
        @Composable
        get() = stringResource( R.string.playlist_name_input_hint)
    override val menuIconTitle: String
        @Composable
        get() = stringResource( R.string.playlist_action_create )

    override var value: TextFieldValue by valueState
    override var isActive: Boolean by activeState

    override fun onShortClick() = showDialog()

    override fun hideDialog() {
        super.hideDialog()
        // TODO: Add a random name generator here
        value = value.copy( "" )
    }

    @Composable
    override fun LeadingIcon() = Icon(
        imageVector = Icons.Outlined.Edit,
        tint = colorPalette().accent,
        contentDescription = stringResource(R.string.accessibility_new_playlist_name)
    )

    override fun onSet( newValue: String ) {
        super.onSet( newValue )
        if( errorMessage.isNotEmpty() ) return

        if (isYouTubeSyncEnabled()) {
            val pushPlaylist = appContext().preferences.getBoolean(syncPushPlaylistKey, false)
            val syncDirection = getSyncDirection()
            if (pushPlaylist && syncDirection != SyncDirection.YT_TO_APP && isNetworkConnected(appContext())) {
                hideDialog()
                coroutineScope.launch(MousikeDispatchers.DATA) {
                    val playlist = runCatching {
                        YtMusic.createPlaylist(newValue).getOrNull()?.let { browseId ->
                            Playlist(
                                name = newValue,
                                browseId = browseId,
                                isYoutubePlaylist = true,
                                isEditable = true
                            )
                        }
                    }.onFailure { e ->
                        Timber.tag("NewPlaylistDialog").e(e, "Failed to create playlist on YouTube")
                        Toaster.e( R.string.common_error_title )
                    }.getOrNull() ?: Playlist(name = newValue)

                    Database.asyncTransaction {
                        val newId = playlistTable.insert( playlist )
                        onPlaylistCreated(playlist.copy(id = newId))
                    }
                    Toaster.done()
                    Timber.tag("NewPlaylistDialog").d("Playlist created: ${playlist.name}, browseId=${playlist.browseId}")
                }
            } else {
                hideDialog()
                createLocalPlaylist(newValue)
            }
        } else {
            hideDialog()
            createLocalPlaylist(newValue)
        }
    }

    private fun createLocalPlaylist(name: String) {
        val playlist = Playlist(name = name)
        Database.asyncTransaction {
            val newId = playlistTable.insert( playlist )
            onPlaylistCreated(playlist.copy(id = newId))
        }
        Toaster.done()
    }
}
