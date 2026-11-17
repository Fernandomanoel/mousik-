package app.mousike.android.components.settings.network

import android.content.Context
import androidx.core.content.edit
import app.mousike.android.enums.AudioQualityFormat
import app.mousike.android.enums.ImageQualityFormat
import app.mousike.android.utils.audioQualityFormatKey
import app.mousike.android.utils.autoDownloadSongKey
import app.mousike.android.utils.autoDownloadSongWhenAlbumBookmarkedKey
import app.mousike.android.utils.autoDownloadSongWhenLikedKey
import app.mousike.android.utils.imageQualityFormatKey
import app.mousike.android.utils.isConnectionMeteredEnabledKey
import app.mousike.android.utils.preferences
import app.mousike.android.utils.putEnum
import app.mousike.android.utils.streamClientAndroidVrEnabledKey
import app.mousike.android.utils.streamClientRestartNeededKey
import app.mousike.android.utils.streamClientWebRemixEnabledKey
import app.mousike.android.bridge.AutoStopSettings
import app.mousike.android.bridge.BridgeServerController
import app.mousike.android.components.dialog.settings.PreferredStreamClientDialog
import app.mousike.android.components.dialog.settings.StreamClientsSettingsDialog
import app.mousike.android.core.network.fullOfflineModeKey
import app.mousike.android.enums.DownloadQualityFormat
import app.mousike.android.enums.downloadQualityFormatKey

internal fun applyNetworkDefaults(context: Context) {
    BridgeServerController.setAutoStopNoClient(context, AutoStopSettings.DEFAULT_NO_CLIENT_MINUTES)
    BridgeServerController.setAutoStopInactivity(context, AutoStopSettings.DEFAULT_INACTIVITY_MINUTES)
    PreferredStreamClientDialog.reset(context)
    StreamClientsSettingsDialog.reset(context)
    context.preferences.edit {
        putBoolean(isConnectionMeteredEnabledKey, false)
        putBoolean(autoDownloadSongKey, false)
        putBoolean(autoDownloadSongWhenLikedKey, false)
        putBoolean(autoDownloadSongWhenAlbumBookmarkedKey, false)
        putEnum(audioQualityFormatKey, AudioQualityFormat.Auto)
        putEnum(downloadQualityFormatKey, DownloadQualityFormat.Auto)
        putEnum(imageQualityFormatKey, ImageQualityFormat.Auto)
        putBoolean(fullOfflineModeKey, false)
        putBoolean(streamClientWebRemixEnabledKey, true)
        putBoolean(streamClientAndroidVrEnabledKey, true)
        putBoolean(streamClientRestartNeededKey, false)
    }
}
