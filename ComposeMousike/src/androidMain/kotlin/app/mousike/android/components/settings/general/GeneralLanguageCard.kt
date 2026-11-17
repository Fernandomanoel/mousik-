package app.mousike.android.components.settings.general

import app.mousike.android.components.dialog.common.DialogPresence
import app.mousike.android.components.settings.common.settingsSearchTarget
import androidx.compose.ui.Modifier
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.LocaleManagerCompat
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import app.mousike.android.enums.Languages
import app.mousike.android.utils.languageAppKey
import app.mousike.android.utils.rememberPreference
import app.mousike.android.R
import app.mousike.android.core.database.Database
import app.mousike.android.utils.appLanguageForLocales
import app.mousike.android.utils.localeListForAppLanguage
import app.mousike.android.utils.shouldRecreateActivity
import app.mousike.android.components.dialog.common.ValueSelectorDialog
import app.mousike.android.components.settings.common.OtherSettingsEntry
import app.mousike.android.components.settings.common.SettingsSectionCard

/** Language card: app language entry, dialog title shows the current system locale. */
@Composable
fun GeneralLanguageCard(
    visible: Boolean,
) {
    var languageApp by rememberPreference(languageAppKey, Languages.System)
    val context = LocalContext.current
    val systemLocale = LocaleManagerCompat.getSystemLocales(context)[0]?.toLanguageTag()
    val configuration = LocalConfiguration.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var appLocales by remember(configuration) { mutableStateOf(AppCompatDelegate.getApplicationLocales()) }

    // A system override can change without recreating this screen if the effective locale is unchanged.
    DisposableEffect(lifecycle, configuration) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) appLocales = AppCompatDelegate.getApplicationLocales()
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    val appliedLanguage = appLanguageForLocales(appLocales)

    var showLanguageDialog by remember { mutableStateOf(false) }

    SettingsSectionCard(
        title = stringResource(R.string.settings_languages_title),
        icon = R.drawable.globe,
        visible = visible,
        content = {
            OtherSettingsEntry(
                modifier = Modifier.settingsSearchTarget("general.languages.app_language"),
                title = stringResource(R.string.settings_app_language_label),
                text = stringResource(R.string.language_app_selection_description),
                selectedValueText = appliedLanguage?.text ?: appLocales[0]?.let { it.getDisplayName(it) },
                onClick = { showLanguageDialog = true },
                icon = R.drawable.translate
            )

            DialogPresence(visible = showLanguageDialog) {
                ValueSelectorDialog(
                    title = stringResource(R.string.settings_app_language_label) + systemLocale?.let { ": $it" }.orEmpty(),
                    selectedValue = appliedLanguage,
                    onValueSelected = { selected ->
                        if (selected != null && shouldRecreateActivity(Database.isClosed)) {
                            languageApp = selected
                            val locales = localeListForAppLanguage(selected)
                            if (AppCompatDelegate.getApplicationLocales() != locales) {
                                AppCompatDelegate.setApplicationLocales(locales)
                            }
                            appLocales = locales
                        }
                    },
                    valueText = { it?.text.orEmpty() },
                    values = Languages.values().toList(),
                    onDismiss = { showLanguageDialog = false }
                )
            }
        }
    )
}
