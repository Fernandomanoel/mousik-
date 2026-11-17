package app.mousike.android.components.settings.network

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import app.mousike.android.LocalPlayerServiceBinder
import app.mousike.android.components.dialog.export.ExportCacheDialog
import app.mousike.android.core.database.Database
import app.mousike.android.download.utils.MyDownloadHelper
import app.mousike.android.utils.coroutines.MousikeDispatchers
import app.mousike.android.utils.preferences
import app.mousike.android.utils.rememberPreference
import kotlinx.coroutines.withContext

/** Folder (SAF tree URI) where finished downloads are copied, so they survive an uninstall. Empty = off. */
const val downloadsBackupFolderKey = "downloadsBackupFolder"
private const val downloadsBackupDoneKey = "downloadsBackupDone"

/**
 * Copies every completed download that was not copied yet to [downloadsBackupFolderKey].
 * Runs while the app UI is open; downloads finished with the app closed are copied on next open.
 */
@OptIn(UnstableApi::class)
@Composable
fun DownloadsBackupEffect() {
    val binder = LocalPlayerServiceBinder.current
    val context = LocalContext.current
    val folder by rememberPreference(downloadsBackupFolderKey, "")
    val downloads by MyDownloadHelper.downloads.collectAsStateWithLifecycle()
    // In-memory: a failed copy is retried next session instead of in a loop.
    val attempted = remember { mutableSetOf<String>() }

    LaunchedEffect(folder, downloads, binder) {
        if (folder.isBlank() || binder == null) return@LaunchedEffect
        val prefs = context.preferences
        val done = prefs.getStringSet(downloadsBackupDoneKey, emptySet()).orEmpty()
        val pending = downloads.values
            .filter { it.state == Download.STATE_COMPLETED }
            .map { it.request.id }
            .filter { it !in done && it !in attempted }
        if (pending.isEmpty()) return@LaunchedEffect
        attempted += pending

        val songs = withContext(MousikeDispatchers.DATA) { Database.songTable.findByIdsDirect(pending) }
        if (songs.isEmpty()) return@LaunchedEffect
        ExportCacheDialog.batchExport(
            folderUri = Uri.parse(folder),
            songs = songs,
            binder = binder,
            onProgress = { _, _, _ -> },
            onComplete = { _, failed ->
                if (failed == 0)
                    prefs.edit().putStringSet(downloadsBackupDoneKey, done + songs.map { it.id }).apply()
            }
        )
    }
}
