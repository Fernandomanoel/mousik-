package app.mousike.android.components.settings.accounts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.media3.common.util.UnstableApi
import app.mousike.innertube.Innertube
import app.mousike.innertube.hasYouTubeAuthCookie
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import app.mousike.android.extensions.youtubelogin.YouTubeLogin
import app.mousike.android.extensions.youtubelogin.clearSavedYouTubeSession
import app.mousike.android.core.network.client.Store
import app.mousike.android.utils.autosyncAlbumsKey
import app.mousike.android.utils.autosyncArtistsKey
import app.mousike.android.utils.autosyncLikesKey
import app.mousike.android.utils.autosyncPlaylistsKey
import app.mousike.android.utils.clearAllSyncedData
import app.mousike.android.utils.enableYouTubeLoginKey
import app.mousike.android.utils.enableYouTubeSyncKey
import app.mousike.android.utils.encryptedPreferences
import app.mousike.android.utils.preferences
import app.mousike.android.utils.quickPicsDiscoverPageKey
import app.mousike.android.utils.quickPicsHomePageKey
import app.mousike.android.utils.quickPicsYtmQuickPicksKey
import app.mousike.android.utils.rememberEncryptedPreference
import app.mousike.android.utils.rememberPreference
import app.mousike.android.utils.streamClientRestartNeededKey
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
import app.mousike.android.utils.useLoginForBrowseKey
import app.mousike.android.utils.ytAccountChannelHandleKey
import app.mousike.android.utils.ytAccountEmailKey
import app.mousike.android.utils.ytAccountNameKey
import app.mousike.android.utils.ytAccountThumbnailKey
import app.mousike.android.utils.ytCookieExpiredKey
import app.mousike.android.utils.ytCookieKey
import app.mousike.android.utils.encryptedPreferencesUpdateTrigger
import app.mousike.android.core.utils.Toaster
import app.mousike.android.LocalPlayerServiceBinder
import app.mousike.android.R
import app.mousike.android.appContext
import app.mousike.android.colorPalette
import app.mousike.android.components.dialog.common.ButtonRow
import app.mousike.android.components.dialog.common.DefaultDialog
import app.mousike.android.components.dialog.common.DialogButton
import app.mousike.android.components.dialog.common.DialogPresence
import app.mousike.android.components.dialog.settings.clearAudioCache
import app.mousike.android.components.glass.CustomModalBottomSheet
import app.mousike.android.components.menu.MenuSheetDragHandle
import app.mousike.android.components.settings.YtSearchAccountToggle
import app.mousike.android.components.settings.common.RestartPlayerService
import app.mousike.android.components.settings.common.OtherSettingsEntry
import app.mousike.android.components.settings.common.OtherSwitchSettingEntry
import app.mousike.android.components.settings.common.SettingsSectionCard
import app.mousike.android.components.settings.common.settingsCardEnter
import app.mousike.android.components.settings.common.settingsCardExit
import app.mousike.android.components.settings.common.settingsSearchTarget
import app.mousike.android.components.tab.Search
import app.mousike.android.core.coil.ImageCacheFactory
import app.mousike.android.playback.services.clearStreamCaches
import app.mousike.innertube.YouTubeSession
import app.mousike.android.thumbnailShape
import app.mousike.android.typography
import app.mousike.android.uiRoundnessShape
import app.mousike.android.utils.coroutines.MousikeDispatchers

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
internal fun AccountsYouTubeCard(search: Search) {
    val context = LocalContext.current
    var showClearSyncDialog by remember { mutableStateOf(false) }

    val accountPreferences = remember(context, encryptedPreferencesUpdateTrigger) { context.encryptedPreferences }
    val cookie = accountPreferences.getString(ytCookieKey, null)
    val accountName = accountPreferences.getString(ytAccountNameKey, "").orEmpty()
    val accountEmail = accountPreferences.getString(ytAccountEmailKey, "").orEmpty()
    val accountChannelHandle = accountPreferences.getString(ytAccountChannelHandleKey, "").orEmpty()
    val accountThumbnail = accountPreferences.getString(ytAccountThumbnailKey, "").orEmpty()
    val isLoggedIn = !cookie.isNullOrBlank()

    // YouTube Music Section
    AnimatedVisibility(
        visible = true,
        enter = settingsCardEnter,
        exit = settingsCardExit
    ) {
        SettingsSectionCard(
            title = stringResource(R.string.settings_youtube_music_title),
            icon = R.drawable.ytmusic,
            content = {
                // rememberEncryptedPreference only works correct with API 24 and up
                var isYouTubeLoginEnabled by rememberEncryptedPreference(enableYouTubeLoginKey, false)
                var isYouTubeSyncEnabled by key(encryptedPreferencesUpdateTrigger) {
                    rememberEncryptedPreference(enableYouTubeSyncKey, false)
                }
                var loginYouTube by remember { mutableStateOf(false) }
                var changingAccount by remember { mutableStateOf(false) }
                val binder = LocalPlayerServiceBinder.current

                LaunchedEffect(isYouTubeLoginEnabled) {
                    if (!isYouTubeLoginEnabled && isYouTubeSyncEnabled) {
                        isYouTubeSyncEnabled = false
                    }
                }

                if (search.inputValue.isBlank() || stringResource(R.string.settings_youtube_login_enabled_label).contains(search.inputValue, true)) {
                    OtherSwitchSettingEntry(
                        modifier = Modifier.settingsSearchTarget("accounts.youtube_music.enable_youtube_music_login"),
                        title = stringResource(R.string.settings_youtube_login_enabled_label),
                        text = stringResource(R.string.account_youtube_login_enabled_description),
                        isChecked = isYouTubeLoginEnabled,
                        onCheckedChange = {
                            isYouTubeLoginEnabled = it
                            app.mousike.android.utils.encryptedPreferencesUpdateTrigger++
                            if (!it) {
                                // Only clear Innertube singleton (stop using account)
                                // Keep account info so user doesn't have to reconnect
                                Store.setSession(YouTubeSession(null, null, null, null))
                                Innertube.useLoginForBrowse = false
                                // Reset cookie status
                                app.mousike.android.MainApplication.cookieStatus = app.mousike.android.MainApplication.CookieStatus.NOT_LOGGED_IN
                                appContext().preferences.edit().remove(ytCookieExpiredKey).apply()

                                // Disable sync and all sub-toggles
                                appContext().encryptedPreferences.edit {
                                    putBoolean(enableYouTubeSyncKey, false)
                                }
                                appContext().preferences.edit {
                                    putBoolean(useLoginForBrowseKey, false)
                                    putBoolean(autosyncArtistsKey, false)
                                    putBoolean(autosyncAlbumsKey, false)
                                    putBoolean(autosyncPlaylistsKey, false)
                                    putBoolean(autosyncLikesKey, false)
                                    putBoolean(syncImportHistoryKey, false)
                                    putBoolean(syncImportLibrarySongsKey, false)
                                    putBoolean(syncImportUploadedSongsKey, false)
                                    putBoolean(syncImportUploadedAlbumsKey, false)
                                    putBoolean(syncImportEpisodesKey, false)
                                    putBoolean(syncPushHistoryKey, false)
                                    putBoolean(syncPushSongLikeKey, false)
                                    putBoolean(syncPushAlbumBookmarkKey, false)
                                    putBoolean(syncPushArtistFollowKey, false)
                                    putBoolean(syncPushPlaylistKey, false)
                                    putBoolean(syncPushEpisodeKey, false)
                                }

                                // Clear cached data
                                appContext().preferences.edit {
                                    remove(quickPicsHomePageKey)
                                    remove(quickPicsYtmQuickPicksKey)
                                    remove(quickPicsDiscoverPageKey)
                                }
                            } else {
                                // Re-enable: restore Innertube from saved preferences
                                val savedCookie = appContext().encryptedPreferences.getString(ytCookieKey, "") ?: ""
                                if (savedCookie.isNotEmpty()) {
                                    Store.initSession(context)
                                    // Only activate browse if we have real credentials
                                    if (hasYouTubeAuthCookie(savedCookie)) {
                                        appContext().preferences.edit().putBoolean(useLoginForBrowseKey, true).apply()
                                        Innertube.useLoginForBrowse = true
                                    }
                                }
                                // Reset cookie status — will be revalidated on next playback
                                app.mousike.android.MainApplication.cookieStatus = app.mousike.android.MainApplication.CookieStatus.NOT_LOGGED_IN
                                appContext().preferences.edit().remove(ytCookieExpiredKey).apply()
                            }
                            // Clear stream caches and mark restart needed
                            clearStreamCaches()
                            appContext().preferences.edit().putBoolean(streamClientRestartNeededKey, true).apply()
                            // Clear audio cache
                            MousikeDispatchers.fireAndForget(MousikeDispatchers.DATA).launch {
                                clearAudioCache(binder?.cache)
                            }
                        },
                        icon = R.drawable.ytmusic
                    )
                }

                val isStreamRestartNeeded by rememberPreference(streamClientRestartNeededKey, false)
                RestartPlayerService(
                    restartService = isStreamRestartNeeded,
                    onRestart = {
                        appContext().preferences.edit().putBoolean(streamClientRestartNeededKey, false).apply()
                    }
                )

                AnimatedVisibility(visible = isYouTubeLoginEnabled) {
                    Column {
                        if (isLoggedIn) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.settings_account_information_label),
                                        color = colorPalette().text,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 5.dp),
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp, bottom = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (accountThumbnail.isNotEmpty()) {
                                            ImageCacheFactory.AsyncImage(
                                                shape = thumbnailShape(),
                                                thumbnailUrl = accountThumbnail,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .padding(start = 5.dp, top = 8.dp, bottom = 8.dp)
                                                    .size(50.dp)
                                                    .clip(thumbnailShape())
                                            )
                                        } else {
                                            Icon(
                                                painter = painterResource(R.drawable.person),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .padding(start = 5.dp, top = 8.dp, bottom = 8.dp)
                                                    .size(50.dp)
                                                    .clip(thumbnailShape()),
                                                tint = colorPalette().textSecondary
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                Text(
                                                    text = accountName,
                                                    color = colorPalette().text,
                                                    modifier = Modifier.padding(start = 5.dp),
                                                    style = typography().m
                                                )
                                                if (accountChannelHandle.isNotEmpty()) {
                                                    Text(
                                                        text = accountChannelHandle,
                                                        color = colorPalette().textSecondary,
                                                        modifier = Modifier.padding(start = 5.dp),
                                                        style = typography().xs
                                                    )
                                                }
                                                if (accountEmail.isNotEmpty()) {
                                                    Text(
                                                        text = accountEmail,
                                                        color = colorPalette().textSecondary,
                                                        modifier = Modifier.padding(start = 5.dp),
                                                        style = typography().xs
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Cookie status warning — show when cookie is invalid/expired
                        if (isLoggedIn && app.mousike.android.MainApplication.cookieStatus in listOf(app.mousike.android.MainApplication.CookieStatus.INVALID, app.mousike.android.MainApplication.CookieStatus.EXPIRED)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.information),
                                    contentDescription = null,
                                    tint = colorPalette().textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = stringResource(R.string.account_youtube_cookie_invalid),
                                    color = colorPalette().textSecondary,
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = typography().xs
                                )
                            }
                        }

                        if (search.inputValue.isBlank() || true) {
                            OtherSettingsEntry(
                                modifier = Modifier.settingsSearchTarget(if (isLoggedIn) "accounts.youtube_music.account_logoff" else "accounts.youtube_music.account_login"),
                                title = if (isLoggedIn) stringResource(R.string.account_action_logout) else stringResource(R.string.account_action_login),
                                text = stringResource(R.string.account_youtube_login_description),
                                icon = if (isLoggedIn) R.drawable.logout else R.drawable.person,
                                onClick = {
                                    if (isLoggedIn) {
                                        showClearSyncDialog = true
                                    } else {
                                        changingAccount = false
                                        loginYouTube = true
                                    }
                                }
                            )
                        }

                        val changeAccountLabel = stringResource(R.string.account_youtube_change_account)
                        val changeAccountDescription = stringResource(R.string.account_youtube_change_account_description)
                        if (isLoggedIn && (search.inputValue.isBlank() ||
                                changeAccountLabel.contains(search.inputValue, ignoreCase = true) ||
                                changeAccountDescription.contains(search.inputValue, ignoreCase = true))
                        ) {
                            OtherSettingsEntry(
                                modifier = Modifier.settingsSearchTarget("accounts.youtube_music.change_account"),
                                title = changeAccountLabel,
                                text = changeAccountDescription,
                                icon = R.drawable.person,
                                onClick = {
                                    changingAccount = true
                                    loginYouTube = true
                                }
                            )
                        }

                        CustomModalBottomSheet(
                            showSheet = loginYouTube,
                            onDismissRequest = {
                                loginYouTube = false
                            },
                            containerColor = colorPalette().background0,
                            contentColor = colorPalette().background0,
                            modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                            shape = uiRoundnessShape(),
                            dragHandle = {
                                MenuSheetDragHandle(minTouchHeight = 48.dp)
                            }
                        ) {
                            YouTubeLogin(
                                onLogin = {
                                    loginYouTube = false
                                    context.preferences.edit {
                                        putBoolean(ytCookieExpiredKey, false)
                                        if (!changingAccount) putBoolean(useLoginForBrowseKey, true)
                                        remove(quickPicsHomePageKey)
                                        remove(quickPicsYtmQuickPicksKey)
                                        remove(quickPicsDiscoverPageKey)
                                    }
                                    app.mousike.android.MainApplication.cookieStatus = app.mousike.android.MainApplication.CookieStatus.VALID
                                    if (!changingAccount) Innertube.useLoginForBrowse = true
                                    Toaster.i(context.getString(R.string.account_youtube_login_success))
                                },
                                onCancel = { loginYouTube = false },
                                changingAccount = changingAccount,
                                isActive = { loginYouTube },
                            )
                        }

                        // Login for Browse option (must be enabled for sync to work)
                        if (isYouTubeLoginEnabled) {
                        var useLoginForBrowse by rememberPreference(useLoginForBrowseKey, true)
                        if (search.inputValue.isBlank() || stringResource(R.string.settings_youtube_authenticated_browsing_label).contains(search.inputValue, true)) {
                            OtherSwitchSettingEntry(
                                modifier = Modifier.settingsSearchTarget("accounts.youtube_music.login_for_browse"),
                                title = stringResource(R.string.settings_youtube_authenticated_browsing_label),
                                text = if (!isLoggedIn) stringResource(R.string.settings_youtube_connection_required_description) else stringResource(R.string.settings_youtube_authenticated_browsing_description),
                                isChecked = isLoggedIn && useLoginForBrowse,
                                enabled = isLoggedIn,
                                onCheckedChange = {
                                    useLoginForBrowse = it
                                    Innertube.useLoginForBrowse = it
                                    if (!it) {
                                        // Reset sync and all sub-toggles when Login for Browse is disabled
                                        appContext().encryptedPreferences.edit {
                                            putBoolean(enableYouTubeSyncKey, false)
                                        }
                                        appContext().preferences.edit {
                                            putBoolean(autosyncArtistsKey, false)
                                            putBoolean(autosyncAlbumsKey, false)
                                            putBoolean(autosyncPlaylistsKey, false)
                                            putBoolean(autosyncLikesKey, false)
                                            putBoolean(syncImportHistoryKey, false)
                                            putBoolean(syncImportLibrarySongsKey, false)
                                            putBoolean(syncImportUploadedSongsKey, false)
                                            putBoolean(syncImportUploadedAlbumsKey, false)
                                            putBoolean(syncImportEpisodesKey, false)
                                            putBoolean(syncPushHistoryKey, false)
                                            putBoolean(syncPushSongLikeKey, false)
                                            putBoolean(syncPushAlbumBookmarkKey, false)
                                            putBoolean(syncPushArtistFollowKey, false)
                                            putBoolean(syncPushPlaylistKey, false)
                                            putBoolean(syncPushEpisodeKey, false)
                                        }
                                    }
                                },
                                icon = R.drawable.person
                            )
                        }
                        }
                        YtSearchAccountToggle(isYouTubeLoginEnabled = isYouTubeLoginEnabled, isLoggedIn = isLoggedIn, searchQuery = search.inputValue)


                    }
                }
            }
        )
    }

    // Clear synced data confirmation dialog
    val dialogCoroutineScope = rememberCoroutineScope()
    DialogPresence(visible = showClearSyncDialog) {
        DefaultDialog(
            title = stringResource(R.string.settings_youtube_clear_synced_data_dialog_title),
            onDismiss = { showClearSyncDialog = false },
            footer = {
                ButtonRow(listOf(
                    DialogButton(stringResource(R.string.common_action_cancel), { showClearSyncDialog = false }, danger = true),
                    DialogButton(stringResource(R.string.common_action_clear), primary = true, onClick = {
                        showClearSyncDialog = false
                        dialogCoroutineScope.launch(MousikeDispatchers.DATA) {
                            val cleared = runCatching { clearAllSyncedData() }.getOrElse { false }
                            withContext(MousikeDispatchers.UI) {
                                clearSavedYouTubeSession(context.encryptedPreferences, clearWebSignIn = true)
                                app.mousike.android.MainApplication.cookieStatus = app.mousike.android.MainApplication.CookieStatus.NOT_LOGGED_IN
                                appContext().preferences.edit().remove(ytCookieExpiredKey).apply()
                                appContext().preferences.edit().putBoolean(streamClientRestartNeededKey, true).apply()
                                if (cleared) {
                                    Toaster.s(R.string.account_action_logout)
                                } else {
                                    Toaster.w(R.string.account_action_logout)
                                }
                            }
                        }
                    })
                ))
            }
        ) {
            BasicText(
                text = stringResource(R.string.settings_youtube_clear_synced_data_confirmation),
                style = typography().s.copy(color = colorPalette().text)
            )

        }
    }
}
