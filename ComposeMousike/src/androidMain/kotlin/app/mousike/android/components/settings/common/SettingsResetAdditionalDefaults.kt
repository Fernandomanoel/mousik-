package app.mousike.android.components.settings.common

import android.content.Context
import android.content.SharedPreferences
import app.mousike.android.utils.checkUpdateStateKey
import app.mousike.android.utils.checkBetaUpdatesKey
import app.mousike.android.utils.otherLanguageAppUpdateKey
import app.mousike.android.utils.autosyncAlbumsKey
import app.mousike.android.utils.autosyncArtistsKey
import app.mousike.android.utils.autosyncKey
import app.mousike.android.utils.autosyncLikesKey
import app.mousike.android.utils.autosyncPlaylistsKey
import app.mousike.android.utils.encryptedPreferences
import app.mousike.android.utils.playbackDurationKey
import app.mousike.android.utils.playbackPitchKey
import app.mousike.android.utils.playbackSpeedKey
import app.mousike.android.utils.playbackVolumeKey
import app.mousike.android.utils.playerBackdropKey
import app.mousike.android.utils.preferences
import app.mousike.android.utils.syncBackgroundGuardKey
import app.mousike.android.utils.syncCooldownKey
import app.mousike.android.utils.syncDirectionKey
import app.mousike.android.utils.syncImportEpisodesKey
import app.mousike.android.utils.syncImportHistoryKey
import app.mousike.android.utils.syncImportLibrarySongsKey
import app.mousike.android.utils.syncImportUploadedAlbumsKey
import app.mousike.android.utils.syncImportUploadedSongsKey
import app.mousike.android.utils.syncPushAlbumBookmarkKey
import app.mousike.android.utils.syncPushArtistFollowKey
import app.mousike.android.utils.syncPushEpisodeKey
import app.mousike.android.utils.syncPushHistoryKey
import app.mousike.android.utils.syncPushPlaylistKey
import app.mousike.android.utils.syncPushSongLikeKey
import app.mousike.android.utils.syncShowDetailsKey
import app.mousike.android.utils.useLoginForBrowseKey
import app.mousike.android.components.settings.useLoginForSearchKey
import app.mousike.android.core.backup.BackupManager
import app.mousike.android.extensions.discord.discordAdvancedSettingKeys
import app.mousike.android.extensions.lastfm.isLastfmNowPlayingEnabledKey
import app.mousike.android.extensions.lastfm.isLastfmScrobbleEnabledKey
import app.mousike.android.extensions.lastfm.isLastfmScrobblingEnabledKey
import app.mousike.android.extensions.lastfm.lastfmMaxScrobbleDelaySecondsKey
import app.mousike.android.extensions.lastfm.lastfmMinTrackDurationSecondsKey
import app.mousike.android.extensions.lastfm.lastfmScrobbleThresholdPercentKey
import app.mousike.android.privacy.PermissionUsageJournal
import app.mousike.android.privacy.blockScreenCaptureKey
import app.mousike.android.utils.DataStoreUtils

// AccountsSync*Card, YtSearchAccountToggle and LocalPlaylistSongs define these defaults.
// Session credentials, sync cursors and account identity are intentionally absent.
private val additionalProfileSettingKeys = setOf(
    autosyncKey, autosyncArtistsKey, autosyncAlbumsKey, autosyncPlaylistsKey, autosyncLikesKey,
    syncDirectionKey, syncCooldownKey, syncShowDetailsKey, syncBackgroundGuardKey,
    syncPushSongLikeKey, syncPushAlbumBookmarkKey, syncPushArtistFollowKey,
    syncPushPlaylistKey, syncPushHistoryKey, syncPushEpisodeKey,
    syncImportHistoryKey, syncImportLibrarySongsKey, syncImportUploadedSongsKey,
    syncImportUploadedAlbumsKey, syncImportEpisodesKey, useLoginForBrowseKey, useLoginForSearchKey,
    blockScreenCaptureKey, PermissionUsageJournal.PAUSED_KEY, DEBUG_UPDATER_ACCESS_KEY,
    checkUpdateStateKey, checkBetaUpdatesKey, otherLanguageAppUpdateKey, "updateTranslationActive",
    // Player menu controls are profile settings; leave the device's physical volume alone.
    playbackSpeedKey, playbackPitchKey, playbackVolumeKey, playbackDurationKey, playerBackdropKey,
)

// AutoBackupSettingsBlock is the canonical reader. Keep PREF_URI and the SAF grant:
// resetting the schedule must not disconnect a chosen destination or delete a backup.
private val additionalBackupSettingKeys = setOf(
    BackupManager.PREF_ENABLED, BackupManager.PREF_INTERVAL, BackupManager.PREF_DB_CHANGE, BackupManager.PREF_MAX_BACKUPS,
    BackupManager.PREF_CUSTOM_INTERVAL_VALUE, BackupManager.PREF_CUSTOM_INTERVAL_UNIT,
    BackupManager.PREF_SCHEDULE_HOUR, BackupManager.PREF_SCHEDULE_DAY_OF_WEEK,
    BackupManager.PREF_SCHEDULE_DAY_OF_MONTH, BackupManager.PREF_TARGET,
    BackupManager.PREF_INCLUDE_YTB, BackupManager.PREF_INCLUDE_DISCORD,
    BackupManager.PREF_INCLUDE_LASTFM, BackupManager.PREF_INCLUDE_PROXY, BackupManager.PREF_INCLUDE_GITHUB, BackupManager.PREF_PRE_INSTALL,
)

// LastFmSettingsCard owns behavior; Last.fm session, username and avatar remain untouched.
private val additionalSecureSettingKeys = discordAdvancedSettingKeys + setOf(
    isLastfmScrobblingEnabledKey, isLastfmNowPlayingEnabledKey, isLastfmScrobbleEnabledKey,
    lastfmMinTrackDurationSecondsKey, lastfmScrobbleThresholdPercentKey, lastfmMaxScrobbleDelaySecondsKey,
)

// DataStoreUtils declares first-run read defaults for these controls. Deep-link consumption
// markers and onboarding flags in the same global store are user/session state, not settings.
private val additionalGlobalSettingKeys = setOf(
    DataStoreUtils.KEY_REWIND_ENABLED, DataStoreUtils.KEY_REWIND_YEARLY_ENABLED,
    DataStoreUtils.KEY_REWIND_MONTHLY_ENABLED, DataStoreUtils.KEY_REWIND_GLOBAL_ENABLED,
    DataStoreUtils.KEY_REWIND_MONTHLY_NOTIF_ENABLED, DataStoreUtils.KEY_REWIND_YEARLY_NOTIF_ENABLED,
    DataStoreUtils.KEY_REWIND_MONTHLY_PLAYLIST_ENABLED, DataStoreUtils.KEY_REWIND_YEARLY_PLAYLIST_ENABLED,
    DataStoreUtils.KEY_REWIND_MONTHLY_PLAYLIST_NOTIF_ENABLED,
    DataStoreUtils.KEY_REWIND_YEARLY_PLAYLIST_NOTIF_ENABLED,
    DataStoreUtils.KEY_REWIND_BGM_ENABLED, DataStoreUtils.KEY_REWIND_BGM_VOLUME,
)

/** Restores controls outside the historical category resets using their first-run read defaults. */
internal fun resetAdditionalSettingsDefaults(
    context: Context,
    cancelAutoBackup: () -> Unit = { BackupManager.scheduleBackup(context, BackupManager.INTERVAL_NONE) },
) {
    context.preferences.removeSettings(additionalProfileSettingKeys + additionalBackupSettingKeys)
    context.encryptedPreferences.removeSettings(additionalSecureSettingKeys)
    DataStoreUtils.prefs(context).removeSettings(additionalGlobalSettingKeys)
    cancelAutoBackup()
}

private fun SharedPreferences.removeSettings(keys: Set<String>) {
    val editor = edit()
    keys.forEach { key -> editor.remove(key) }
    editor.apply()
}
