package app.mousike.android.components.dialog.tab

import app.mousike.android.appTextResources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import app.mousike.android.R
import app.mousike.android.LocalPlayerServiceBinder
import app.mousike.android.appContext
import app.mousike.android.core.database.Database
import app.mousike.android.utils.coroutines.MousikeDispatchers
import kotlinx.coroutines.launch
import app.mousike.android.models.Song
import app.mousike.android.download.utils.MyDownloadHelper
import app.mousike.android.playback.services.PlayerServiceModern
import app.mousike.android.ui.components.tab.toolbar.Descriptive
import app.mousike.android.ui.components.tab.toolbar.MenuIcon
import app.mousike.android.utils.asMediaItem
import app.mousike.android.components.dialog.media.MediaDownloadDialog
import timber.log.Timber

@UnstableApi
class DeleteAllDownloadedSongsDialog(
    activeState: MutableState<Boolean>,
    getSongs: () -> List<Song>,
    binder: PlayerServiceModern.Binder?
) : MediaDownloadDialog(activeState, getSongs, binder), MenuIcon, Descriptive {

    companion object {
        @Composable
        operator fun invoke( getSongs: () -> List<Song> ) =
            DeleteAllDownloadedSongsDialog(
                remember { mutableStateOf(false) },
                getSongs,
                LocalPlayerServiceBinder.current
            )
    }

    override val confirmationTitle: String
        @Composable get() = stringResource(R.string.downloads_delete_dialog_title)
    override val confirmationAction: String
        @Composable get() = stringResource(R.string.common_action_delete)

    override val messageId: Int = R.string.downloads_action_remove_all
    override val iconId: Int = R.drawable.download
    override val dialogTitle: String
        @Composable
        get() {
            val count = getSongs().count { song -> MyDownloadHelper.downloads.value.containsKey(song.id) }
            return if( count > 0 )
                stringResource( R.string.downloads_delete_all_count_confirmation, count )
            else
                stringResource( R.string.downloads_delete_all_confirmation )
        }
    override val menuIconTitle: String
        @Composable
        get() = stringResource( messageId )

    override fun onShortClick() {
        val count = getSongs().count { song -> MyDownloadHelper.downloads.value.containsKey(song.id) }
        if (count > 0) {
            super.onShortClick()
        } else {
            app.mousike.android.core.utils.Toaster.toast(
                appTextResources().getString(R.string.downloads_nothing_to_delete),
                type = app.mousike.android.core.utils.Toaster.Type.INFO
            )
        }
    }

    override fun onConfirm() {
        // Reverse filter: process both completed and ongoing downloads for deletion
        val downloadedSongs = getSongs().filter { song ->
            MyDownloadHelper.downloads.value.containsKey(song.id)
        }

        if (downloadedSongs.isEmpty()) {
            Timber.tag("DeleteAllDownloadedSongsDialog").d("No downloaded songs to delete")
            onDismiss()
            return
        }

        onDismiss()
        MousikeDispatchers.fireAndForget(MousikeDispatchers.DATA).launch {
            downloadedSongs.forEach {
                MyDownloadHelper.removeDownload( appContext(), it.asMediaItem )
            }
        }
    }

    override fun onAction( media: Song ) =
        MyDownloadHelper.removeDownload( appContext(), media.asMediaItem )
}


