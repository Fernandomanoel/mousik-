package app.mousike.android.core.network

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import app.mousike.android.utils.activeProfileKey
import app.mousike.android.utils.profilePreferences
import app.mousike.android.utils.preferences
import app.mousike.android.utils.rememberPreference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/** Preferences key of the per-profile "full offline mode" toggle (burger menu). */
const val fullOfflineModeKey = "fullOfflineModeEnabled"

/** Profile-owned offline policy shared by phone and automotive clients. */
object OfflineMode {

    /** Non-composable read of the mode flag (profile-scoped, default off). */
    fun enabled(context: Context): Boolean =
        context.preferences.getBoolean(fullOfflineModeKey, false)

    /** Live flow of the mode flag: emits on initial read and on every change. */
    fun enabledFlow(context: Context): Flow<Boolean> = callbackFlow {
        val profilePrefs = context.profilePreferences
        var prefs = context.preferences
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == fullOfflineModeKey) trySend(enabled(context))
        }
        val profileListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == activeProfileKey) {
                prefs.unregisterOnSharedPreferenceChangeListener(listener)
                prefs = context.preferences
                prefs.registerOnSharedPreferenceChangeListener(listener)
                trySend(enabled(context))
            }
        }
        profilePrefs.registerOnSharedPreferenceChangeListener(profileListener)
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(enabled(context))
        awaitClose {
            profilePrefs.unregisterOnSharedPreferenceChangeListener(profileListener)
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }.distinctUntilChanged()

    /** Live, profile-scoped read of the mode flag for composition (listener-backed, like [rememberPreference]). */
    @Composable
    fun rememberEnabled() = rememberPreference(fullOfflineModeKey, false)
}
