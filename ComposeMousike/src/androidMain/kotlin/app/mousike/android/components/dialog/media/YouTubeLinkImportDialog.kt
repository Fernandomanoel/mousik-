package app.mousike.android.components.dialog.media

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import app.mousike.android.components.glass.GlassTextField as TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import app.mousike.android.R
import app.mousike.android.utils.semiBold
import app.mousike.android.colorPalette
import app.mousike.android.typography
import app.mousike.android.components.dialog.common.ButtonRow
import app.mousike.android.components.dialog.common.DialogButton
import app.mousike.android.components.dialog.common.DialogFrame
import app.mousike.android.components.dialog.common.InputDialog
import app.mousike.android.components.dialog.common.LocalDialogInput
import app.mousike.android.components.dialog.common.dialogInputEnabled
import androidx.core.net.toUri

/**
 * Centralized YouTube link import dialog.
 * Takes a URL and calls [onImport] with the extracted playlist ID.
 *
 * @param onImport Called with the extracted playlist ID (e.g. "PLxxx...")
 * @param onDismiss Called when the dialog is dismissed
 */
@Composable
fun YouTubeLinkImportDialog(
    onImport: (playlistId: String) -> Unit,
    onDismiss: () -> Unit
) {
    var youTubeUrl by remember { mutableStateOf("") }

    DialogFrame(
        onDismiss = onDismiss,
        title = stringResource(R.string.playlist_import_youtube_link),
        footer = {
            ButtonRow(
                buttons = listOf(
                    DialogButton(
                        text = stringResource(R.string.common_action_cancel),
                        onClick = onDismiss,
                        danger = true
                    ),
                    DialogButton(
                        text = stringResource(R.string.common_action_import),
                        onClick = {
                            val playlistId = extractYouTubePlaylistId(youTubeUrl)
                            if (playlistId != null) {
                                onImport(playlistId)
                                onDismiss()
                            }
                        },
                        primary = true
                    )
                )
            )
        }
    ) {
        val presentation = LocalDialogInput.current
        TextField(
            enabled = dialogInputEnabled,
            value = youTubeUrl,
            onValueChange = { youTubeUrl = it },
            placeholder = { BasicText("https://youtube.com/playlist?list=...", style = typography().xs.semiBold.copy(color = colorPalette().textDisabled)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                val playlistId = extractYouTubePlaylistId(youTubeUrl)
                if (presentation?.interactive != false && playlistId != null) {
                    onImport(playlistId)
                    onDismiss()
                }
            }),
            colors = InputDialog.defaultTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun extractYouTubePlaylistId(url: String): String? {
    val prefixes = listOf(
        "https://www.youtube.com/playlist?",
        "https://youtube.com/playlist?",
        "https://music.youtube.com/playlist?",
        "https://m.youtube.com/playlist?"
    )
    return prefixes.find { url.startsWith(it) }
        ?.let { url.trim().toUri().getQueryParameter("list") }
}
