package app.mousike.android.components.settings.data

import app.mousike.android.core.coil.PROFILE_IMAGE_CACHE_CUSTOM_MB
import app.mousike.android.utils.coilDiskCacheMaxSizeKey
import app.mousike.android.utils.exoPlayerCacheLocationKey
import app.mousike.android.utils.exoPlayerCustomCacheKey
import app.mousike.android.utils.exoPlayerDiskCacheMaxSizeKey
import app.mousike.android.utils.exoPlayerDiskDownloadCacheMaxSizeKey
import app.mousike.android.utils.pauseListenHistoryKey
import app.mousike.android.utils.pauseSearchHistoryKey

/** Stable local card, preference and action targets for the Data page. */
internal object DataSettingTargets {
    const val HISTORY_FOCUS = "history"
    val cards = listOf("data.cache", "data.backup", "data.auto-backup", "data.history", "data.reset")
    val preferenceKeys = listOf(coilDiskCacheMaxSizeKey, exoPlayerDiskCacheMaxSizeKey, exoPlayerDiskDownloadCacheMaxSizeKey, exoPlayerCacheLocationKey, exoPlayerCustomCacheKey, PROFILE_IMAGE_CACHE_CUSTOM_MB, pauseSearchHistoryKey, pauseListenHistoryKey)
    val actions = listOf("data.cache.clear-images", "data.cache.clear-songs", "data.cache.clear-downloads", "data.backup.export", "data.backup.import", "data.backup.migrate", "data.history.open", "data.history.clear-search", "data.history.clear-listen", "data.reset.apply")
}
