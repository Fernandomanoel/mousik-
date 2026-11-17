package app.mousike.android.extensions.youtubelogin

import android.content.SharedPreferences

import androidx.core.content.edit

import app.mousike.android.core.network.client.Store
import app.mousike.android.playback.services.clearStreamCaches
import app.mousike.android.utils.cancelAllSyncs
import app.mousike.android.utils.enableYouTubeLoginKey
import app.mousike.android.utils.enableYouTubeSyncKey
import app.mousike.android.utils.encryptedPreferencesUpdateTrigger
import app.mousike.android.utils.ytAccountChannelHandleKey
import app.mousike.android.utils.ytAccountEmailKey
import app.mousike.android.utils.ytAccountNameKey
import app.mousike.android.utils.ytAccountThumbnailKey
import app.mousike.android.utils.ytAuthUserKey
import app.mousike.android.utils.ytCookieKey
import app.mousike.android.utils.ytDataSyncIdKey
import app.mousike.android.utils.ytVisitorDataKey
import app.mousike.innertube.Innertube
import app.mousike.innertube.YouTubeSession
import app.mousike.innertube.models.AccountInfo
import app.mousike.innertube.normalizeYouTubeAuthUser

internal fun SharedPreferences.readYouTubeSession(): YouTubeSession {
    val cookie = getString(ytCookieKey, null)?.takeIf(String::isNotBlank)
    return YouTubeSession(
        cookie = cookie,
        visitorData = getString(ytVisitorDataKey, null)?.takeIf(String::isNotBlank),
        dataSyncId = cookie?.let { getString(ytDataSyncIdKey, null)?.takeIf(String::isNotBlank) },
        authUser = cookie?.let { normalizeYouTubeAuthUser(getString(ytAuthUserKey, null)) },
    )
}

internal fun SharedPreferences.Editor.putYouTubeSession(session: YouTubeSession) {
    putString(ytCookieKey, session.cookie)
    putString(ytVisitorDataKey, session.visitorData)
    putString(ytDataSyncIdKey, session.dataSyncId)
    putString(ytAuthUserKey, session.authUser)
}

internal fun saveVerifiedYouTubeSession(
    prefs: SharedPreferences,
    session: YouTubeSession,
    account: AccountInfo,
    webProfileName: String?,
    knownAccounts: List<YouTubeBrowserAccount>,
) {
    val previous = prefs.readYouTubeSession()
    val changedAccount = previous.cookie != null && (
        previous.dataSyncId != session.dataSyncId ||
            (previous.authUser ?: "0") != (session.authUser ?: "0") ||
            previous.dataSyncId == null && previous.cookie != session.cookie
        )
    cancelAllSyncs()
    prefs.edit {
        putYouTubeSession(session)
        putYouTubeBrowserAccount(webProfileName, account, knownAccounts)
        putString(ytAccountNameKey, account.name)
        putString(ytAccountEmailKey, account.email)
        putString(ytAccountChannelHandleKey, account.channelHandle)
        putString(ytAccountThumbnailKey, account.thumbnailUrl)
        putBoolean(enableYouTubeLoginKey, true)
        if (changedAccount) putBoolean(enableYouTubeSyncKey, false)
    }
    Store.setSession(session)
    clearStreamCaches()
    encryptedPreferencesUpdateTrigger++
}

internal fun clearSavedYouTubeSession(prefs: SharedPreferences, clearWebSignIn: Boolean = false) {
    val guest = YouTubeSession(null, null, null, null)
    cancelAllSyncs()
    prefs.edit {
        putYouTubeSession(guest)
        remove(ytAccountNameKey)
        remove(ytAccountEmailKey)
        remove(ytAccountChannelHandleKey)
        remove(ytAccountThumbnailKey)
        putBoolean(enableYouTubeSyncKey, false)
        if (clearWebSignIn) removeAllYouTubeBrowserAccounts()
    }
    Store.setSession(guest)
    Innertube.useLoginForBrowse = false
    clearStreamCaches()
    encryptedPreferencesUpdateTrigger++
    if (clearWebSignIn) clearAllYouTubeBrowserProfiles()
}
