package app.mousike.android.components.settings.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.mousike.android.R
import app.mousike.android.components.settings.common.SettingsSectionCard
import app.mousike.android.components.settings.common.settingsSearchTarget
import app.mousike.android.core.backup.ui.AutoBackupSettingsBlock
import app.mousike.android.core.profiles.ProfileStorageIdentity

/** Reuses the existing automatic backup controls. */
@Composable
internal fun DataAutoBackupCard(target: ProfileStorageIdentity, pageEntryTargets: Set<String>) {
    SettingsSectionCard(
        title = stringResource(R.string.backup_auto_title),
        icon = R.drawable.history,
        headerModifier = Modifier.settingsSearchTarget("embedded.auto_backup.section"),
        content = { AutoBackupSettingsBlock(target, pageEntryTargets) },
    )
}
