@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")
package app.mousike.android.components.ui.screens.home


import app.mousike.android.components.settings.appearance.preview.LocalThemePreview
import app.mousike.android.components.settings.appearance.preview.ThemePreviewDemo
import app.mousike.android.components.ui.screens.home.onDevice.DeviceLibraryPullRefresh
import app.mousike.android.components.navigation.FloatingActionSiblingSpace
import app.mousike.android.components.search.SearchEmptyFeedback
import app.mousike.android.components.search.SearchNoResults

import androidx.compose.ui.draw.clip

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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.zIndex
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import app.mousike.android.core.ondevice.DEVICE_ARTIST_PREFIX
import app.mousike.android.core.ondevice.observeDeviceLibrary
import app.mousike.android.core.ondevice.DeviceLibraryResults
import app.mousike.android.core.ondevice.scanDeviceLibrary
import app.mousike.android.utils.getActiveProfile
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.scale
import sh.calvin.reorderable.rememberReorderableLazyGridState
import sh.calvin.reorderable.ReorderableItem
import app.mousike.android.themed.mousike.component.playlist.PositionLock
import app.mousike.android.enums.ArtistSortBy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import app.mousike.android.components.navigation.rememberPageChromeContentPadding
import androidx.compose.ui.unit.Dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import app.mousike.android.R
import app.mousike.android.compose.persist.persistList
import app.mousike.innertube.Innertube
import app.mousike.innertube.requests.searchPage
import app.mousike.innertube.utils.from
import app.mousike.innertube.YtMusic
import app.mousike.android.core.database.BookmarkStateManager
import app.mousike.android.core.database.Database
import app.mousike.android.core.network.OfflineMode
import app.mousike.android.colorPalette
import app.mousike.android.components.glass.GlassFloatingActions
import app.mousike.android.components.glass.glassFloatingActionContent
import app.mousike.android.components.glass.rememberGlassFloatingActionBackdrop
import app.mousike.android.appContext
import app.mousike.android.LocalPlayerServiceBinder
import kotlinx.coroutines.flow.first
import app.mousike.android.ui.components.themed.Enqueue
import app.mousike.android.ui.components.themed.PlayNext
import app.mousike.android.utils.asMediaItem
import app.mousike.android.models.Song
import app.mousike.android.utils.player.addNextOffMain
import app.mousike.android.utils.player.enqueueOffMain
import app.mousike.android.utils.coroutines.MousikeDispatchers
import app.mousike.android.enums.ArtistsType
import app.mousike.android.enums.FilterBy
import app.mousike.android.enums.SortOrder
import app.mousike.android.models.Artist
import app.mousike.android.ui.components.LocalMenuState
import app.mousike.android.components.glass.GlassToolbarButtons
import app.mousike.android.ui.components.tab.ItemSize
import app.mousike.android.ui.components.tab.TabHeader
import app.mousike.android.ui.components.tab.toolbar.Button
import app.mousike.android.ui.components.tab.toolbar.Randomizer
import app.mousike.android.components.menu.FilterMenu
import app.mousike.android.components.tab.ItemSelector
import app.mousike.android.ui.components.themed.FloatingActionsContainerWithScrollToTop
import app.mousike.android.ui.components.themed.HeaderInfo
import app.mousike.android.ui.components.themed.MultiFloatingActionsContainer
import app.mousike.android.ui.items.ArtistItem
import app.mousike.android.components.settings.accounts.isYouTubeSyncEnabled
import app.mousike.android.ui.styling.Dimensions
import app.mousike.android.ui.styling.LocalAppearance
import app.mousike.android.ui.styling.onOverlay
import app.mousike.android.ui.styling.overlay
import app.mousike.android.utils.Preference.HOME_ARTISTS_FAVORITES_SORT_BY
import app.mousike.android.utils.Preference.HOME_ARTISTS_FAVORITES_SORT_ORDER
import app.mousike.android.utils.Preference.HOME_ARTISTS_LIBRARY_SORT_BY
import app.mousike.android.utils.Preference.HOME_ON_DEVICE_ARTISTS_SORT_BY
import app.mousike.android.utils.Preference.HOME_ARTISTS_LIBRARY_SORT_ORDER
import app.mousike.android.utils.Preference.HOME_ON_DEVICE_ARTISTS_SORT_ORDER
import app.mousike.android.utils.Preference.HOME_ARTISTS_DISLIKED_SORT_BY
import app.mousike.android.utils.Preference.HOME_ARTISTS_DISLIKED_SORT_ORDER
import app.mousike.android.utils.Preference.HOME_ARTIST_ITEM_SIZE
import app.mousike.android.utils.artistTypeKey
import app.mousike.android.utils.autoSyncToolbutton
import app.mousike.android.utils.autosyncArtistsKey
import app.mousike.android.utils.disableScrollingTextKey
import app.mousike.android.utils.filterByKey
import app.mousike.android.utils.importYTMSubscribedChannels
import app.mousike.android.utils.rememberPreference
import app.mousike.android.utils.semiBold
import app.mousike.android.utils.formatAsTime
import app.mousike.android.utils.center
import app.mousike.android.utils.color
import app.mousike.android.utils.showFavoritesArtistKey
import app.mousike.android.utils.showOnDeviceArtistsKey
import app.mousike.android.core.ondevice.isDeviceArtistId
import app.mousike.android.playback.services.isLocal
import app.mousike.android.core.ondevice.deviceLibraryEnabled
import app.mousike.android.utils.showDislikedArtistKey
import app.mousike.android.utils.excludeDislikedArtistsKey
import app.mousike.android.enums.DislikeMode
import app.mousike.android.utils.homeArtistsOrderKey
import app.mousike.android.utils.showFloatingIconKey
import app.mousike.android.utils.homeArtistsLibraryToolbarOrderKey
import app.mousike.android.utils.homeArtistsOnDeviceToolbarOrderKey
import app.mousike.android.utils.homeArtistsFavoritesToolbarOrderKey
import app.mousike.android.utils.homeArtistsDislikedToolbarOrderKey
import app.mousike.android.utils.homeArtistsFavoritesSortMenuOrderKey
import app.mousike.android.utils.homeArtistsLibrarySortMenuOrderKey
import app.mousike.android.utils.homeArtistsOnDeviceSortMenuOrderKey
import app.mousike.android.utils.homeArtistsDislikedSortMenuOrderKey
import org.json.JSONArray
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import app.mousike.android.components.Sort
import app.mousike.android.components.tab.Search
import app.mousike.android.components.tab.SongShuffler
import app.mousike.android.core.utils.PropUtils
import app.mousike.android.components.menu.artist.LocalArtistItemMenu
import app.mousike.android.core.utils.Toaster
import app.mousike.android.components.dialog.export.ExportSongsToCSVDialog
import app.mousike.android.components.dialog.settings.HomeArtistsToolbarSettingsDialog
import app.mousike.android.artistThumbnailShape
import app.mousike.android.MODIFIED_PREFIX
import app.mousike.android.ui.components.themed.PlaylistsMenu
import androidx.navigation.NavController
import java.util.ArrayList

import timber.log.Timber

@ExperimentalMaterial3Api
@UnstableApi
@SuppressLint("SuspiciousIndentation")
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun HomeArtists(
    navController: NavController,
    onArtistClick: (Artist) -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    topContentInsetProvider: () -> Dp = { 0.dp },
    scrollContentInsetProvider: () -> Dp = topContentInsetProvider,
) {
    // Essentials
    val (colorPalette, typography) = LocalAppearance.current
    val menuState = LocalMenuState.current
    val binder = LocalPlayerServiceBinder.current
    val coroutineScope = rememberCoroutineScope()

    // Settings
    var artistType by rememberPreference(artistTypeKey, ArtistsType.Library )
    val lazyGridState = ArtistsType.entries.map { tab ->
        key(tab) { rememberLazyGridState() }
    }[artistType.ordinal]
    val headerOffsetState = ArtistsType.entries.map { tab ->
        key(tab) { rememberSaveable { androidx.compose.runtime.mutableFloatStateOf(0f) } }
    }[artistType.ordinal]
    var filterBy by rememberPreference(filterByKey, FilterBy.All)


    var items by key(artistType) { persistList<Artist>( "home/artists/$artistType/items") }
    var itemsToFilter by key(artistType) { persistList<Artist>( "home/artists/$artistType/itemsToFilter" ) }

    var itemsOnDisplay by key(artistType) { persistList<Artist>( "home/artists/$artistType/on_display" ) }

    val disableScrollingText by rememberPreference(disableScrollingTextKey, false)

    val search = Search(lazyGridState)
    val itemSelector = ItemSelector<Artist>()

    val sort = when( artistType ) {
        ArtistsType.Favorites -> Sort( HOME_ARTISTS_FAVORITES_SORT_BY, HOME_ARTISTS_FAVORITES_SORT_ORDER, homeArtistsFavoritesSortMenuOrderKey, "art_fav" )
        ArtistsType.OnDevice -> Sort( HOME_ON_DEVICE_ARTISTS_SORT_BY, HOME_ON_DEVICE_ARTISTS_SORT_ORDER, homeArtistsOnDeviceSortMenuOrderKey, "art_dev" )
        ArtistsType.Library -> Sort( HOME_ARTISTS_LIBRARY_SORT_BY, HOME_ARTISTS_LIBRARY_SORT_ORDER, homeArtistsLibrarySortMenuOrderKey, "art_lib" )
        ArtistsType.Disliked -> Sort( HOME_ARTISTS_DISLIKED_SORT_BY, HOME_ARTISTS_DISLIKED_SORT_ORDER, homeArtistsDislikedSortMenuOrderKey, "art_dis" )
    }
    val positionLock = remember( sort.sortOrder ) { PositionLock(sort.sortOrder) }

    val itemSize = ItemSize.init( HOME_ARTIST_ITEM_SIZE )

    val randomizer = object: Randomizer<Artist> {
        override fun getItems(): List<Artist> = itemSelector.ifEmpty { itemsOnDisplay }
        override fun onClick(index: Int) {
            val items = itemSelector.ifEmpty { itemsOnDisplay }
            onArtistClick( items[index] )
        }
    }

    suspend fun getSelectedSongs(): List<Song> = withContext(MousikeDispatchers.DATA) {
        val deviceOnly = artistType == ArtistsType.OnDevice
        val deviceGroups = if (deviceOnly) appContext().scanDeviceLibrary().artistSongIds else emptyMap()
        val selected = itemSelector.ifEmpty { itemsOnDisplay }.filter { isDeviceArtistId(it.id) == deviceOnly }
        val seen = HashSet<String>()
        val result = ArrayList<Song>()
        for( artist in selected ) {
            val songs = Database.songArtistMapTable.allSongsBy( artist.id ).first()
            for( song in songs ) {
                if (song.isLocal == deviceOnly && (!deviceOnly || song.id in deviceGroups[artist.id].orEmpty()) && seen.add(song.id)) result.add(song)
            }
        }
        result
    }
    var selectedSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
    LaunchedEffect(itemSelector.size, itemsOnDisplay) {
        runCatching {
            selectedSongs = getSelectedSongs()
        }.onFailure { e ->
            Timber.tag("HomeArtist").e(e, "Failed to load selected songs")
        }
    }
    fun getSelectedMediaItems(): List<androidx.media3.common.MediaItem> =
        selectedSongs.map { it.asMediaItem }

    val shuffle = SongShuffler { selectedSongs }
    val playNext = PlayNext {
        coroutineScope.launch {
            val mediaItems = withContext(MousikeDispatchers.DATA) { getSelectedSongs().map { it.asMediaItem } }
            binder?.player?.addNextOffMain( mediaItems, appContext() )
            itemSelector.isActive = false
        }
    }
    val enqueue = Enqueue {
        coroutineScope.launch {
            val mediaItems = withContext(MousikeDispatchers.DATA) { getSelectedSongs().map { it.asMediaItem } }
            binder?.player?.enqueueOffMain( mediaItems, appContext() )
            itemSelector.isActive = false
        }
    }
    val addToPlaylist = PlaylistsMenu.init(
        navController = navController,
        mediaItems = { _ -> getSelectedMediaItems() },
        onFailure = { throwable, preview ->
            Timber.tag("HomeArtist").e(throwable, "Failed to add songs to playlist ${preview.playlist.name}")
        },
        finalAction = { itemSelector.isActive = false }
    )
    val exportDialog = ExportSongsToCSVDialog(
        playlistName = "Artists",
        songs = { selectedSongs }
    )

    val showOnDeviceTab by rememberPreference(showOnDeviceArtistsKey, true)
    val showOnDevice = deviceLibraryEnabled() && showOnDeviceTab
    val onDeviceLabel = stringResource(R.string.library_on_device_title)
    val showFavoritesArtist by rememberPreference(showFavoritesArtistKey, true)
    val showDislikedArtist by rememberPreference(excludeDislikedArtistsKey, DislikeMode.Enabled)
    val homeArtistsOrderPref by rememberPreference(homeArtistsOrderKey, "")

    val favoritesLabel = stringResource(R.string.library_favorites_title)
    val allLabel = stringResource(R.string.common_option_all)
    val dislikedLabel = stringResource(R.string.library_disliked_title)
    val artistsDefaultOrder = listOf("all", "favorites", "disliked", "on_device")
    val labelMap = mapOf("on_device" to onDeviceLabel, "favorites" to favoritesLabel, "all" to allLabel, "disliked" to dislikedLabel)
    val typeMap = mapOf("on_device" to ArtistsType.OnDevice, "favorites" to ArtistsType.Favorites, "all" to ArtistsType.Library, "disliked" to ArtistsType.Disliked)
    val toggleMap = mapOf("on_device" to showOnDevice, "favorites" to showFavoritesArtist, "all" to true, "disliked" to showDislikedArtist.isEnabled)
    val buttonsList = remember(showOnDevice, onDeviceLabel, showDislikedArtist, showFavoritesArtist, homeArtistsOrderPref, favoritesLabel, allLabel, dislikedLabel) {
        val order = try {
            val arr = JSONArray(homeArtistsOrderPref)
            val parsed = (0 until arr.length()).map { arr.getString(it) }
            val valid = parsed.filter { it in artistsDefaultOrder }.toMutableList()
            for (id in artistsDefaultOrder) { if (id !in valid) valid.add(id) }
            valid
        } catch (_: Exception) { artistsDefaultOrder }
        order.mapNotNull { id ->
            if (toggleMap[id] == true) {
                val type = typeMap[id] ?: return@mapNotNull null
                val label = labelMap[id] ?: return@mapNotNull null
                type to label
            } else null
        }
    }

    LaunchedEffect(showFavoritesArtist, showOnDevice, artistType) {
        if (!showOnDevice && artistType == ArtistsType.OnDevice) artistType = ArtistsType.Library
        if (!showFavoritesArtist && artistType == ArtistsType.Favorites) artistType = ArtistsType.Library
    }

    if (!isYouTubeSyncEnabled()) {
        filterBy = FilterBy.All
    }

    val context = LocalContext.current
    val preview = LocalThemePreview.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val profile = getActiveProfile(context)
    var eligibleDeviceGroups by remember(profile) {
        mutableStateOf(if (preview == null) DeviceLibraryResults.current(profile)?.artistIds else emptySet())
    }
    LaunchedEffect(context, lifecycleOwner, artistType) {
        if (preview != null || artistType != ArtistsType.OnDevice) return@LaunchedEffect
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            context.observeDeviceLibrary().collect { eligibleDeviceGroups = it.artistIds }
        }
    }

    var searchDataReady by remember(sort.sortBy, sort.sortOrder, artistType) { mutableStateOf(false) }
    val groupsReady = artistType != ArtistsType.OnDevice || eligibleDeviceGroups != null
    var completedSearchQuery by remember(artistType, sort.sortBy, sort.sortOrder, groupsReady) { mutableStateOf<String?>(null) }
    val gridReady = searchDataReady && groupsReady && completedSearchQuery == search.inputValue
    LaunchedEffect( Unit, sort.sortBy, sort.sortOrder, artistType ) {
        if (preview != null) {
            val demoItems = ThemePreviewDemo.artists
            itemsToFilter = sort.sortOrder.applyTo(when (sort.sortBy) {
                ArtistSortBy.Name -> demoItems.sortedBy { it.name.orEmpty() }
                else -> demoItems
            })
            searchDataReady = true
            return@LaunchedEffect
        }
        when( artistType ) {
            ArtistsType.Favorites -> Database.artistTable.sortFollowing( sort.sortBy, sort.sortOrder )
            ArtistsType.Library -> Database.artistTable.sortInLibrary( sort.sortBy, sort.sortOrder )
            ArtistsType.OnDevice -> kotlinx.coroutines.flow.combine(
                Database.artistTable.sortInLibrary(sort.sortBy, sort.sortOrder),
                Database.artistTable.sortInLibrary(ArtistSortBy.Name, sort.sortOrder),
            ) { sorted, all -> (sorted + all).distinctBy { it.id } }
            ArtistsType.Disliked -> Database.artistTable.allDisliked()
        }.collect {
            itemsToFilter = it
            searchDataReady = true
        }
    }
    LaunchedEffect( searchDataReady, groupsReady, itemsToFilter, artistType, filterBy, showDislikedArtist, eligibleDeviceGroups, search.inputValue ) {
        if (!searchDataReady || !groupsReady) return@LaunchedEffect
        items = when {
            artistType == ArtistsType.OnDevice -> itemsToFilter.filter { it.id in eligibleDeviceGroups.orEmpty() }
            filterBy == FilterBy.All -> itemsToFilter.filterNot { isDeviceArtistId(it.id) }
            filterBy == FilterBy.YoutubeLibrary -> itemsToFilter.filter { !isDeviceArtistId(it.id) && it.isYoutubeArtist }
            else -> itemsToFilter.filterNot { isDeviceArtistId(it.id) || it.isYoutubeArtist }
        }.let { list ->
            if (artistType != ArtistsType.Disliked) {
                list.filter {
                    it.dislikedAt == null &&
                        (!it.id.startsWith(DEVICE_ARTIST_PREFIX) || eligibleDeviceGroups?.contains(it.id) != false)
                }
            } else list
        }

        itemsOnDisplay = items.filter {
            it.name?.contains( search.inputValue, true ) ?: false
        }
        completedSearchQuery = search.inputValue
    }
    SearchEmptyFeedback(
        query = search.inputValue,
        empty = itemsOnDisplay.isEmpty(),
        loading = !gridReady
    )

    if (items.any{it.thumbnailUrl == null}) {
        LaunchedEffect(Unit) {
            if (preview != null) return@LaunchedEffect
            withContext(MousikeDispatchers.DATA) {
                items.filter { it.thumbnailUrl == null && !isDeviceArtistId(it.id) }.forEach { artist ->
                    coroutineScope.launch(MousikeDispatchers.DATA) {
                        val artistThumbnail = YtMusic.getArtistPage(artist.id.removePrefix(MODIFIED_PREFIX)).getOrNull()?.artist?.thumbnail?.url
                        Database.asyncTransaction {
                            artistTable.update( artist.copy(thumbnailUrl = artistThumbnail) )
                        }
                    }
                }
            }
        }
    }

    val sync = autoSyncToolbutton(R.string.sync_channels, autosyncArtistsKey) { coroutineScope.launch { importYTMSubscribedChannels(force = true) } }

    // Full offline mode (spec-full-offline-mode): the network sync action is removed.
    val offlineMode by OfflineMode.rememberEnabled()

    val doAutoSync by rememberPreference(autosyncArtistsKey, false)
    var justSynced by rememberSaveable { mutableStateOf(!doAutoSync) }


    LaunchedEffect(justSynced, doAutoSync) {
        if (preview == null && !justSynced && importYTMSubscribedChannels())
                justSynced = true
    }

    val floatingBackdrop = rememberGlassFloatingActionBackdrop()

    DeviceLibraryPullRefresh(artistType == ArtistsType.OnDevice) {
        Box (
            modifier = Modifier
                .background(colorPalette().background0)
                .fillMaxHeight()
                .fillMaxWidth()
        ) {

            Column( Modifier.fillMaxSize() ) {

                val homeArtistsToolbarOrderPrefLibrary by rememberPreference(homeArtistsLibraryToolbarOrderKey, "")
                val homeArtistsToolbarOrderPrefFavorites by rememberPreference(homeArtistsFavoritesToolbarOrderKey, "")
                val homeArtistsToolbarOrderPrefDisliked by rememberPreference(homeArtistsDislikedToolbarOrderKey, "")

                val onDeviceToolbarOrder by rememberPreference(homeArtistsOnDeviceToolbarOrderKey, "")

                val currentToolbarOrderPref = when (artistType) {
                    ArtistsType.OnDevice -> onDeviceToolbarOrder
                    ArtistsType.Favorites -> homeArtistsToolbarOrderPrefFavorites
                    ArtistsType.Disliked -> homeArtistsToolbarOrderPrefDisliked
                    else -> homeArtistsToolbarOrderPrefLibrary
                }

                val toolbarButtons = buildList<Button> {
                    val defaultToolbarOrder = if (artistType == ArtistsType.OnDevice) {
                        HomeArtistsToolbarSettingsDialog.onDeviceButtonIds
                    } else {
                        HomeArtistsToolbarSettingsDialog.allButtonIds
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
                            "position_lock" -> { if (sort.sortBy == ArtistSortBy.Custom) add(positionLock) }
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
                                TabHeader( R.string.library_artists_title ) {
                                    HeaderInfo(items.size.toString(), R.drawable.people)
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
                            currentValue = artistType,
                            onValueUpdate = { artistType = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    AnimatedVisibility(
                        visible = artistType != ArtistsType.OnDevice && isYouTubeSyncEnabled(),
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
                    val artistIds = remember(itemsOnDisplay) { itemsOnDisplay.map { it.id } }
                    val bookmarkStatesMap by remember(artistIds) {
                        BookmarkStateManager.getArtistBookmarkStates(artistIds)
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
                            modifier = Modifier.background( colorPalette().background0 ).fillMaxSize(),
                            contentPadding = rememberPageChromeContentPadding(
                                topInsetProvider = scrollContentInsetProvider,
                                base = PaddingValues(bottom = Dimensions.bottomSpacer),
                                extraTopProvider = { with(headerDensity) { headerHeight.toDp() } },
                            )
                        ) {
                    items(items = itemsOnDisplay.distinctBy { it.id }, key = { it.id }) { artist ->
                        ReorderableItem(
                            reorderableLazyGridState,
                            key = artist.id
                        ) { isDraggingItem ->
                            Box(modifier = Modifier) {
                                if (!positionLock.isLocked() && sort.sortBy == ArtistSortBy.Custom && sort.sortOrder == SortOrder.Ascending) {
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
                                                        currentItems.forEachIndexed { index, artist ->
                                                            artistTable.updatePosition(artist.id, index)
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

                                ArtistItem(
                                    artist = artist,
                                    thumbnailSizeDp = itemSize.size.dp,
                                    thumbnailSizePx = itemSize.size.px,
                                    alternative = true,
                                    bookmarkState = bookmarkStatesMap[artist.id],
                                    modifier = Modifier.clip(uiRoundnessShape()).combinedClickable(
                                        onClick = {
                                            if( itemSelector.isActive ) {
                                                if( artist in itemSelector ) itemSelector.remove( artist )
                                                else itemSelector.add( artist )
                                            } else {
                                                search.hideIfEmpty()
                                                onArtistClick( artist )
                                            }
                                        },
                                        onLongClick = {
                                            menuState.display { LocalArtistItemMenu(artist = artist).MenuComponent() }
                                        }
                                    ),
                                    disableScrollingText = disableScrollingText,
                                    isYoutubeArtist = artist.isYoutubeArtist,
                                    thumbnailOverlay = {
                                        if( itemSelector.isActive ) {
                                            key(itemSelector.size) {
                                                Icon(
                                                    painter = painterResource(if (artist in itemSelector) R.drawable.checked_filled else R.drawable.unchecked_outline),
                                                    contentDescription = null,
                                                    tint = if (artist in itemSelector) colorPalette().accent else colorPalette().text,
                                                    modifier = Modifier
                                                        .padding(4.dp)
                                                        .size(24.dp)
                                                        .align(Alignment.TopStart)
                                                        .zIndex(2f)
                                                )
                                            }
                                        } else if (sort.sortBy == ArtistSortBy.PlayCount) {
                                            val playCount by (if (preview == null) Database.eventTable.getArtistPlayCount(artist.id) else kotlinx.coroutines.flow.flowOf(0)).collectAsStateWithLifecycle(initialValue = 0, context = MousikeDispatchers.DATA)
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(artistThumbnailShape())
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
                                        } else if (sort.sortBy == ArtistSortBy.ListeningTime) {
                                            val playTime by (if (preview == null) Database.eventTable.getArtistTotalPlayTime(artist.id) else kotlinx.coroutines.flow.flowOf(0L)).collectAsStateWithLifecycle(initialValue = 0L, context = MousikeDispatchers.DATA)
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(artistThumbnailShape())
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

                if( showFloatingIcon )
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










