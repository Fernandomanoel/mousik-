package app.mousike.android.components.settings.ai

import app.mousike.android.components.settings.common.settingsSearchTarget
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.media3.common.util.UnstableApi
import app.mousike.android.core.utils.Toaster
import app.mousike.android.R
import app.mousike.android.components.settings.common.OtherSettingsEntry
import app.mousike.android.components.settings.common.SettingsSectionCard
import app.mousike.android.components.tab.Search

/** Preserves the original Reset settings and actions. */
@UnstableApi
@Composable
internal fun AiResetCard(search: Search) {

    val searchCtx_8 = search.inputValue.isBlank() || stringResource(R.string.settings_reset).contains(search.inputValue, true)
    SettingsSectionCard(
        title = stringResource(R.string.settings_reset),
        icon = R.drawable.refresh,
        visible = searchCtx_8,
        content = {
            val context = LocalContext.current
            if (search.inputValue.isBlank() || stringResource(R.string.settings_reset).contains(search.inputValue, true) || (stringResource(R.string.settings_restore_default_settings)).contains(search.inputValue, true)) {
                OtherSettingsEntry(
                    modifier = Modifier.settingsSearchTarget("ai.settings_reset.settings_reset"),
                    title = stringResource(R.string.settings_reset),
                    text = stringResource(R.string.settings_restore_default_settings),
                    icon = R.drawable.refresh,
                    onClick = { applyAiDefaults(context); Toaster.done() }
                )
            }

        }
    )
}
