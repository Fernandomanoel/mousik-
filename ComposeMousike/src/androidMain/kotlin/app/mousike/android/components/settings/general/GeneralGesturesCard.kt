package app.mousike.android.components.settings.general

import app.mousike.android.components.settings.common.settingsSearchTarget
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import app.mousike.android.utils.rememberPreference
import app.mousike.android.utils.useVolumeKeysToChangeSongKey
import app.mousike.android.R
import app.mousike.android.components.settings.common.OtherSwitchSettingEntry
import app.mousike.android.components.settings.common.SettingsSectionCard

/** Gestures & Events card: volume keys to change song. */
@Composable
fun GeneralGesturesCard(
    visible: Boolean,
    onRestartServiceSet: () -> Unit,
) {
    var useVolumeKeysToChangeSong by rememberPreference(useVolumeKeysToChangeSongKey, false)

    SettingsSectionCard(
        title = stringResource(R.string.settings_gestures_title),
        icon = R.drawable.gesture,
        visible = visible,
        content = {
            OtherSwitchSettingEntry(
                modifier = Modifier.settingsSearchTarget("general.gestures_events.event_volumekeys"),
                title = stringResource(R.string.settings_gestures_volume_keys_skip_label),
                text = stringResource(R.string.settings_gestures_volume_keys_skip_description),
                isChecked = useVolumeKeysToChangeSong,
                onCheckedChange = {
                    useVolumeKeysToChangeSong = it
                    onRestartServiceSet()
                },
                icon = R.drawable.volume_control
            )
        }
    )
}
