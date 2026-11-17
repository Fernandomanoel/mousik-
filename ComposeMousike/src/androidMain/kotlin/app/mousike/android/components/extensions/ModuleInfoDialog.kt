package app.mousike.android.components.extensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.mousike.android.R
import app.mousike.android.colorPalette
import app.mousike.android.components.dialog.common.ButtonRow
import app.mousike.android.components.dialog.common.DialogButton
import app.mousike.android.components.dialog.common.DialogFrame
import app.mousike.android.typography

/**
 * Module info dialog (spec-ffmpeg-extension-module): title + version/catalog
 * text block + a single OK pill on the shared [DialogFrame].
 */
@Composable
fun ModuleInfoDialog(
    title: String,
    content: String,
    onDismiss: () -> Unit,
) {
    DialogFrame(onDismiss = onDismiss, title = title,
        bodyPadding = PaddingValues(0.dp),
        footer = {
            ButtonRow(
                buttons = listOf(
                    DialogButton(
                        text = stringResource(R.string.common_action_close),
                        onClick = onDismiss,
                        danger = true,
                    ),
                ),
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Scrollable content so the banner never pushes the button row out
            // of the frame (CrashLogDialog pattern)
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()).padding(24.dp),
            ) {
                BasicText(
                    text = content,
                    style = typography().xs.copy(color = colorPalette().textSecondary),
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }
        }
    }
}
