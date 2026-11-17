package app.mousike.android.components.settings.accounts

import app.mousike.android.utils.autosyncAlbumsKey
import app.mousike.android.utils.autosyncArtistsKey
import app.mousike.android.utils.autosyncLikesKey
import app.mousike.android.utils.autosyncPlaylistsKey
import app.mousike.android.utils.discordAvatarKey
import app.mousike.android.utils.discordPersonalAccessTokenKey
import app.mousike.android.utils.discordUsernameKey
import app.mousike.android.utils.enableYouTubeLoginKey
import app.mousike.android.utils.enableYouTubeSyncKey
import app.mousike.android.utils.isDiscordPresenceEnabledKey
import app.mousike.android.utils.streamClientRestartNeededKey
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
import app.mousike.android.utils.ytAccountChannelHandleKey
import app.mousike.android.utils.ytAccountEmailKey
import app.mousike.android.utils.ytAccountNameKey
import app.mousike.android.utils.ytAccountThumbnailKey
import app.mousike.android.utils.ytCookieKey
import app.mousike.android.utils.ytDataSyncIdKey
import app.mousike.android.utils.ytAuthUserKey
import app.mousike.android.utils.ytVisitorDataKey
import app.mousike.android.components.settings.useLoginForSearchKey
import app.mousike.android.extensions.discord.discordAdvancedActivityTypeKey
import app.mousike.android.extensions.discord.discordAdvancedButton1EnabledKey
import app.mousike.android.extensions.discord.discordAdvancedButton1LabelKey
import app.mousike.android.extensions.discord.discordAdvancedButton1UrlKey
import app.mousike.android.extensions.discord.discordAdvancedButton2EnabledKey
import app.mousike.android.extensions.discord.discordAdvancedButton2LabelKey
import app.mousike.android.extensions.discord.discordAdvancedButton2UrlKey
import app.mousike.android.extensions.discord.discordAdvancedDetailsTemplateKey
import app.mousike.android.extensions.discord.discordAdvancedIdleCloseEnabledKey
import app.mousike.android.extensions.discord.discordAdvancedLargeImageTextKey
import app.mousike.android.extensions.discord.discordAdvancedNameKey
import app.mousike.android.extensions.discord.discordAdvancedPauseClearEnabledKey
import app.mousike.android.extensions.discord.discordAdvancedPausePresenceEnabledKey
import app.mousike.android.extensions.discord.discordAdvancedPauseTemplateKey
import app.mousike.android.extensions.discord.discordAdvancedRefreshIntervalMsKey
import app.mousike.android.extensions.discord.discordAdvancedShowArtworkKey
import app.mousike.android.extensions.discord.discordAdvancedShowDetailsKey
import app.mousike.android.extensions.discord.discordAdvancedShowSmallImageKey
import app.mousike.android.extensions.discord.discordAdvancedShowStateKey
import app.mousike.android.extensions.discord.discordAdvancedShowTimestampsKey
import app.mousike.android.extensions.discord.discordAdvancedSmallImageTextKey
import app.mousike.android.extensions.discord.discordAdvancedStateTemplateKey
import app.mousike.android.extensions.discord.isDiscordAdvancedModeKey
import app.mousike.android.extensions.lastfm.isLastfmNowPlayingEnabledKey
import app.mousike.android.extensions.lastfm.isLastfmScrobbleEnabledKey
import app.mousike.android.extensions.lastfm.isLastfmScrobblingEnabledKey
import app.mousike.android.extensions.lastfm.lastfmAvatarUrlKey
import app.mousike.android.extensions.lastfm.lastfmMaxScrobbleDelaySecondsKey
import app.mousike.android.extensions.lastfm.lastfmMinTrackDurationSecondsKey
import app.mousike.android.extensions.lastfm.lastfmScrobbleThresholdPercentKey
import app.mousike.android.extensions.lastfm.lastfmSessionKey
import app.mousike.android.extensions.lastfm.lastfmUsernameKey

/** Semantic inventory for later exact search, independent of translated labels and order. */
internal object AccountsSettingTargets {
    val cards = listOf(
        AccountsCardTargets("accounts.card.face",
            preferenceKeys = listOf(),
            actions = listOf("editFace", "openProfiles"),
        ),
        AccountsCardTargets("accounts.card.youtube",
            preferenceKeys = listOf(ytVisitorDataKey, ytDataSyncIdKey, ytAuthUserKey, ytCookieKey, ytAccountNameKey, ytAccountEmailKey, ytAccountChannelHandleKey, ytAccountThumbnailKey, enableYouTubeLoginKey, enableYouTubeSyncKey, streamClientRestartNeededKey, useLoginForBrowseKey, useLoginForSearchKey),
            actions = listOf("connect", "changeAccount", "disconnect", "clearSyncedData"),
        ),
        AccountsCardTargets("accounts.card.sync",
            preferenceKeys = listOf(enableYouTubeSyncKey, syncDirectionKey, syncCooldownKey, syncShowDetailsKey, syncBackgroundGuardKey),
            actions = listOf("syncNow", "syncStatus"),
        ),
        AccountsCardTargets("accounts.card.autosync",
            preferenceKeys = listOf(autosyncArtistsKey, autosyncAlbumsKey, autosyncPlaylistsKey, autosyncLikesKey),
            actions = listOf(),
        ),
        AccountsCardTargets("accounts.card.sync_import",
            preferenceKeys = listOf(syncImportHistoryKey, syncImportLibrarySongsKey, syncImportUploadedSongsKey, syncImportUploadedAlbumsKey, syncImportEpisodesKey),
            actions = listOf(),
        ),
        AccountsCardTargets("accounts.card.sync_push",
            preferenceKeys = listOf(syncPushSongLikeKey, syncPushAlbumBookmarkKey, syncPushArtistFollowKey, syncPushPlaylistKey, syncPushHistoryKey, syncPushEpisodeKey),
            actions = listOf(),
        ),
        AccountsCardTargets("accounts.card.lastfm",
            preferenceKeys = listOf(isLastfmScrobblingEnabledKey, isLastfmNowPlayingEnabledKey, isLastfmScrobbleEnabledKey, lastfmMinTrackDurationSecondsKey, lastfmScrobbleThresholdPercentKey, lastfmMaxScrobbleDelaySecondsKey, lastfmSessionKey, lastfmUsernameKey, lastfmAvatarUrlKey),
            actions = listOf("connect", "disconnect"),
        ),
        AccountsCardTargets("accounts.card.listentogether",
            preferenceKeys = listOf(),
            actions = listOf("openScreen"),
        ),
        AccountsCardTargets("accounts.card.discord",
            preferenceKeys = listOf(isDiscordPresenceEnabledKey, discordPersonalAccessTokenKey, discordAvatarKey, discordUsernameKey, isDiscordAdvancedModeKey, discordAdvancedActivityTypeKey, discordAdvancedPausePresenceEnabledKey, discordAdvancedNameKey, discordAdvancedStateTemplateKey, discordAdvancedDetailsTemplateKey, discordAdvancedPauseTemplateKey, discordAdvancedButton1EnabledKey, discordAdvancedButton1LabelKey, discordAdvancedButton1UrlKey, discordAdvancedButton2EnabledKey, discordAdvancedButton2LabelKey, discordAdvancedButton2UrlKey, discordAdvancedShowStateKey, discordAdvancedShowDetailsKey, discordAdvancedShowArtworkKey, discordAdvancedShowSmallImageKey, discordAdvancedShowTimestampsKey, discordAdvancedLargeImageTextKey, discordAdvancedSmallImageTextKey, discordAdvancedPauseClearEnabledKey, discordAdvancedIdleCloseEnabledKey, discordAdvancedRefreshIntervalMsKey),
            actions = listOf("connect", "disconnect", "preview", "editTemplate"),
        ),
        AccountsCardTargets("accounts.card.reset",
            preferenceKeys = listOf(),
            actions = listOf("applyAccountsDefaults"),
        ),
    )
    val ids: List<String> = cards.flatMap { card ->
        listOf(card.id) + card.preferenceKeys.map { "${card.id}.setting.$it" } +
            card.actions.map { "${card.id}.action.$it" }
    }
}

internal data class AccountsCardTargets(
    val id: String,
    val preferenceKeys: List<String> = emptyList(),
    val actions: List<String> = emptyList(),
)
