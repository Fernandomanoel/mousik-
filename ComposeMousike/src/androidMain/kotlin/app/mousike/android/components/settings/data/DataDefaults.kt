package app.mousike.android.components.settings.data

import android.content.Context
import androidx.core.content.edit
import app.mousike.android.enums.CoilDiskCacheMaxSize
import app.mousike.android.enums.ExoPlayerCacheLocation
import app.mousike.android.enums.ExoPlayerDiskCacheMaxSize
import app.mousike.android.enums.ExoPlayerDiskDownloadCacheMaxSize
import app.mousike.android.core.coil.PROFILE_IMAGE_CACHE_CUSTOM_MB
import app.mousike.android.utils.coilDiskCacheMaxSizeKey
import app.mousike.android.utils.exoPlayerCustomCacheKey
import app.mousike.android.utils.exoPlayerDiskCacheMaxSizeKey
import app.mousike.android.utils.exoPlayerDiskDownloadCacheMaxSizeKey
import app.mousike.android.utils.pauseListenHistoryKey
import app.mousike.android.utils.pauseSearchHistoryKey
import app.mousike.android.utils.getActiveProfile
import app.mousike.android.utils.putEnum
import app.mousike.android.components.search.SearchHistoryStore
import app.mousike.android.core.backup.DatabaseRestoreJournal
import app.mousike.android.core.profiles.ProfileCacheLocationMigration
import app.mousike.android.core.profiles.profileDataPrefs
import app.mousike.android.core.profiles.profileDownloadSpanBytes
import app.mousike.android.core.profiles.profileStorageIdentity

/** Restores the original eight Data defaults once for the active profile. */
internal fun applyDataDefaults(context: Context) = DatabaseRestoreJournal.withProfileStorageMutation(context) {
    val target = checkNotNull(profileStorageIdentity(context, getActiveProfile(context)))
    val preferences = profileDataPrefs(context, target.id)
    preferences.edit {
        putEnum(coilDiskCacheMaxSizeKey, CoilDiskCacheMaxSize.`128MB`)
        putEnum(exoPlayerDiskCacheMaxSizeKey, ExoPlayerDiskCacheMaxSize.`2GB`)
        if (runCatching { profileDownloadSpanBytes(context, target.id) <= ExoPlayerDiskDownloadCacheMaxSize.`2GB`.bytes }
                .getOrDefault(false)
        ) {
            putEnum(exoPlayerDiskDownloadCacheMaxSizeKey, ExoPlayerDiskDownloadCacheMaxSize.`2GB`)
        }
        ProfileCacheLocationMigration.stageRequest(preferences, this, target, ExoPlayerCacheLocation.System)
        putInt(exoPlayerCustomCacheKey, 32)
        putInt(PROFILE_IMAGE_CACHE_CUSTOM_MB, 32)
        putBoolean(pauseSearchHistoryKey, false)
        putBoolean(pauseListenHistoryKey, false)
        SearchHistoryStore.pages.forEach { page -> remove(SearchHistoryStore.enabledKey(page)) }
    }
}
