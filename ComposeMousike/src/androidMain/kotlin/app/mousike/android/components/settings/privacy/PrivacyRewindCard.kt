package app.mousike.android.components.settings.privacy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.mousike.android.R
import app.mousike.android.components.settings.common.OtherSwitchSettingEntry
import app.mousike.android.components.settings.common.SettingsSectionCard
import app.mousike.android.components.settings.common.settingsSearchTarget
import app.mousike.android.utils.DataStoreUtils
import app.mousike.android.utils.rememberDataStoreBooleanPreference

@Composable
fun PrivacyRewindCard() {
    val context = LocalContext.current
    val enabled by rememberDataStoreBooleanPreference(DataStoreUtils.KEY_REWIND_ENABLED, true)
    SettingsSectionCard(
        title = stringResource(R.string.rewind_settings_enabled), icon = R.drawable.sparkles,
        content = {
            OtherSwitchSettingEntry(
                title = stringResource(R.string.rewind_settings_enabled),
                text = stringResource(R.string.rewind_settings_enabled_description),
                isChecked = enabled,
                onCheckedChange = {
                    DataStoreUtils.saveBoolean(context, DataStoreUtils.KEY_REWIND_ENABLED, it)
                    DataStoreUtils.saveBoolean(context, DataStoreUtils.KEY_REWIND_MONTHLY_PLAYLIST_ENABLED, it)
                    DataStoreUtils.saveBoolean(context, DataStoreUtils.KEY_REWIND_YEARLY_PLAYLIST_ENABLED, it)
                },
                icon = R.drawable.sparkles,
                modifier = Modifier.settingsSearchTarget("ai.tab_general.rw_settings_master"),
            )
        },
    )
}
