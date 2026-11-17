package app.mousike.android.components.settings.privacy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.mousike.android.utils.rememberPreference
import app.mousike.android.R
import app.mousike.android.components.settings.common.OtherSwitchSettingEntry
import app.mousike.android.components.settings.common.SettingsSectionCard
import app.mousike.android.components.settings.common.settingsSearchTarget
import app.mousike.android.privacy.blockScreenCaptureKey

@Composable
fun PrivacyScreenCaptureCard() {
    var blocked by rememberPreference(blockScreenCaptureKey, false)
    SettingsSectionCard(
        title = stringResource(R.string.privacy_screen_protection),
        icon = R.drawable.shield_checkmark,
        content = {
            OtherSwitchSettingEntry(
                title = stringResource(R.string.privacy_block_screenshots),
                text = stringResource(R.string.privacy_block_screenshots_description),
                isChecked = blocked,
                onCheckedChange = { blocked = it },
                icon = R.drawable.shield_checkmark,
                modifier = Modifier.settingsSearchTarget("privacy.screen.block_capture"),
            )
        },
    )
}
