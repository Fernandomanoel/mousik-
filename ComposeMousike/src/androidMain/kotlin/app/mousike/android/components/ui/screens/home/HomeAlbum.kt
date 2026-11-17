@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")
package app.mousike.android.components.ui.screens.home


import app.mousike.android.components.settings.appearance.preview.LocalThemePreview
import app.mousike.android.components.settings.appearance.preview.ThemePreviewDemo
import app.mousike.android.components.ui.screens.home.onDevice.DeviceLibraryPullRefresh
import app.mousike.android.components.navigation.FloatingActionSiblingSpace
import app.mousike.android.components.search.SearchEmptyFeedback
import app.mousike.android.components.search.SearchNoResults

import app.mousike.android.uiRoundnessShape

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.zIndex
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.scale
import sh.calvin.reorderable.rememberReorderableLazyGridState
import sh.calvin.reorderable.ReorderableItem
import app.mousike.android.themed.mousike.component.playlist.PositionLock
import app.mousike.android.enums.AlbumSortBy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import app.mousike.android.components.navigation.rememberPageChromeContentPadding
import androidx.compose.ui.unit.Dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import app.mousike.android.core.ondevice.DEVICE_ALBUM_PREFIX
import app.mousike.android.core.ondevice.observeDeviceLibrary
import app.mousike.android.core.ondevice.DeviceLibraryResults
import app.mousike.android.core.ondevice.scanDeviceLibrary
import app.mousike.android.utils.getActiveProfile
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import app.mousike.android.R
import app.mousike.android.LocalTopBarOffset
import app.mousike.android.compose.persist.persistList
import app.mousike.android.core.database.BookmarkStateManager
import app.mousike.android.core.database.Database
import app.mousike.android.core.network.OfflineMode
import app.mousike.android.LocalPlayerServiceBinder
import app.mousike.android.colorPalette
import app.mousike.android.components.glass.GlassFloatingActions
import app.mousike.android.components.glass.glassFloatingActionContent
import app.mousike.android.components.glass.rememberGlassFloatingActionBackdrop
import app.mousike.android.enums.AlbumsType
import app.mousike.android.enums.FilterBy
import app.mousike.android.enums.SortOrder
import app.mousike.android.models.Album
import app.mousike.android.models.Song
import app.mousike.android.thumbnailShape
import app.mousike.android.ui.components.LocalMenuState
import app.mousike.android.components.glass.GlassToolbarButtons
import app.mousike.android.ui.components.tab.ItemSize
import app.mousike.android.ui.components.tab.TabHeader
import app.mousike.android.ui.components.tab.toolbar.Button
import app.mousike.android.ui.components.tab.toolbar.Randomizer
import app.mousike.android.components.menu.album.AlbumItemMenu
import app.mousike.android.components.menu.FilterMenu
import app.mousike.android.components.tab.ItemSelector
import app.mousike.android.ui.components.themed.FloatingActionsContainerWithScrollToTop
import app.mousike.android.ui.components.themed.HeaderInfo
import app.mousike.android.ui.components.themed.MultiFloatingActionsContainer
import app.mousike.android.ui.items.AlbumItem
import app.mousike.android.components.settings.accounts.isYouTubeSyncEnabled
import app.mousike.android.ui.styling.Dimensions
import app.mousike.android.ui.styling.LocalAppearance
import app.mousike.android.utils.Preference.HOME_ALBUMS_FAVORITES_SORT_BY
import app.mousike.android.utils.Preference.HOME_ALBUMS_FAVORITES_SORT_ORDER
import app.mousike.android.utils.Preference.HOME_ALBUMS_LIBRARY_SORT_BY
import app.mousike.android.utils.Preference.HOME_ON_DEVICE_ALBUMS_SORT_BY
import app.mousike.android.utils.Preference.HOME_ALBUMS_LIBRARY_SORT_ORDER
import app.mousike.android.utils.Preference.HOME_ON_DEVICE_ALBUMS_SORT_ORDER
import app.mousike.android.utils.Preference.HOME_ALBUMS_DISLIKED_SORT_BY
import app.mousike.android.utils.Preference.HOME_ALBUMS_DISLIKED_SORT_ORDER
import app.mousike.android.utils.Preference.HOME_ALBUM_ITEM_SIZE
import app.mousike.android.utils.albumTypeKey
import app.mousike.android.utils.autoSyncToolbutton
import app.mousike.android.utils.autosyncAlbumsKey
import app.mousike.android.utils.disableScrollingTextKey
import app.mousike.android.utils.filterByKey
import app.mousike.android.utils.importYTMLikedAlbums
import app.mousike.android.utils.rememberPreference
import app.mousike.android.utils.semiBold
import app.mousike.android.utils.formatAsTime
import app.mousike.android.utils.center
import app.mousike.android.utils.color
import app.mousike.android.utils.showFavoritesAlbumKey
import app.mousike.android.utils.showOnDeviceAlbumsKey
import app.mousike.android.core.ondevice.isDeviceAlbumId
import app.mousike.android.playback.services.isLocal
import app.mousike.android.core.ondevice.deviceLibraryEnabled
import app.mousike.android.utils.showDislikedAlbumKey
import app.mousike.android.utils.excludeDislikedAlbumsKey
import app.mousike.android.enums.DislikeMode
import app.mousike.android.utils.homeAlbumsOrderKey
import app.mousike.android.utils.showFloatingIconKey
import app.mousike.android.utils.homeAlbumsLibraryToolbarOrderKey
import app.mousike.android.utils.homeAlbumsOnDeviceToolbarOrderKey
import app.mousike.android.utils.homeAlbumsFavoritesToolbarOrderKey
import app.mousike.android.utils.homeAlbumsDislikedToolbarOrderKey
import app.mousike.android.utils.homeAlbumsFavoritesSortMenuOrderKey
import app.mousike.android.utils.homeAlbumsLibrarySortMenuOrderKey
import app.mousike.android.utils.homeAlbumsOnDeviceSortMenuOrderKey
import app.mousike.android.utils.homeAlbumsDislikedSortMenuOrderKey
import org.json.JSONArray
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

import app.mousike.android.components.Sort
import app.mousike.android.components.tab.Search
import app.mousike.android.components.tab.SongShuffler
import app.mousike.innertube.Innertube
import app.mousike.innertube.YtMusic
import app.mousike.innertube.requests.albumPage
import app.mousike.innertube.requests.searchPage
import app.mousike.innertube.utils.from
import app.mousike.android.core.utils.PropUtils
import app.mousike.android.utils.parseArtists
import app.mousike.android.models.SongAlbumMap
import app.mousike.android.ui.styling.onOverlay
import app.mousike.android.ui.styling.overlay
import app.mousike.android.utils.asMediaItem
import app.mousike.android.utils.player.addNextOffMain
import app.mousike.android.utils.player.enqueueOffMain
import app.mousike.android.utils.coroutines.MousikeDispatchers
import app.mousike.android.appContext
import kotlinx.coroutines.withContext
import app.mousike.android.ui.components.themed.Enqueue
import app.mousike.android.ui.components.themed.PlayNext
import app.mousike.android.ui.components.themed.PlaylistsMenu
import app.mousike.android.core.utils.Toaster
import app.mousike.android.components.dialog.export.ExportSongsToCSVDialog
import app.mousike.android.components.dialog.settings.HomeAlbumsToolbarSettingsDialog
import java.util.ArrayList
import androidx.compose.ui.input.nestedscroll.nestedScroll
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalTextApi
@UnstableApi
@SuppressLint("SuspiciousIndentation")
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun HomeAlbums(
    navController: NavController,
    onAlbumClick: (Album) -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    topContentInsetProvider: () -> Dp = { 0.dp },
    scrollContentInsetProvider: () -> Dp = topContentInsetProvider,
) {
    // Essentials
    val menuState = LocalMenuState.current
    val binder = LocalPlayerServiceBinder.current

    // Settings
    val disableScrollingText by rememberPreference(disableScrollingTextKey, false)
    var albumType by rememberPreference(albumTypeKey, AlbumsType.Library )
    val lazyGridState = AlbumsType.entries.map { tab ->
        key(tab) { rememberLazyGridState() }
    }[albumType.ordinal]
    val headerOffsetState = AlbumsType.entries.map { tab ->
        key(tab) { rememberSaveable { androidx.compose.runtime.mutableFloatStateOf(0f) } }
    }[albumType.ordinal]

    var items by key(albumType) { persistList<Album>( "home/albums/$albumType/items" ) }
    var itemsToFilter by key(albumType) { persistList<Album>( "home/albums/$albumType/itemsToFilter" ) }
    var filterBy by rememberPreference(filterByKey, FilterBy.All)
    val (colorPalette, typography) = LocalAppearance.current

    var itemsOnDisplay by key(albumType) { persistList<Album>( "home/albums/$albumType/on_display" ) }

    val search = Search(lazyGridState)
    val itemSelector = ItemSelector<Album>()

    val sort = when( albumType ) {
        AlbumsType.Favorites -> Sort( HOME_ALBUMS_FAVORITES_SORT_BY, HOME_ALBUMS_FAVORITES_SORT_ORDER, homeAlbumsFavoritesSortMenuOrderKey, "alb_fav" )
        AlbumsType.OnDevice -> Sort( HOME_ON_DEVICE_ALBUMS_SORT_BY, HOME_ON_DEVICE_ALBUMS_SORT_ORDER, homeAlbumsOnDeviceSortMenuOrderKey, "alb_dev" )
        AlbumsType.Library -> Sort( HOME_ALBUMS_LIBRARY_SORT_BY, HOME_ALBUMS_LIBRARY_SORT_ORDER, homeAlbumsLibrarySortMenuOrderKey, "alb_lib" )
        AlbumsType.Disliked -> Sort( HOME_ALBUMS_DISLIKED_SORT_BY, HOME_ALBUMS_DISLIKED_SORT_ORDER, homeAlbumsDislikedSortMenuOrderKey, "alb_dis" )
    }
    val positionLock = remember( sort.sortOrder ) { PositionLock(sort.sortOrder) }

    val itemSize = ItemSize.init( HOME_ALBUM_ITEM_SIZE )

    val randomizer = object: Randomizer<Album> {
        override fun getItems(): List<Album> = itemSelector.ifEmpty { itemsOnDisplay }
        override fun onClick(index: Int) {
            val items = itemSelector.ifEmpty { itemsOnDisplay }
            onAlbumClick( items[index] )
        }
    }

    val scope = rememberCoroutineScope()
    suspend fun getSelectedSongs(): List<Song> = withContext(MousikeDispatchers.DATA) {
        val deviceOnly = albumType == AlbumsType.OnDevice
        val deviceGroups = if (deviceOnly) appContext().scanDeviceLibrary().albumSongIds else emptyMap()
        val selected = itemSelector.ifEmpty { itemsOnDisplay }.filter { isDeviceAlbumId(it.id) == deviceOnly }
        val seen = HashSet<String>()
        val result = ArrayList<Song>()
        for( album in selected ) {
            for( song in Database.songAlbumMapTable.allSongsOfDirect( album.id ) ) {
                if (song.isLocal == deviceOnly && (!deviceOnly || song.id in deviceGroups[album.id].orEmpty()) && seen.add(song.id)) result.add(song)
            }
        }
        result
    }
    var selectedSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
    LaunchedEffect(itemSelector.size, itemsOnDisplay) {
        runCatching {
            selectedSongs = getSelectedSongs()
        }.onFailure { e ->
            Timber.tag("HomeAlbum").e(e, "Failed to load selected songs")
        }
    }
    fun getSelectedMediaItems(): List<androidx.media3.common.MediaItem> =
        selectedSongs.map { it.asMediaItem }

    val shuffle = SongShuffler { selectedSongs }
    val playNext = PlayNext {
        scope.launch {
            val mediaItems = withContext(MousikeDispatchers.DATA) { getSelectedSongs().map { it.asMediaItem } }
            binder?.player?.addNextOffMain( mediaItems, appContext() )
            itemSelector.isActive = false
        }
    }
    val enqueue = Enqueue {
        scope.launch {
            val mediaItems = withContext(MousikeDispatchers.DATA) { getSelectedSongs().map { it.asMediaItem } }
            binder?.player?.enqueueOffMain( mediaItems, appContext() )
            itemSelector.isActive = false
        }
    }
    val addToPlaylist = PlaylistsMenu.init(
        navController = navController,
        mediaItems = { _ -> getSelectedMediaItems() },
        onFailure = { throwable, preview ->
            Timber.tag("HomeAlbum").e(throwable, "Failed to add songs to playlist ${preview.playlist.name}")
        },
        finalAction = { itemSelector.isActive = false }
    )
    val exportDialog = ExportSongsToCSVDialog(
        playlistName = "Albums",
        songs = { selectedSongs }
    )

    val showOnDeviceTab by rememberPreference(showOnDeviceAlbumsKey, true)
    val showOnDevice = deviceLibraryEnabled() && showOnDeviceTab
    val onDeviceLabel = stringResource(R.string.library_on_device_title)
    val showFavoritesAlbum by rememberPreference(showFavoritesAlbumKey, true)
    val showDislikedAlbum by rememberPreference(excludeDislikedAlbumsKey, DislikeMode.Enabled)
    val homeAlbumsOrderPref by rememberPreference(homeAlbumsOrderKey, "")

    val favoritesLabel = stringResource(R.string.library_favorites_title)
    val allLabel = stringResource(R.string.common_option_all)
    val dislikedLabel = stringResource(R.string.library_disliked_title)
    val albumsDefaultOrder = listOf("all", "favorites", "disliked", "on_device")
    val labelMap = mapOf("on_device" to onDeviceLabel, "favorites" to favoritesLabel, "all" to allLabel, "disliked" to dislikedLabel)
    val typeMap = mapOf("on_device" to AlbumsType.OnDevice, "favorites" to AlbumsType.Favorites, "all" to AlbumsType.Library, "disliked" to AlbumsType.Disliked)
    val toggleMap = mapOf("on_device" to showOnDevice, "favorites" to showFavoritesAlbum, "all" to true, "disliked" to showDislikedAlbum.isEnabled)
    val buttonsList = remember(showOnDevice, onDeviceLabel, showDislikedAlbum, showFavoritesAlbum, homeAlbumsOrderPref, favoritesLabel, allLabel, dislikedLabel) {
        val order = try {
            val arr = JSONArray(homeAlbumsOrderPref)
            val parsed = (0 until arr.length()).map { arr.getString(it) }
            val valid = parsed.filter { it in albumsDefaultOrder }.toMutableList()
            for (id in albumsDefaultOrder) { if (id !in valid) valid.add(id) }
            valid
        } catch (_: Exception) { albumsDefaultOrder }
        order.mapNotNull { id ->
            if (toggleMap[id] == true) {
                val type = typeMap[id] ?: return@mapNotNull null
                val label = labelMap[id] ?: return@mapNotNull null
                type to label
            } else null
        }
    }

    LaunchedEffect(showFavoritesAlbum, showOnDevice, albumType) {
        if (!showOnDevice && albumType == AlbumsType.OnDevice) albumType = AlbumsType.Library
        if (!showFavoritesAlbum && albumType == AlbumsType.Favorites) albumType = AlbumsType.Library
    }

    if (!isYouTubeSyncEnabled()) {
        filterBy = FilterBy.All
    }

    val context = LocalContext.current
    val preview = LocalThemePreview.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val profile = getActiveProfile(context)
    var eligibleDeviceGroups by remember(profile) {
        mutableStateOf(if (preview == null) DeviceLibraryResults.current(profile)?.albumIds else emptySet())
    }
    LaunchedEffect(context, lifecycleOwner, albumType) {
        if (preview != null || albumType != AlbumsType.OnDevice) return@LaunchedEffect
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            context.observeDeviceLibrary().collect { eligibleDeviceGroups = it.albumIds }
        }
    }

    var searchDataReady by remember(sort.sortBy, sort.sortOrder, albumType) { mutableStateOf(false) }
    val groupsReady = albumType != AlbumsType.OnDevice || eligibleDeviceGroups != null
    var completedSearchQuery by remember(albumType, sort.sortBy, sort.sortOrder, groupsReady) { mutableStateOf<String?>(null) }
    val gridReady = searchDataReady && groupsReady && completedSearchQuery == search.inputValue
    LaunchedEffect( sort.sortBy, sort.sortOrder, albumType ) {
        if (preview != null) {
            val demoItems = ThemePreviewDemo.albums
            itemsToFilter = sort.sortOrder.applyTo(when (sort.sortBy) {
                AlbumSortBy.Title -> demoItems.sortedBy { it.title.orEmpty() }
                AlbumSortBy.Artist -> demoItems.sortedBy { it.authorsText.orEmpty() }
                AlbumSortBy.Year -> demoItems.sortedBy { it.year.orEmpty() }
                AlbumSortBy.Songs, AlbumSortBy.Duration -> demoItems.sortedBy { ThemePreviewDemo.songsForAlbum(it.id).size }
                else -> demoItems
            })
            searchDataReady = true
            return@LaunchedEffect
        }
        when ( albumType ) {
            AlbumsType.Favorites -> Database.albumTable.sortBookmarked( sort.sortBy, sort.sortOrder )
            AlbumsType.Library -> Database.albumTable.sortInLibrary( sort.sortBy, sort.sortOrder )
            AlbumsType.OnDevice -> kotlinx.coroutines.flow.combine(
                Database.albumTable.sortInLibrary(sort.sortBy, sort.sortOrder),
                Database.albumTable.sortInLibrary(AlbumSortBy.Title, sort.sortOrder),
            ) { sorted, all -> (sorted + all).distinctBy { it.id } }
            AlbumsType.Disliked -> Database.albumTable.allDisliked()
        }.collect {
            itemsToFilter = it
            searchDataReady = true
        }
    }
    LaunchedEffect( searchDataReady, groupsReady, itemsToFilter, albumType, filterBy, showDislikedAlbum, eligibleDeviceGroups, search.inputValue ) {
        if (!searchDataReady || !groupsReady) return@LaunchedEffect
        items = when {
            albumType == AlbumsType.OnDevice -> itemsToFilter.filter { it.id in eligibleDeviceGroups.orEmpty() }
            filterBy == FilterBy.All -> itemsToFilter.filterNot { isDeviceAlbumId(it.id) }
            filterBy == FilterBy.YoutubeLibrary -> itemsToFilter.filter { !isDeviceAlbumId(it.id) && it.isYoutubeAlbum }
            else -> itemsToFilter.filterNot { isDeviceAlbumId(it.id) || it.isYoutubeAlbum }
        }.let { list ->
            if (albumType != AlbumsType.Disliked) {
                list.filter {
                    it.dislikedAt == null &&
                        (!it.id.startsWith(DEVICE_ALBUM_PREFIX) || eligibleDeviceGroups?.contains(it.id) != false)
                }
            } else list
        }

        itemsOnDisplay = items.filter {
            it.title?.contains( search.inputValue, true) ?: false
                    || it.year?.contains( search.inputValue, true) ?: false
                    || it.authorsText?.contains( search.inputValue, true) ?: false
        }
        completedSearchQuery = search.inputValue
    }
    SearchEmptyFeedback(
        query = search.inputValue,
        empty = itemsOnDisplay.isEmpty(),
        loading = !gridReady
    )


    LaunchedEffect( Unit ) {
        if (preview != null) return@LaunchedEffect
        // TODO Convert to fetch from the internet
        Database.asyncTransaction {
            // Only occurs when album doesn't have thumbnailUrl assigned
            items.filter { it.thumbnailUrl == null }
                 .forEach { album ->
                     /**
                      * The album's cover: the most frequent [Song.thumbnailUrl]
                      * among its songs. A user-changed thumbnail on one song does
                      * not shift the result (all others share the url), and even
                      * when most songs changed, any url shared by 2 songs wins.
                      */
                     val coverUrl = songAlbumMapTable.allSongsOfDirect( album.id )
                                          .groupingBy( Song::thumbnailUrl )
                                          .eachCount()
                                          .maxByOrNull { it.value }
                                          ?.key
                     coverUrl?.let { albumTable.updateCover( album.id, it ) }
                 }
        }
    }

    val sync = autoSyncToolbutton(R.string.sync_albums, autosyncAlbumsKey) { scope.launch { importYTMLikedAlbums(force = true) } }

    // Full offline mode (spec-full-offline-mode): the network sync action is removed.
    val offlineMode by OfflineMode.rememberEnabled()

    val doAutoSync by rememberPreference(autosyncAlbumsKey, false)
    var justSynced by rememberSaveable { mutableStateOf(!doAutoSync) }


    LaunchedEffect(justSynced, doAutoSync) {
        if (preview == null && !justSynced && importYTMLikedAlbums())
            justSynced = true
    }

    val floatingBackdrop = rememberGlassFloatingActionBackdrop()

    DeviceLibraryPullRefresh(albumType == AlbumsType.OnDevice) {
        Box(
            modifier = Modifier
                .background(colorPalette().background0)
                .fillMaxHeight()
                .fillMaxWidth()
        ) {

            Column( Modifier.fillMaxSize() ) {

                val homeAlbumsToolbarOrderPrefLibrary by rememberPreference(homeAlbumsLibraryToolbarOrderKey, "")
                val homeAlbumsToolbarOrderPrefFavorites by rememberPreference(homeAlbumsFavoritesToolbarOrderKey, "")
                val homeAlbumsToolbarOrderPrefDisliked by rememberPreference(homeAlbumsDislikedToolbarOrderKey, "")

                val onDeviceToolbarOrder by rememberPreference(homeAlbumsOnDeviceToolbarOrderKey, "")

                val currentToolbarOrderPref = when (albumType) {
                    AlbumsType.OnDevice -> onDeviceToolbarOrder
                    AlbumsType.Favorites -> homeAlbumsToolbarOrderPrefFavorites
                    AlbumsType.Disliked -> homeAlbumsToolbarOrderPrefDisliked
                    else -> homeAlbumsToolbarOrderPrefLibrary
                }

                val toolbarButtons = buildList<Button> {
                    val defaultToolbarOrder = if (albumType == AlbumsType.OnDevice) {
                        HomeAlbumsToolbarSettingsDialog.onDeviceButtonIds
                    } else {
                        HomeAlbumsToolbarSettingsDialog.allButtonIds
                    }
                    val order = try {
                        if (currentToolbarOrderPref.isBlank()) defaultToolbarOrder else {
                            val arr = JSONArray(currentToolbarOrderPref)
                            (0 until arr.length()).map { arr.getString(it) }.distinct()
                        }
                    } catch (_: Exception) { defaultToolbarOrder }

                    order.filter { it in defaultToolbarOrder }.forEach { id ->
                        when(id) {
                            "sort" -> add(sort)
                            "position_lock" -> { if (sort.sortBy == AlbumSortBy.Custom) add(positionLock) }
                            "sync" -> { if (isYouTubeSyncEnabled() && !offlineMode) add(sync) }
                            "search" -> add(search)
                            "randomizer" -> add(randomizer)
                            "shuffle" -> add(shuffle)
                            "item_selector" -> add(itemSelector)
                            "play_next" -> add(playNext)
                            "enqueue" -> add(enqueue)
                            "add_to_playlist" -> add(addToPlaylist)
                            "export_dialog" -> add(exportDialog)
                            "item_size" -> add(itemSize)
                        }
                    }
                }


                val hapticFeedback = LocalHapticFeedback.current
                
                val reorderableLazyGridState = sh.calvin.reorderable.rememberReorderableLazyGridState(
                    lazyGridState = lazyGridState
                ) { from, to ->
                    val mutableItems = itemsOnDisplay.toMutableList()
                    val fromIndex = mutableItems.indexOfFirst { it.id == from.key }
                    val toIndex = mutableItems.indexOfFirst { it.id == to.key }

                    if (fromIndex != -1 && toIndex != -1) {
                        val movedItem = mutableItems.removeAt(fromIndex)
                        mutableItems.add(toIndex, movedItem)
                        itemsOnDisplay = mutableItems
                    }
                }

                val header: @Composable (MutableFloatState, MutableIntState) -> Unit = { titleOffsetState, titleHeightState ->
                    Column {
                        Column {
                    CollapsibleTitleRow( titleOffsetState, titleHeightState ) {
                        TabHeader(R.string.library_albums_title) {
                            HeaderInfo(items.size.toString(), R.drawable.album)
                        }
                    }
                    exportDialog.Render()
                    GlassToolbarButtons( toolbarButtons, disableAnimation = true )
                    search.SearchBar( this@Column )
                }

                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .fillMaxWidth()
                    ) {
                        GlassHomeChips(
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp),
                            chips = buttonsList,
                            currentValue = albumType,
                            onValueUpdate = { albumType = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    AnimatedVisibility(
                        visible = albumType != AlbumsType.OnDevice && isYouTubeSyncEnabled(),
                        enter = fadeIn(animationSpec = tween(200)) + expandVertically(animationSpec = tween(200)),
                        exit = fadeOut(animationSpec = tween(200)) + shrinkVertically(animationSpec = tween(200))
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 8.dp)
                                .fillMaxWidth()
                        ) {
                            GlassHomeFilterChip(
                                label = {
                                    Text(
                                        text = when (filterBy) {
                                            FilterBy.All -> stringResource(R.string.common_option_all)
                                            FilterBy.Local -> stringResource(R.string.library_on_device_title)
                                            FilterBy.YoutubeLibrary -> stringResource(R.string.library_youtube_music_title)
                                        }
                                    )
                                },
                                selected = true,
                                shape = uiRoundnessShape(),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = colorPalette().background1,
                                    labelColor = colorPalette().text,
                                    selectedContainerColor = colorPalette().accent,
                                    selectedLabelColor = colorPalette().onAccent,
                                ),
                                onClick = {
                                    menuState.display {
                                        FilterMenu(
                                            title = stringResource(R.string.library_filter_by_title),
                                            onDismiss = menuState::hide,
                                            onAll = { filterBy = FilterBy.All },
                                            onYoutubeLibrary = { filterBy = FilterBy.YoutubeLibrary },
                                            onLocal = { filterBy = FilterBy.Local }
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize().glassFloatingActionContent(floatingBackdrop)) {
                    val albumIds = remember(itemsOnDisplay) { itemsOnDisplay.map { it.id } }
                    val bookmarkStatesMap by remember(albumIds) {
                        BookmarkStateManager.getAlbumBookmarkStates(albumIds)
                    }.collectAsStateWithLifecycle(emptyMap(), context = MousikeDispatchers.DATA)

                    val headerHeightState = remember { androidx.compose.runtime.mutableIntStateOf(0) }
                    val headerHeight by headerHeightState
                    val headerOffset by headerOffsetState

                    val nestedScrollConnection = rememberHomeHeaderConnection(headerHeight, headerOffsetState, itemsOnDisplay.isNotEmpty())

                    HomeChromeLayout(
                        modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection),
                        header = {
                            HomeCollapsingHeader(
                                headerOffset = headerOffsetState,
                                headerHeight = headerHeightState,
                                topContentInset = topContentInsetProvider,
                            ) { header(headerOffsetState, headerHeightState) }
                        },
                    ) {
                        val headerDensity = androidx.compose.ui.platform.LocalDensity.current
                        if (gridReady) LazyVerticalGrid(
                            state = lazyGridState,
                            columns = GridCells.Adaptive( itemSize.size.dp ),
                            contentPadding = rememberPageChromeContentPadding(
                                topInsetProvider = scrollContentInsetProvider,
                                base = PaddingValues(bottom = Dimensions.bottomSpacer),
                                extraTopProvider = { with(headerDensity) { headerHeight.toDp() } },
                            ),
                            modifier = Modifier.background( colorPalette().background0 ).fillMaxSize()
                        ) {
                            items(
                                items = itemsOnDisplay.distinctBy { it.id },
                                key = { it.id }
                            ) { album ->
                                ReorderableItem(
                                    reorderableLazyGridState,
                                    key = album.id
                                ) { isDraggingItem ->
                                    Box(modifier = Modifier) {
                                        if (!positionLock.isLocked() && sort.sortBy == AlbumSortBy.Custom && sort.sortOrder == SortOrder.Ascending) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(4.dp)
                                                    .size(32.dp)
                                                    .align(Alignment.TopEnd)
                                                    .zIndex(2f)
                                                    .draggableHandle(
                                                        onDragStarted = {
                                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        },
                                                        onDragStopped = {
                                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                            val currentItems = itemsOnDisplay.toList()
                                                            Database.asyncTransaction {
                                                                currentItems.forEachIndexed { index, album ->
                                                                    albumTable.updatePosition(album.id, index)
                                                                }
                                                            }
                                                        }
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.reorder),
                                                    contentDescription = null,
                                                    tint = if (isDraggingItem) colorPalette().accent else colorPalette().textDisabled
                                                )
                                            }
                                        }

                                        var position by remember {
                                            mutableIntStateOf(0)
                                        }
                                        val context = LocalContext.current

                                        AlbumItem(
                                            alternative = true,
                                            showAuthors = true,
                                            album = album,
                                            thumbnailSizeDp = itemSize.size.dp,
                                            thumbnailSizePx = itemSize.size.px,
                                            bookmarkState = bookmarkStatesMap[album.id],
                                            modifier = Modifier
                                                .clip(uiRoundnessShape()).combinedClickable(

                                                    onLongClick = {
                                                        menuState.display {
                                                            AlbumItemMenu(
                                                                navController = navController,
                                                                album = album,
                                                                binder = binder
                                                            ).MenuComponent()
                                                        }
                                                    },
                                                    onClick = {
                                                        if( itemSelector.isActive ) {
                                                            if( album in itemSelector ) itemSelector.remove( album )
                                                            else itemSelector.add( album )
                                                        } else {
                                                            search.hideIfEmpty()
                                                            onAlbumClick( album )
                                                        }
                                                    }
                                                )
                                                .clip(thumbnailShape()),
                                            disableScrollingText = disableScrollingText,
                                            isYoutubeAlbum = album.isYoutubeAlbum,
                                            thumbnailOverlay = {
                                                if( itemSelector.isActive ) {
                                                    key(itemSelector.size) {
                                                        Icon(
                                                            painter = painterResource(if (album in itemSelector) R.drawable.checked_filled else R.drawable.unchecked_outline),
                                                            contentDescription = null,
                                                            tint = if (album in itemSelector) colorPalette().accent else colorPalette().text,
                                                            modifier = Modifier
                                                                .padding(4.dp)
                                                                .size(24.dp)
                                                                .align(Alignment.TopStart)
                                                                .zIndex(2f)
                                                        )
                                                    }
                                                } else if (sort.sortBy == AlbumSortBy.PlayCount) {
                                                    val playCount by (if (preview == null) Database.eventTable.getAlbumPlayCount(album.id) else kotlinx.coroutines.flow.flowOf(0)).collectAsStateWithLifecycle(initialValue = 0, context = MousikeDispatchers.DATA)
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(thumbnailShape())
                                                            .background(colorPalette().overlay)
                                                    ) {
                                                        BasicText(
                                                            text = playCount.toString(),
                                                            style = typography.s.semiBold.center.color(colorPalette().onOverlay),
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            modifier = Modifier.align(Alignment.Center).basicMarquee(iterations = Int.MAX_VALUE)
                                                        )
                                                    }
                                                } else if (sort.sortBy == AlbumSortBy.ListeningTime) {
                                                    val playTime by (if (preview == null) Database.eventTable.getAlbumTotalPlayTime(album.id) else kotlinx.coroutines.flow.flowOf(0L)).collectAsStateWithLifecycle(initialValue = 0L, context = MousikeDispatchers.DATA)
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(thumbnailShape())
                                                            .background(colorPalette().overlay)
                                                    ) {
                                                        BasicText(
                                                            text = formatAsTime(playTime),
                                                            style = typography.s.semiBold.center.color(colorPalette().onOverlay),
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis,
                                                            modifier = Modifier.align(Alignment.Center).basicMarquee(iterations = Int.MAX_VALUE)
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                if (gridReady && itemsOnDisplay.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(rememberPageChromeContentPadding(topInsetProvider = scrollContentInsetProvider, base = PaddingValues(top = with(androidx.compose.ui.platform.LocalDensity.current) { (headerHeight + headerOffset).coerceAtLeast(0f).toDp() }, bottom = Dimensions.bottomSpacer))),
                        contentAlignment = Alignment.Center
                    ) {
                        if (search.inputValue.isBlank()) SearchNoResults(text = stringResource(R.string.common_empty_items))
                    }
                }
                    }
            }
            }

            GlassFloatingActions(floatingBackdrop) {
                val showFloatingIcon by rememberPreference(showFloatingIconKey, false)
                val barsToggle = app.mousike.android.components.navigation.sideAwareLandscapeBarsToggleButton()
                FloatingActionsContainerWithScrollToTop(
                    reservedEndSpace = if (showFloatingIcon) FloatingActionSiblingSpace else 0.dp,
                    lazyGridState = lazyGridState,
                    iconId = barsToggle?.iconId,
                    onClick = barsToggle?.onClick
                )

                if ( showFloatingIcon )
                    MultiFloatingActionsContainer(
                        iconId = R.drawable.search,
                        onClick = onSearchClick,
                        onClickSettings = onSettingsClick,
                        onClickSearch = onSearchClick
                    )
            }
        }
    }
}








