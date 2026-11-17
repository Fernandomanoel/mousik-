package app.mousike.android.ui.screens.mood

import app.mousike.android.utils.showButtonPlayerVideoDefault
import app.mousike.android.components.ui.screens.CollectionPageStatus
import androidx.compose.foundation.layout.PaddingValues
import app.mousike.android.components.navigation.rememberPageChromeContentPadding
import androidx.compose.ui.draw.clip

import app.mousike.android.uiRoundnessShape

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.mousike.android.R
import app.mousike.android.compose.persist.persist
import app.mousike.innertube.Innertube
import app.mousike.innertube.requests.BrowseResult
import app.mousike.innertube.requests.browseCategory
import kotlinx.coroutines.withContext
import app.mousike.android.utils.coroutines.MousikeDispatchers
import app.mousike.android.colorPalette
import app.mousike.android.enums.NavRoutes
import app.mousike.android.models.Mood
import app.mousike.android.typography
import app.mousike.android.ui.components.themed.HeaderWithIcon
import app.mousike.android.ui.items.AlbumItem
import app.mousike.android.ui.items.ArtistItem
import app.mousike.android.ui.items.PlaylistItem
import app.mousike.android.ui.components.themed.Loader
import app.mousike.android.ui.styling.Dimensions
import app.mousike.android.ui.styling.px
import app.mousike.android.utils.center
import app.mousike.android.utils.disableScrollingTextKey
import app.mousike.android.utils.rememberPreference
import app.mousike.android.utils.secondary
import app.mousike.android.utils.semiBold
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import app.mousike.android.ui.components.LocalMenuState
import app.mousike.android.components.menu.album.OnlineAlbumItemMenu
import app.mousike.android.components.menu.artist.OnlineArtistItemMenu
import app.mousike.android.components.menu.playlist.OnlinePlaylistItemMenu
import app.mousike.android.ui.items.VideoItem
import app.mousike.android.components.SongItem
import app.mousike.android.LocalDownloadStatesMap
import app.mousike.android.ui.components.SwipeablePlaylistItem
import app.mousike.android.core.database.LikeStateManager
import app.mousike.android.core.database.PlaylistStateManager
import app.mousike.android.components.menu.song.SongItemMenu
import app.mousike.android.components.menu.video.VideoItemMenu
import app.mousike.android.core.database.BookmarkStateManager
import app.mousike.android.core.database.Database
import app.mousike.android.LocalPlayerServiceBinder
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import app.mousike.android.utils.preferences
import app.mousike.android.models.Song
import app.mousike.android.core.utils.Toaster
import app.mousike.android.utils.showButtonPlayerVideoKey

import app.mousike.android.utils.addNext
import app.mousike.android.utils.asMediaItem
import app.mousike.android.utils.asSong
import app.mousike.android.utils.enqueue
import app.mousike.android.utils.forcePlay
import app.mousike.android.utils.isDownloadedSong
import app.mousike.android.utils.manageDownload
import app.mousike.android.utils.playVideo
import app.mousike.android.enums.DownloadedStateMedia
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.mousike.android.download.utils.MyDownloadHelper
import androidx.media3.exoplayer.offline.Download
import app.mousike.android.enums.PlaylistSwipeAction
import app.mousike.android.utils.playlistSwipeLeftActionKey
import app.mousike.android.utils.playlistSwipeRightActionKey

internal const val defaultBrowseId = "FEmusic_moods_and_genres_category"

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun MoodList(
    navController: NavController,
    mood: Mood
) {

    val browseId = mood.browseId ?: defaultBrowseId
    var moodPage by persist<Result<BrowseResult>>("moods/$browseId${mood.params?.let { "/$it" } ?: ""}")

    LaunchedEffect(Unit) {
        moodPage = withContext(MousikeDispatchers.DATA) {
            Innertube.browseCategory(browseId = browseId, params = mood.params)
        }
    }

    val thumbnailSizeDp = Dimensions.thumbnails.album
    val thumbnailSizePx = thumbnailSizeDp.px

    val lazyListState = rememberLazyListState()


    val sectionTextModifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(top = 24.dp, bottom = 8.dp)

    val disableScrollingText by rememberPreference(disableScrollingTextKey, false)

    val menuState = LocalMenuState.current
    val hapticFeedback = LocalHapticFeedback.current
    val context = LocalContext.current
    val binder = LocalPlayerServiceBinder.current
    val isVideoEnabled = remember { context.preferences.getBoolean(showButtonPlayerVideoKey, showButtonPlayerVideoDefault) }

    Column (
        modifier = Modifier
            .background(colorPalette().background0)
            //.fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        moodPage?.getOrNull()?.let { moodResult ->
            val moodSongIds = remember(moodResult) {
                moodResult.items.flatMap { section ->
                    section.items.filterIsInstance<Innertube.SongItem>().mapNotNull { it.key }
                }.distinct()
            }
            val likeStatesMap by remember(moodSongIds) {
                LikeStateManager.getLikeStates(moodSongIds)
            }.collectAsStateWithLifecycle(emptyMap(), context = MousikeDispatchers.DATA)
            val playlistStatesMap by remember(moodSongIds) {
                PlaylistStateManager.getPlaylistStates(moodSongIds)
            }.collectAsStateWithLifecycle(emptyMap(), context = MousikeDispatchers.DATA)

            val moodSongs = remember(moodResult) {
                moodResult.items.flatMap { section ->
                    section.items.filterIsInstance<Innertube.SongItem>()
                }
            }
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
                    moodSongs.associate { song ->
                        (song.key ?: "") to if ((song.key ?: "") in downloadedIds) DownloadedStateMedia.DOWNLOADED else DownloadedStateMedia.NOT_CACHED_OR_DOWNLOADED
                    }
                }
            }

            // Hoisted swipe action preferences
            val playlistSwipeLeftAction by rememberPreference(playlistSwipeLeftActionKey, PlaylistSwipeAction.Favourite)
            val playlistSwipeRightAction by rememberPreference(playlistSwipeRightActionKey, PlaylistSwipeAction.PlayNext)

            CompositionLocalProvider(LocalDownloadStatesMap provides downloadStatesMap) {
            LazyColumn(
                contentPadding = rememberPageChromeContentPadding(PaddingValues(bottom = Dimensions.bottomSpacer)),
                state = lazyListState,

                modifier = Modifier
                    .background(colorPalette().background0)
                    .fillMaxSize()
            ) {
                item(
                    key = "header",
                    contentType = 0
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        HeaderWithIcon(
                            title = mood.name,
                            iconId = R.drawable.globe,
                            enabled = true,
                            showIcon = true,
                            modifier = Modifier,
                            onClick = {}
                        )
                    }
                }

                moodResult.items.forEach { item ->
                    item {
                        BasicText(
                            text = item.title,
                            style = typography().m.semiBold,
                            modifier = sectionTextModifier
                        )
                    }
                    item {
                        val sectionAlbumKeys = remember(item) { item.items.filterIsInstance<Innertube.AlbumItem>().mapNotNull { it.key } }
                        val sectionAlbumBookmarkStatesMap by remember(sectionAlbumKeys) {
                            BookmarkStateManager.getAlbumBookmarkStates(sectionAlbumKeys)
                        }.collectAsStateWithLifecycle(emptyMap(), context = MousikeDispatchers.DATA)
                        val sectionArtistKeys = remember(item) { item.items.filterIsInstance<Innertube.ArtistItem>().mapNotNull { it.key } }
                        val sectionArtistBookmarkStatesMap by remember(sectionArtistKeys) {
                            BookmarkStateManager.getArtistBookmarkStates(sectionArtistKeys)
                        }.collectAsStateWithLifecycle(emptyMap(), context = MousikeDispatchers.DATA)
                        val sectionPlaylistKeys = remember(item) { item.items.filterIsInstance<Innertube.PlaylistItem>().mapNotNull { it.key } }
                        val sectionPlaylistBookmarkStatesMap by remember(sectionPlaylistKeys) {
                            BookmarkStateManager.getPlaylistBookmarkStates(sectionPlaylistKeys)
                        }.collectAsStateWithLifecycle(emptyMap(), context = MousikeDispatchers.DATA)

                        LazyRow {
                            items(items = item.items, key = { it.key }) { childItem ->
                                if (childItem.key == defaultBrowseId) return@items
                                when (childItem) {
                                    is Innertube.AlbumItem -> AlbumItem(
                                        album = childItem,
                                        thumbnailSizePx = thumbnailSizePx,
                                        thumbnailSizeDp = thumbnailSizeDp,
                                        alternative = true,
                                        bookmarkState = sectionAlbumBookmarkStatesMap[childItem.key],
                                        modifier = Modifier.clip(uiRoundnessShape()).combinedClickable(
                                            onClick = {
                                                childItem.info?.endpoint?.browseId?.let {
                                                    navController.navigate(route = "${NavRoutes.album.name}/$it")
                                                }
                                            },
                                            onLongClick = {
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.display {
                                                    OnlineAlbumItemMenu(
                                                        navController = navController,
                                                        album = childItem
                                                    ).MenuComponent()
                                                }
                                            }
                                        ),
                                        disableScrollingText = disableScrollingText
                                    )

                                    is Innertube.ArtistItem -> ArtistItem(
                                        artist = childItem,
                                        thumbnailSizePx = thumbnailSizePx,
                                        thumbnailSizeDp = thumbnailSizeDp,
                                        alternative = true,
                                        bookmarkState = sectionArtistBookmarkStatesMap[childItem.key],
                                        modifier = Modifier.clip(uiRoundnessShape()).combinedClickable(
                                            onClick = {
                                                childItem.info?.endpoint?.browseId?.let {
                                                    navController.navigate(route = "${NavRoutes.artist.name}/$it")
                                                }
                                            },
                                            onLongClick = {
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.display {
                                                    OnlineArtistItemMenu(navController = navController, artist = childItem).MenuComponent()
                                                }
                                            }
                                        ),
                                        disableScrollingText = disableScrollingText
                                    )

                                    is Innertube.PlaylistItem -> PlaylistItem(
                                        playlist = childItem,
                                        thumbnailSizePx = thumbnailSizePx,
                                        thumbnailSizeDp = thumbnailSizeDp,
                                        alternative = true,
                                        isBookmarked = sectionPlaylistBookmarkStatesMap[childItem.key],
                                        modifier = Modifier.clip(uiRoundnessShape()).combinedClickable(
                                            onClick = {
                                                childItem.info?.endpoint?.let { endpoint ->
                                                    navController.navigate(route = "${NavRoutes.playlist.name}/${endpoint.browseId}")
                                                }
                                            },
                                            onLongClick = {
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.display {
                                                    OnlinePlaylistItemMenu(
                                                        navController = navController,
                                                        playlist = childItem
                                                    ).MenuComponent()
                                                }
                                            }
                                        ),
                                        disableScrollingText = disableScrollingText
                                    )

                                    is Innertube.SongItem -> {
                                        val isDownloaded = isDownloadedSong(childItem.asMediaItem.mediaId)
                                        SwipeablePlaylistItem(
                                            mediaItem = childItem.asMediaItem,
                                            onPlayNext = { binder?.player?.addNext(childItem.asMediaItem) },
                                            onDownload = {
                                                binder?.cache?.removeResource(childItem.asMediaItem.mediaId ?: "")
                                                Database.asyncTransaction {
                                                    Database.formatTable.updateContentLengthOf(childItem.key ?: "")
                                                }
                                                manageDownload(context, childItem.asMediaItem, isDownloaded)
                                            },
                                            onEnqueue = { binder?.player?.enqueue(childItem.asMediaItem) },
                                            downloadStateParam = downloadsMapState[childItem.key]?.state ?: Download.STATE_STOPPED,
                                            downloadedStateMediaParam = downloadStatesMap[childItem.key] ?: DownloadedStateMedia.NOT_CACHED_OR_DOWNLOADED,
                                            swipeLeftActionParam = playlistSwipeLeftAction,
                                            swipeRightActionParam = playlistSwipeRightAction
                                        ) {
                                            SongItem(
                                                song = childItem.asMediaItem.asSong ?: Song.makePlaceholder(""),
                                                isLiked = likeStatesMap[childItem.key],
                                                inPlaylist = playlistStatesMap[childItem.key],
                                                navController = navController,
                                                modifier = Modifier.clip(uiRoundnessShape()).combinedClickable(
                                                    onClick = {
                                                        binder?.stopRadio()
                                                        if (isVideoEnabled) binder?.player?.playVideo(childItem.asMediaItem)
                                                        else binder?.player?.forcePlay(childItem.asMediaItem)
                                                    },
                                                    onLongClick = {
                                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        menuState.display {
                                                            SongItemMenu(
                                                                navController = navController,
                                                                song = childItem.asMediaItem.asSong ?: Song.makePlaceholder("")
                                                            ).MenuComponent()
                                                        }
                                                    }
                                                ),
                                                onClick = {
                                                    binder?.stopRadio()
                                                    if (isVideoEnabled) binder?.player?.playVideo(childItem.asMediaItem)
                                                    else binder?.player?.forcePlay(childItem.asMediaItem)
                                                }
                                            )
                                        }
                                    }

                                    is Innertube.VideoItem -> {
                                        SwipeablePlaylistItem(
                                            mediaItem = childItem.asMediaItem,
                                            onPlayNext = { binder?.player?.addNext(childItem.asMediaItem) },
                                            onDownload = { Toaster.w(R.string.search_video_download_unsupported_message) },
                                            onEnqueue = { binder?.player?.enqueue(childItem.asMediaItem) },
                                            downloadStateParam = downloadsMapState[childItem.key]?.state ?: Download.STATE_STOPPED,
                                            downloadedStateMediaParam = downloadStatesMap[childItem.key] ?: DownloadedStateMedia.NOT_CACHED_OR_DOWNLOADED,
                                            swipeLeftActionParam = playlistSwipeLeftAction,
                                            swipeRightActionParam = playlistSwipeRightAction
                                        ) {
                                            VideoItem(
                                                video = childItem,
                                                thumbnailWidthDp = thumbnailSizeDp,
                                                thumbnailHeightDp = thumbnailSizeDp,
                                                modifier = Modifier
                                                    .background(colorPalette().background0)
                                                    .clip(uiRoundnessShape()).combinedClickable(
                                                        onClick = {
                                                            binder?.stopRadio()
                                                            if (isVideoEnabled) binder?.player?.playVideo(childItem.asMediaItem)
                                                            else binder?.player?.forcePlay(childItem.asMediaItem)
                                                        },
                                                        onLongClick = {
                                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            menuState.display {
                                                                VideoItemMenu(
                                                                    navController = navController,
                                                                    song = childItem.asMediaItem.asSong ?: Song.makePlaceholder("")
                                                                ).MenuComponent()
                                                            }
                                                        }
                                                    ),
                                                disableScrollingText = disableScrollingText
                                            )
                                        }
                                    }

                                    else -> {}
                                }
                            }
                        }
                    }
                }

            }
            }
        } ?: CollectionPageStatus {
            if (moodPage?.exceptionOrNull() != null) {
                BasicText(
                    text = stringResource(R.string.recommendations_load_failed_message),
                    style = typography().s.secondary.center,
                    modifier = Modifier.padding(all = 16.dp)
                )
            } else {
                Loader(modifier = Modifier.fillMaxSize())
            }
        }
    }
}








