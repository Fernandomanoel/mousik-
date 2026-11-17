package app.mousike.android.components.settings.accounts

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import androidx.media3.common.util.UnstableApi
import app.mousike.android.utils.enableYouTubeLoginKey
import app.mousike.android.utils.enableYouTubeSyncKey
import app.mousike.android.utils.encryptedPreferences
import app.mousike.android.utils.isDiscordPresenceEnabledKey
import app.mousike.android.utils.preferences
import app.mousike.android.utils.restartActivityKey
import app.mousike.android.core.utils.Toaster
import app.mousike.android.R
import app.mousike.android.components.settings.common.OtherSettingsEntry
import app.mousike.android.components.settings.common.SettingsSectionCard
import app.mousike.android.components.settings.common.resetPlaylistRefreshDefaults
import app.mousike.android.components.settings.common.settingsCardEnter
import app.mousike.android.components.settings.common.settingsCardExit
import app.mousike.android.components.settings.common.settingsSearchTarget
import app.mousike.android.components.tab.Search
import app.mousike.android.extensions.discord.discordAdvancedActivityTypeKey
import app.mousike.android.extensions.discord.discordAdvancedButton1EnabledKey
import app.mousike.android.extensions.discord.discordAdvancedButton1LabelKey
import app.mousike.android.extensions.discord.discordAdvancedButton1UrlKey
import app.mousike.android.extensions.discord.discordAdvancedButton2EnabledKey
import app.mousike.android.extensions.discord.discordAdvancedButton2LabelKey
import app.mousike.android.extensions.discord.discordAdvancedButton2UrlKey
import app.mousike.android.extensions.discord.discordAdvancedDetailsTemplateKey
import app.mousike.android.extensions.discord.discordAdvancedNameKey
import app.mousike.android.extensions.discord.discordAdvancedPausePresenceEnabledKey
import app.mousike.android.extensions.discord.discordAdvancedPauseTemplateKey
import app.mousike.android.extensions.discord.discordAdvancedStateTemplateKey
import app.mousike.android.extensions.discord.isDiscordAdvancedModeKey

/** Applies only the existing Accounts reset set; saved credentials remain intact. */
internal fun applyAccountsDefaults(context: Context) {
    resetPlaylistRefreshDefaults(context.preferences)
    context.preferences.edit {
        putBoolean(restartActivityKey, false)
    }
    context.encryptedPreferences.edit {
        putBoolean(enableYouTubeLoginKey, false)
        putBoolean(enableYouTubeSyncKey, false)
        putBoolean(isDiscordPresenceEnabledKey, false)
        putBoolean(isDiscordAdvancedModeKey, false)
        putInt(discordAdvancedActivityTypeKey, 2)
        putBoolean(discordAdvancedPausePresenceEnabledKey, true)
        putString(discordAdvancedNameKey, "")
        putString(discordAdvancedStateTemplateKey, "")
        putString(discordAdvancedDetailsTemplateKey, "")
        putString(discordAdvancedPauseTemplateKey, "")
        putBoolean(discordAdvancedButton1EnabledKey, true)
        putString(discordAdvancedButton1LabelKey, "")
        putString(discordAdvancedButton1UrlKey, "")
        putBoolean(discordAdvancedButton2EnabledKey, true)
        putString(discordAdvancedButton2LabelKey, "")
        putString(discordAdvancedButton2UrlKey, "")
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
internal fun AccountsResetCard(search: Search) {
    val context = LocalContext.current
    val searchCtx_Reset = search.inputValue.isBlank() || stringResource(R.string.settings_reset).contains(search.inputValue, true) || stringResource(R.string.settings_restore_default_settings).contains(search.inputValue, true)
    AnimatedVisibility(
        visible = searchCtx_Reset,
        enter = settingsCardEnter,
        exit = settingsCardExit
    ) {
        SettingsSectionCard(
            title = stringResource(R.string.settings_reset),
            icon = R.drawable.refresh,
            content = {

                if (search.inputValue.isBlank() || stringResource(R.string.settings_restore_default_settings).contains(search.inputValue, true) || stringResource(R.string.settings_reset).contains(search.inputValue, true)) {
                    OtherSettingsEntry(
                        modifier = Modifier.settingsSearchTarget("accounts.settings_reset.settings_reset"),
                        title = stringResource(R.string.settings_reset),
                        text = stringResource(R.string.settings_restore_default_settings),
                        icon = R.drawable.refresh,
                        onClick = {
                            applyAccountsDefaults(context)
                            Toaster.done()
                        }
                    )
                }
            }
        )
    }
}
