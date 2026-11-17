package app.mousike.android.components.dialog.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.mousike.android.R
import app.mousike.android.models.Info
import app.mousike.android.utils.ExternalUris

/** "Listen on" dialog: opens the current media on an external app (YouTube / YouTube Music / Invidious) via the shared [ValueSelectorDialog]. */
@Composable
fun ListenOnDialog(
    mediaId: String,
    onDismiss: () -> Unit,
    onPlayOnUrl: (String) -> Unit
) {
    ValueSelectorDialog(
        title = stringResource(R.string.player_listen_on_title),
        onDismiss = onDismiss,
        selectedValue = Info("none", null),
        values = listOf(
            Info(
                ExternalUris.youtube(mediaId),
                stringResource(R.string.player_action_listen_on_youtube)
            ),
            Info(
                ExternalUris.youtubeMusic(mediaId),
                stringResource(R.string.player_action_listen_on_youtube_music)
            ),

            Info(
                ExternalUris.invidious(mediaId),
                stringResource(
                    R.string.player_action_listen_on_invidious,
                    stringResource(R.string.player_brand_invidious)
                )
            )
        ),
        onValueSelected = {
            onPlayOnUrl(it.id)
        },
        valueText = { it.name ?: "" }
    )
}
