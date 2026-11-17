package app.mousike.android.components.settings.other

import app.mousike.android.utils.defaultFolderKey
import app.mousike.android.utils.isKeepScreenOnEnabledKey
import app.mousike.android.utils.isProxyEnabledKey
import app.mousike.android.utils.logDebugEnabledKey
import app.mousike.android.utils.parentalControlEnabledKey
import app.mousike.android.utils.proxyHostnameKey
import app.mousike.android.utils.proxyModeKey
import app.mousike.android.utils.proxyPasswordEncryptedKey
import app.mousike.android.utils.proxyPortKey
import app.mousike.android.utils.proxyUsernameKey
import app.mousike.android.utils.regionOverrideKey
import app.mousike.android.utils.showFoldersOnDeviceKey

/** Stable inventory for future settings search integration. */
internal object OtherSettingTargets {
    val cards = listOf("other.on-device", "other.service-lifetime", "other.proxy", "other.parental-control", "other.maintenance", "other.extensions", "other.rescue", "other.reset")
    val preferenceKeys = listOf(defaultFolderKey, showFoldersOnDeviceKey, isKeepScreenOnEnabledKey, isProxyEnabledKey, proxyHostnameKey, proxyPortKey, proxyModeKey, proxyUsernameKey, proxyPasswordEncryptedKey, regionOverrideKey, parentalControlEnabledKey, logDebugEnabledKey)
    val actions = listOf("other.on-device.blacklist", "other.service-lifetime.battery-optimization", "other.maintenance.open", "other.maintenance.export-logs", "other.extensions.open", "other.rescue.open", "other.reset.apply")
    val focusAliases = mapOf("debug" to "other.maintenance", "maintenance" to "other.maintenance", "extensions" to "other.extensions", "rescue" to "other.rescue")
}
