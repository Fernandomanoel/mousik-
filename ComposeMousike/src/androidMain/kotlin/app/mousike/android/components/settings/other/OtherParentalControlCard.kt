package app.mousike.android.components.settings.other

import app.mousike.android.components.settings.common.settingsSearchTarget
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import app.mousike.android.utils.parentalControlEnabledKey
import app.mousike.android.utils.rememberPreference
import app.mousike.android.R
import app.mousike.android.components.settings.common.OtherSwitchSettingEntry
import app.mousike.android.components.settings.common.SettingsSectionCard
import app.mousike.android.components.tab.Search

@UnstableApi
@Composable
internal fun OtherParentalControlCard(search: Search) {
    var parentalControlEnabled by rememberPreference(parentalControlEnabledKey, false)

    SettingsSectionCard(
        title = stringResource(R.string.parental_control),
        icon = R.drawable.parental_control,
        content = {
            if (search.inputValue.isBlank() || stringResource(R.string.parental_control).contains(search.inputValue, true) || stringResource(R.string.settings_parental_age_restriction_description).contains(search.inputValue, true)) {
                OtherSwitchSettingEntry(
                    modifier = Modifier.settingsSearchTarget("other.parental_control.parental_control"),
                    title = stringResource(R.string.parental_control),
                    text = stringResource(R.string.settings_parental_age_restriction_description),
                    isChecked = parentalControlEnabled,
                    onCheckedChange = { parentalControlEnabled = it },
                    icon = R.drawable.parental_control
                )
            }
        }
    )
}
