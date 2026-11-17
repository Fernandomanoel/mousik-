package app.mousike.android.components.settings.general

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.mousike.android.utils.disableClosingPlayerSwipingDownKey
import app.mousike.android.utils.keepPlayerMinimizedKey
import app.mousike.android.utils.rememberPreference
import app.mousike.android.R
import app.mousike.android.components.settings.common.OtherSwitchSettingEntry
import app.mousike.android.components.settings.common.SettingsSectionCard
import app.mousike.android.components.settings.common.settingsSearchTarget

/** Player Controls card: minimized player and swipe behavior. */
@Composable
fun GeneralPlayerControlsCard(
    visible: Boolean,
) {
    var keepPlayerMinimized by rememberPreference(keepPlayerMinimizedKey, true)
    var disableClosingPlayerSwipingDown by rememberPreference(disableClosingPlayerSwipingDownKey, false)

    SettingsSectionCard(
        title = stringResource(R.string.player_controls),
        icon = R.drawable.player_control,
        visible = visible,
        content = {
            OtherSwitchSettingEntry(
                modifier = Modifier.settingsSearchTarget("general.player_controls.player_keep_minimized"),
                title = stringResource(R.string.player_keep_minimized),
                text = stringResource(R.string.settings_player_start_minimized_description),
                isChecked = keepPlayerMinimized,
                onCheckedChange = { keepPlayerMinimized = it },
                icon = R.drawable.maximize
            )

            OtherSwitchSettingEntry(
                modifier = Modifier.settingsSearchTarget("general.player_controls.player_collapsed_disable_swiping_down"),
                title = stringResource(R.string.player_collapsed_disable_swiping_down),
                text = stringResource(R.string.settings_player_prevent_collapsed_dismissal_description),
                isChecked = disableClosingPlayerSwipingDown,
                onCheckedChange = { disableClosingPlayerSwipingDown = it },
                icon = R.drawable.reorder
            )

        }
    )
}
