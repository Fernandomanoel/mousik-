package app.mousike.android.components.settings.other

import java.net.Proxy
import android.content.Context
import androidx.core.content.edit
import app.mousike.innertube.Innertube
import app.mousike.android.utils.defaultFolderKey
import app.mousike.android.utils.isKeepScreenOnEnabledKey
import app.mousike.android.utils.isProxyEnabledKey
import app.mousike.android.utils.logDebugEnabledKey
import app.mousike.android.utils.otherLanguageAppAlbumKey
import app.mousike.android.utils.otherLanguageAppArtistKey
import app.mousike.android.utils.otherLanguageAppPlaylistKey
import app.mousike.android.utils.parentalControlEnabledKey
import app.mousike.android.utils.preferences
import app.mousike.android.utils.proxyHostnameKey
import app.mousike.android.utils.proxyModeKey
import app.mousike.android.utils.proxyPortKey
import app.mousike.android.utils.putEnum
import app.mousike.android.utils.regionOverrideKey
import app.mousike.android.utils.showFoldersOnDeviceKey
import app.mousike.android.core.settings.defaultProxyPort

internal fun applyOtherDefaults(context: Context) {
    context.preferences.edit {
        putBoolean(isProxyEnabledKey, false)
        putString(proxyHostnameKey, "")
        putInt(proxyPortKey, defaultProxyPort)
        putEnum(proxyModeKey, Proxy.Type.HTTP)
        putString(regionOverrideKey, "")
        putString(defaultFolderKey, "/")
        putBoolean(isKeepScreenOnEnabledKey, false)
        putBoolean(showFoldersOnDeviceKey, true)
        putBoolean(parentalControlEnabledKey, false)
        putBoolean(logDebugEnabledKey, false)
        remove(otherLanguageAppAlbumKey)
        remove(otherLanguageAppArtistKey)
        remove(otherLanguageAppPlaylistKey)
    }
    Innertube.regionOverride = ""
    Innertube.regionOverrideActive = false
}
