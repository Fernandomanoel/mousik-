package app.mousike.android.components.settings.network

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import app.mousike.android.R
import app.mousike.android.components.settings.DownloadQualityEntry
import app.mousike.android.components.settings.UpdateDownloadsButton
import app.mousike.android.components.settings.common.SettingsSectionCard
import app.mousike.android.core.coil.ImageCacheFactory

@UnstableApi
@Composable
internal fun NetworkDownloadQualityCard(
    query: String,
    networkQuality: ImageCacheFactory.NetworkQuality,
    onRestartRequired: (Boolean) -> Unit,
) {
    SettingsSectionCard(
        title = stringResource(R.string.downloads_quality),
        icon = R.drawable.audio_quality,
        content = {
            if (query.isBlank() || stringResource(R.string.downloads_quality_format).contains(query, true) || stringResource(R.string.downloads_action_refresh_quality).contains(query, true)) {
                DownloadQualityEntry(automaticQualityText = automaticQualityStatusText(networkQuality), onQualityChanged = { onRestartRequired(true) })
                UpdateDownloadsButton()
            }
        },
    )
}
