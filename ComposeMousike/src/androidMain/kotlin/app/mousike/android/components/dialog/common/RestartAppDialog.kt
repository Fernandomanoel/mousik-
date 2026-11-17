package app.mousike.android.components.dialog.common

import android.app.Activity
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import app.mousike.android.BuildConfig
import app.mousike.android.R
import app.mousike.android.appContext
import app.mousike.android.colorPalette
import app.mousike.android.download.services.MyDownloadService
import app.mousike.android.playback.services.PlayerServiceModern
import app.mousike.android.typography
import app.mousike.android.utils.intent
import kotlin.system.exitProcess

object RestartAppDialog: ConfirmDialog {

    override val dialogTitle: String
        @Composable
        get() = stringResource( R.string.common_restart_required_title )

    override var isActive: Boolean by mutableStateOf(false)
    override fun hideDialog() {
        onConfirm()
    }

    override fun onConfirm() {
        appContext().stopService( appContext().intent<PlayerServiceModern>() )
        appContext().stopService( appContext().intent<MyDownloadService>() )

        // Close other activities
        (appContext() as? Activity)?.finishAffinity()

        // Close app with exit 0 notify that no problem occurred
        exitProcess( 0 )
    }

    @Composable
    override fun Buttons() = ButtonRow(
        buttons = listOf(
            DialogButton(
                text = stringResource(R.string.common_action_close_app),
                onClick = ::onConfirm,
                danger = true
            )
        )
    )

    @Composable
    override fun DialogBody() {
        BasicText(
            text = stringResource( R.string.common_restart_required_message, BuildConfig.APP_NAME ),
            style = typography().xs.copy( color = colorPalette().text )
        )
    }
}




