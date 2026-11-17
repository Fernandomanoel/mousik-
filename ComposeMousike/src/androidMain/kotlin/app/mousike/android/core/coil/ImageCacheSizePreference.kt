package app.mousike.android.core.coil

import android.content.SharedPreferences
import app.mousike.android.enums.CoilDiskCacheMaxSize
import app.mousike.android.utils.coilDiskCacheMaxSizeKey
import app.mousike.android.utils.exoPlayerCustomCacheKey
import app.mousike.android.utils.getEnum

internal const val PROFILE_IMAGE_CACHE_CUSTOM_MB = "coilCustomDiskCache"

internal fun imageCacheCustomMb(preferences: SharedPreferences): Int =
    preferences.getInt(PROFILE_IMAGE_CACHE_CUSTOM_MB, preferences.getInt(exoPlayerCustomCacheKey, 32))

internal fun materializeImageCacheCustomMb(preferences: SharedPreferences) {
    if (!preferences.contains(PROFILE_IMAGE_CACHE_CUSTOM_MB) && preferences.contains(exoPlayerCustomCacheKey)) {
        check(preferences.edit().putInt(PROFILE_IMAGE_CACHE_CUSTOM_MB, imageCacheCustomMb(preferences)).commit()) {
            "Could not separate the custom cache limits"
        }
    }
}

/** Match the custom size shown in Data settings even when no custom size was saved. */
internal fun configuredImageCacheBytes(preferences: SharedPreferences): Long =
    when (val size = preferences.getEnum(coilDiskCacheMaxSizeKey, CoilDiskCacheMaxSize.`128MB`)) {
        CoilDiskCacheMaxSize.Custom -> imageCacheCustomMb(preferences) * 1_000_000L
        else -> size.bytes
    }
