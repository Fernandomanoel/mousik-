package app.mousike.android.components.navigation.header

import app.mousike.android.components.settings.appearance.preview.LocalThemePreview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.mousike.android.utils.encryptedPreferencesUpdateTrigger
import app.mousike.android.utils.getActiveProfile
import app.mousike.android.R
import app.mousike.android.components.ui.screens.profiles.loadActiveProfileFace
import app.mousike.android.components.ui.screens.profiles.profileFaceUpdateTrigger
import app.mousike.android.utils.FaceAvatar
import app.mousike.android.utils.ProfileFace
import app.mousike.android.utils.coroutines.MousikeDispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/** The root retains a resolved avatar while side headers are replaced during navigation. */
internal val LocalHeaderProfileFace = compositionLocalOf<ProfileFace?> { null }

internal fun initialHeaderProfileFace(defaultName: String): ProfileFace =
    ProfileFace(defaultName, FaceAvatar.Initials(defaultName))

@Composable
internal fun rememberHeaderProfileFace(): ProfileFace {
    val defaultName = stringResource(R.string.profile_base_name)
    if (LocalThemePreview.current != null) return initialHeaderProfileFace(defaultName)
    val context = LocalContext.current
    val activeProfile = getActiveProfile(context)
    val faceTrigger = encryptedPreferencesUpdateTrigger + profileFaceUpdateTrigger
    var face by remember(activeProfile, defaultName) { mutableStateOf(initialHeaderProfileFace(defaultName)) }
    LaunchedEffect(activeProfile, faceTrigger, defaultName) {
        val resolved = withContext(MousikeDispatchers.DATA) {
            runCatching { loadActiveProfileFace(context, defaultName) }
                .onFailure { Timber.tag("ActionBar").e(it, "Failed to resolve the active profile face") }
                .getOrNull()
        }
        if (resolved != null) face = resolved
    }
    return face
}
