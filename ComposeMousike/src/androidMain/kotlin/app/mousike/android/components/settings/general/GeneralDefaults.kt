package app.mousike.android.components.settings.general

import android.content.Context
import app.mousike.android.enums.DislikeMode
import app.mousike.android.enums.DurationInMinutes
import app.mousike.android.enums.ExoPlayerMinTimeForEvent
import app.mousike.android.enums.Languages
import app.mousike.android.enums.MaxSongs
import app.mousike.android.enums.MusicAnimationType
import app.mousike.android.enums.PauseBetweenSongs
import app.mousike.android.enums.PipModule
import app.mousike.android.enums.PresetsReverb
import app.mousike.android.utils.audioReverbPresetKey
import app.mousike.android.utils.autoLoadSongsInQueueKey
import app.mousike.android.utils.bassboostEnabledKey
import app.mousike.android.utils.bassboostLevelKey
import app.mousike.android.utils.closeWithBackButtonKey
import app.mousike.android.utils.closebackgroundPlayerKey
import app.mousike.android.utils.crossfadeDurationKey
import app.mousike.android.utils.crossfadeEnabledKey
import app.mousike.android.utils.crossfadeGaplessKey
import app.mousike.android.utils.disableClosingPlayerSwipingDownKey
import app.mousike.android.utils.discoverKey
import app.mousike.android.utils.enablePictureInPictureAutoKey
import app.mousike.android.utils.enablePictureInPictureKey
import app.mousike.android.utils.excludeDislikedAlbumsKey
import app.mousike.android.utils.excludeDislikedArtistsKey
import app.mousike.android.utils.excludeDislikedSongsKey
import app.mousike.android.utils.excludeSongsWithDurationLimitKey
import app.mousike.android.utils.exoPlayerMinTimeForEventKey
import app.mousike.android.utils.handleAudioFocusEnabledKey
import app.mousike.android.utils.isPauseOnVolumeZeroEnabledKey
import app.mousike.android.utils.jumpPreviousKey
import app.mousike.android.utils.keepPlayerMinimizedKey
import app.mousike.android.utils.languageAppKey
import app.mousike.android.utils.loudnessBaseGainKey
import app.mousike.android.utils.maxSongsInQueueAndroidAutoKey
import app.mousike.android.utils.maxSongsInQueueKey
import app.mousike.android.utils.minimumSilenceDurationKey
import app.mousike.android.utils.nowPlayingIndicatorKey
import app.mousike.android.utils.pauseBetweenSongsKey
import app.mousike.android.utils.pauseOnHeadphoneDisconnectKey
import app.mousike.android.utils.persistentQueueKey
import app.mousike.android.utils.pipModuleKey
import app.mousike.android.utils.playlistindicatorKey
import app.mousike.android.utils.resumePlaybackOnStartKey
import app.mousike.android.utils.resumePlaybackWhenDeviceConnectedKey
import app.mousike.android.utils.skipMediaOnErrorKey
import app.mousike.android.utils.skipSilenceKey
import app.mousike.android.utils.useVolumeKeysToChangeSongKey
import app.mousike.android.utils.volumeBoostLevelKey
import app.mousike.android.utils.volumeNormalizationKey
import app.mousike.android.components.dialog.settings.AppShortcutsSettingsDialog

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.core.os.LocaleListCompat
import app.mousike.android.core.database.Database
import app.mousike.android.utils.shouldRecreateActivity
import androidx.core.content.edit
import app.mousike.android.utils.preferences
import app.mousike.android.utils.putEnum

/** Restores General settings without touching saved queue contents or account data. */
internal fun applyGeneralDefaults(context: Context) {
    context.preferences.edit {
        putEnum(languageAppKey, Languages.System)
        putEnum(exoPlayerMinTimeForEventKey, ExoPlayerMinTimeForEvent.`20s`)
        putBoolean(persistentQueueKey, false)
        putBoolean(resumePlaybackOnStartKey, false)
        putBoolean(closebackgroundPlayerKey, false)
        putBoolean(closeWithBackButtonKey, true)
        putBoolean(resumePlaybackWhenDeviceConnectedKey, false)
        putBoolean(skipSilenceKey, false)
        putBoolean(skipMediaOnErrorKey, false)
        putBoolean(volumeNormalizationKey, false)
        putFloat(volumeBoostLevelKey, 0f)
        putBoolean(keepPlayerMinimizedKey, true)
        putBoolean(disableClosingPlayerSwipingDownKey, false)
        putEnum(pauseBetweenSongsKey, PauseBetweenSongs.`0`)
        putEnum(maxSongsInQueueKey, MaxSongs.Unlimited)
        putEnum(maxSongsInQueueAndroidAutoKey, MaxSongs.Unlimited)
        putBoolean(crossfadeEnabledKey, false)
        putInt(crossfadeDurationKey, 3000)
        putBoolean(crossfadeGaplessKey, false)
        putBoolean(useVolumeKeysToChangeSongKey, false)
        putEnum(excludeSongsWithDurationLimitKey, DurationInMinutes.Disabled)
        putBoolean(playlistindicatorKey, false)
        putEnum(nowPlayingIndicatorKey, MusicAnimationType.Bubbles)
        putBoolean(discoverKey, false)
        putBoolean(isPauseOnVolumeZeroEnabledKey, false)
        putBoolean(pauseOnHeadphoneDisconnectKey, true)
        putEnum(excludeDislikedSongsKey, DislikeMode.Enabled)
        putEnum(excludeDislikedArtistsKey, DislikeMode.Enabled)
        putEnum(excludeDislikedAlbumsKey, DislikeMode.Enabled)
        putLong(minimumSilenceDurationKey, 2_000_000L)
        putFloat(loudnessBaseGainKey, 5.00f)
        putBoolean(autoLoadSongsInQueueKey, true)
        putBoolean(bassboostEnabledKey, false)
        putFloat(bassboostLevelKey, 0f)
        putEnum(audioReverbPresetKey, PresetsReverb.NONE)
        putBoolean(handleAudioFocusEnabledKey, true)
        putBoolean(enablePictureInPictureKey, false)
        putBoolean(enablePictureInPictureAutoKey, false)
        putEnum(pipModuleKey, PipModule.Cover)
        putString(jumpPreviousKey, "3")
    }
    if (shouldRecreateActivity(Database.isClosed) && !AppCompatDelegate.getApplicationLocales().isEmpty) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
    }
    AppShortcutsSettingsDialog.reset(context)
}

/** Compatibility entry for the category reset, retaining its synchronous writes. */
@Composable
fun DefaultGeneralSettings(context: Context) {
    applyGeneralDefaults(context)
}
