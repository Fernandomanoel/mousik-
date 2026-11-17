package app.mousike.android.components.ui.screens.home.onDevice

import kotlinx.coroutines.flow.map
import app.mousike.android.core.database.Database
import app.mousike.android.privacy.PermissionUsageJournal
import app.mousike.android.privacy.PermissionFeature
import app.mousike.android.privacy.JournalPermission
import app.mousike.android.privacy.PermissionEventKind

import app.mousike.android.components.search.SearchEmptyFeedback
import app.mousike.android.components.search.SearchNoResults

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import app.mousike.android.components.glass.PageButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import app.mousike.android.components.navigation.rememberPageChromeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import app.mousike.android.R
import timber.log.Timber
import app.mousike.android.EXPLICIT_PREFIX
import app.mousike.android.LocalPlayerServiceBinder
import app.mousike.android.colorPalette
import app.mousike.android.models.Song
import app.mousike.android.typography
import app.mousike.android.ui.components.SwipeablePlaylistItem
import app.mousike.android.ui.components.tab.toolbar.Button
import app.mousike.android.ui.styling.Dimensions
import app.mousike.android.utils.Preference.HOME_ON_DEVICE_SONGS_SORT_BY
import app.mousike.android.utils.Preference.HOME_ON_DEVICE_SONGS_SORT_ORDER
import app.mousike.android.utils.homeSongsOnDeviceSortMenuOrderKey
import app.mousike.android.utils.addNext
import app.mousike.android.utils.asMediaItem
import app.mousike.android.utils.bold
import app.mousike.android.utils.semiBold
import app.mousike.android.utils.enqueue
import app.mousike.android.utils.forcePlayAtIndex
import app.mousike.android.utils.isAtLeastAndroid13
import app.mousike.android.utils.parentalControlEnabledKey
import app.mousike.android.utils.rememberPreference
import app.mousike.android.utils.showFoldersOnDeviceKey
import app.mousike.android.utils.defaultFolderKey
import app.mousike.android.utils.getActiveProfile
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import app.mousike.android.utils.coroutines.MousikeDispatchers
import app.mousike.android.components.AppPullToRefreshBox
import app.mousike.android.components.FolderItem
import app.mousike.android.components.SongItem
import app.mousike.android.components.Sort
import app.mousike.android.core.database.LikeStateManager
import app.mousike.android.core.database.PlaylistStateManager
import app.mousike.android.components.tab.ItemSelector
import app.mousike.android.components.tab.Search
import app.mousike.android.core.utils.PathUtils
import app.mousike.android.core.utils.Toaster
import app.mousike.android.download.utils.MyDownloadHelper
import app.mousike.android.LocalDownloadStatesMap
import app.mousike.android.enums.DownloadedStateMedia
import app.mousike.android.enums.PlaylistSwipeAction
import app.mousike.android.utils.playlistSwipeLeftActionKey
import app.mousike.android.utils.playlistSwipeRightActionKey
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.exoplayer.offline.Download
import java.io.IOException

@UnstableApi
@ExperimentalFoundationApi
@OptIn(FlowPreview::class)
@Composable
fun OnDeviceSong(
    navController: NavController,
    lazyListState: LazyListState,
    itemSelector: ItemSelector<Song>,
    search: Search,
    buttons: MutableList<Button>,
    itemsOnDisplay: MutableList<Song>,
    getSongs: () -> List<Song>,
    headerPadding: androidx.compose.ui.unit.Dp = 0.dp,
    topContentInsetProvider: () -> androidx.compose.ui.unit.Dp = { 0.dp },
    visibleHeaderPadding: () -> androidx.compose.ui.unit.Dp = { headerPadding },
) {
    // Essentials
    val context = LocalContext.current
    val binder = LocalPlayerServiceBinder.current
    val profile = getActiveProfile(context)
    val lifecycleOwner = LocalLifecycleOwner.current

    //<editor-fold defaultstate="collapsed" desc="Settings">
    val parentalControlEnabled by rememberPreference( parentalControlEnabledKey, false )
    val showFolder4LocalSongs by rememberPreference( showFoldersOnDeviceKey, true )
    val startFolder by rememberPreference(defaultFolderKey, "/")
    val latestStartFolder by rememberUpdatedState(startFolder)
    //</editor-fold>

    var songsOnDevice by remember(profile) {
        mutableStateOf(emptyMap<Song, String>(), referentialEqualityPolicy())
    }
    var selectedFolder by remember(profile) {
        mutableStateOf<String?>(null)
    }
    val currentPath = selectedFolder.orEmpty()
    var pendingRefreshRevision by remember(profile) { mutableStateOf<Long?>(null) }

    //<editor-fold defaultstate="collapsed" desc="Permission handler">
    val permission = rememberSaveable {
        if( isAtLeastAndroid13 ) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
    }
    var isPermissionGranted by remember { mutableStateOf(
        ContextCompat.checkSelfPermission( context, permission ) == PackageManager.PERMISSION_GRANTED
    ) }

    /** Opens a prompt saying that a permission (should be either [Manifest.permission.READ_MEDIA_AUDIO] or [Manifest.permission.READ_EXTERNAL_STORAGE]) Then apply result of that prompt. */
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        isPermissionGranted = it
        PermissionUsageJournal.record(context, PermissionFeature.LOCAL_LIBRARY, JournalPermission.MEDIA_AUDIO,
            if (it) PermissionEventKind.GRANTED else PermissionEventKind.DENIED)
    }

    /**
     * Starts new activity (should be [Settings.ACTION_APPLICATION_DETAILS_SETTINGS]).
     * Then wait until user exits the activity, check for permission changes.
     */
    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        isPermissionGranted = ContextCompat.checkSelfPermission( context, permission ) == PackageManager.PERMISSION_GRANTED
    }
    //</editor-fold>

    val odSort = Sort( HOME_ON_DEVICE_SONGS_SORT_BY, HOME_ON_DEVICE_SONGS_SORT_ORDER, homeSongsOnDeviceSortMenuOrderKey, "dev")

    var searchDataReady by remember(profile, isPermissionGranted, odSort.sortBy, odSort.sortOrder) { mutableStateOf(false) }
    var completedSearchQuery by remember { mutableStateOf<String?>(null) }
    var completedSearchPath by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(lifecycleOwner, profile, isPermissionGranted, odSort.sortBy, odSort.sortOrder) {
        if (!isPermissionGranted) return@LaunchedEffect

        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            localSongRefreshRequests.forProfile(profile).collectLatest { revision ->
                try {
                    context.getProfileLocalSongs(odSort.sortBy, odSort.sortOrder, profile)
                        .collect { songs ->
                            PermissionUsageJournal.record(context, PermissionFeature.LOCAL_LIBRARY, JournalPermission.MEDIA_AUDIO, PermissionEventKind.USED)
                            if (songs.isNotEmpty() || selectedFolder != null) {
                                selectedFolder = PathUtils.resolveFolder(songs.values, selectedFolder, latestStartFolder)
                            }
                            if (itemSelector.isNotEmpty()) {
                                val eligibleIds = songs.keys.mapTo(HashSet()) { it.id }
                                itemSelector.removeAll { it.id !in eligibleIds }
                            }
                            songsOnDevice = songs
                            searchDataReady = true
                        }
                } catch (error: SecurityException) {
                    Timber.tag("OnDeviceSongs").w(error, "Media access changed during scan")
                    isPermissionGranted = false
                    itemSelector.clear()
                } catch (error: Exception) {
                    if (error !is IOException && error !is IllegalArgumentException && error !is SQLiteException) throw error
                    Timber.tag("OnDeviceSongs").w(error, "Could not scan on-device music")
                    searchDataReady = true
                } finally {
                    if (pendingRefreshRevision?.let { revision >= it } == true) {
                        pendingRefreshRevision = null
                    }
                }
            }
        }
    }
    val songEntries by remember(profile) {
        derivedStateOf { songsOnDevice.entries.toList() }
    }
    val storedSongs by remember(profile) {
        Database.songTable.allOnDevice().map { songs -> songs.associateBy(Song::id) }
    }.collectAsStateWithLifecycle(emptyMap(), context = MousikeDispatchers.DATA)
    val query = search.inputValue
    val visibleSongs = remember(songEntries, storedSongs, query, currentPath, showFolder4LocalSongs, parentalControlEnabled, isPermissionGranted) {
        if (isPermissionGranted) {
            songEntries.asSequence()
                .filter { !showFolder4LocalSongs || it.value == currentPath }
                .map { storedSongs[it.key.id] ?: it.key }
                .filter { !parentalControlEnabled || !it.title.startsWith(EXPLICIT_PREFIX, true) }
                .filter { it.cleanTitle().contains(query, true) || it.cleanArtistsText().contains(query, true) }
                .toList()
        } else emptyList()
    }
    LaunchedEffect(visibleSongs, query, currentPath) {
        androidx.compose.runtime.snapshots.Snapshot.withMutableSnapshot {
            itemsOnDisplay.clear()
            itemsOnDisplay.addAll(visibleSongs)
            completedSearchQuery = query
            completedSearchPath = currentPath
        }
    }
    val folders by remember(profile) {
        derivedStateOf {
            if (showFolder4LocalSongs) {
                PathUtils.getAvailablePaths(songsOnDevice.values, selectedFolder.orEmpty())
            } else emptyList()
        }
    }
    SearchEmptyFeedback(
        query = search.inputValue,
        empty = itemsOnDisplay.isEmpty() && folders.isEmpty(),
        loading = !isPermissionGranted || !searchDataReady ||
            completedSearchQuery != search.inputValue || completedSearchPath != currentPath
    )
    LaunchedEffect( Unit ) {
        if( !isPermissionGranted )
            try {
                PermissionUsageJournal.record(context, PermissionFeature.LOCAL_LIBRARY, JournalPermission.MEDIA_AUDIO, PermissionEventKind.REQUESTED)
                permissionLauncher.launch( permission )
            } catch ( e: Exception ) {
                Timber.tag("OnDeviceSongs").w(e, "Could not request media access")
                Toaster.e(R.string.library_on_device_permission_request_failed)
            }
    }



    val songIds = remember(itemsOnDisplay) { itemsOnDisplay.map { it.id } }
    val likeStatesMap by remember(songIds) {
        LikeStateManager.getLikeStates(songIds)
    }.collectAsStateWithLifecycle(emptyMap(), context = MousikeDispatchers.DATA)
    val playlistStatesMap by remember(songIds) {
        PlaylistStateManager.getPlaylistStates(songIds)
    }.collectAsStateWithLifecycle(emptyMap(), context = MousikeDispatchers.DATA)

    // Download state cache
    val downloadsMapState by MyDownloadHelper.downloads.collectAsStateWithLifecycle(initialValue = MyDownloadHelper.downloads.value, context = MousikeDispatchers.DATA)
    val downloadedIds by remember {
        derivedStateOf {
            downloadsMapState.values
                .filter { it.state == Download.STATE_COMPLETED }
                .mapTo(HashSet()) { it.request.id }
        }
    }
    val downloadStatesMap by remember {
        derivedStateOf {
            itemsOnDisplay.associate { song ->
                song.id to when {
                    song.id in downloadedIds -> DownloadedStateMedia.DOWNLOADED
                    else -> DownloadedStateMedia.NOT_CACHED_OR_DOWNLOADED
                }
            }
        }
    }

    AppPullToRefreshBox(
        isRefreshing = pendingRefreshRevision != null,
        onRefresh = {
            if (isPermissionGranted) {
                pendingRefreshRevision = localSongRefreshRequests.request(profile)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
    // Hoisted swipe action preferences
    val playlistSwipeLeftAction by rememberPreference(playlistSwipeLeftActionKey, PlaylistSwipeAction.Favourite)
    val playlistSwipeRightAction by rememberPreference(playlistSwipeRightActionKey, PlaylistSwipeAction.PlayNext)

    CompositionLocalProvider(LocalDownloadStatesMap provides downloadStatesMap) {
    LazyColumn(
        state = lazyListState,
        userScrollEnabled = songsOnDevice.isNotEmpty(),
        contentPadding = rememberPageChromeContentPadding(
            topInsetProvider = topContentInsetProvider,
            base = PaddingValues(bottom = Dimensions.bottomSpacer),
            extraTopProvider = visibleHeaderPadding,
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        if( !isPermissionGranted ) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillParentMaxSize()
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.locked),
                            tint = colorPalette().textDisabled,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize( .4f )
                        )

                        BasicText(
                            text = stringResource( R.string.library_local_media_permission_message ),
                            style = typography().m.copy( color = colorPalette().textDisabled )
                        )

                        Spacer( Modifier.height( 20.dp ) )

                        PageButton(
                            border = BorderStroke( 2.dp, colorPalette().accent ),
                            colors = ButtonDefaults.buttonColors().copy( containerColor = Color.Transparent ),
                            onClick = {
                                try {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null )
                                    }
                                    settingsLauncher.launch( intent )
                                } catch ( e: Exception ) {
                                    Timber.tag("OnDeviceSongs").w(e, "Could not open permission settings")
                                    Toaster.e(R.string.library_on_device_permission_settings_failed)
                                }
                            }
                        ) {
                            BasicText(
                                text = stringResource( R.string.visualizer_action_open_permission_settings ),
                                style = typography().l.bold.copy( color = colorPalette().accent )
                            )
                        }
                    }
                }
            }
        } else {
            if( showFolder4LocalSongs && songsOnDevice.isNotEmpty() ) {
                item( "folder_paths" ) {
                    PathUtils.AddressBar(
                        currentPath = currentPath,
                        onSpecificAddressClick = { selectedFolder = it }
                    )
                }

            items(
                items = folders,
                key = { "folder:${PathUtils.childPath(currentPath, it)}" }
            ) {
                Box(Modifier.animateItem()) {
                    FolderItem(it) { selectedFolder = PathUtils.childPath(currentPath, it) }
                }
            }
        }

        if (isPermissionGranted && searchDataReady && visibleSongs.isEmpty() && folders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (search.inputValue.isBlank()) SearchNoResults(text = stringResource(R.string.common_empty_items))
                }
            }
        }

        items(
            items = visibleSongs,
            key = { song -> song.id }
        ) { song ->
            val mediaItem = song.asMediaItem

            SwipeablePlaylistItem(
                mediaItem = mediaItem,
                onPlayNext = { binder?.player?.addNext( mediaItem ) },
                onEnqueue = {
                    binder?.player?.enqueue(mediaItem)
                },
                downloadStateParam = downloadsMapState[mediaItem.mediaId]?.state ?: Download.STATE_STOPPED,
                downloadedStateMediaParam = downloadStatesMap[mediaItem.mediaId] ?: DownloadedStateMedia.NOT_CACHED_OR_DOWNLOADED,
                swipeLeftActionParam = playlistSwipeLeftAction,
                swipeRightActionParam = playlistSwipeRightAction
            ) {
                SongItem(
                    song = song,
                    isLiked = likeStatesMap[song.id],
                    inPlaylist = playlistStatesMap[song.id],
                    itemSelector = itemSelector,
                    navController = navController,
                    modifier = Modifier.animateItem(),

                    onClick = {
                        search.hideIfEmpty()

                            val mediaItems = getSongs().fastMap( Song::asMediaItem )
                            val index = mediaItems.indexOfFirst { it.mediaId == song.id }
                            if (index >= 0) {
                                binder?.stopRadio()
                                binder?.player?.forcePlayAtIndex(mediaItems, index)
                            }
                        }
                    )
                }
            }
        }
    }
    } // CompositionLocalProvider
    }
}

