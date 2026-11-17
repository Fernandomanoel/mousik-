package app.mousike.android.ui.screens.searchresult

import app.mousike.android.utils.showButtonPlayerVideoDefault
import app.mousike.android.components.settings.appearance.preview.LocalThemePreview
import app.mousike.android.components.settings.appearance.preview.themePreviewSearchPage
import app.mousike.android.components.navigation.rememberPageChromeContentPadding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp

import androidx.compose.ui.draw.clip

import app.mousike.android.uiRoundnessShape

import app.mousike.android.core.database.*

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.mousike.innertube.Innertube
import app.mousike.innertube.requests.searchPage
import app.mousike.innertube.requests.searchPageContinuation
import app.mousike.innertube.utils.from
import app.mousike.android.enums.ContentType
import app.mousike.android.core.database.Database
import app.mousike.android.LocalPlayerServiceBinder
import app.mousike.android.models.Song
import app.mousike.android.ui.components.LocalMenuState
import app.mousike.android.ui.components.SwipeablePlaylistItem
import app.mousike.android.utils.addNext
import app.mousike.android.core.database.artistEntryNames
import app.mousike.android.utils.asMediaItem
import app.mousike.android.utils.asSong
import app.mousike.android.utils.enqueue
import app.mousike.android.utils.forcePlay
import app.mousike.android.utils.isDownloadedSong
import app.mousike.android.utils.manageDownload
import app.mousike.android.utils.playVideo
import app.mousike.android.colorPalette
import app.mousike.android.LocalDownloadStatesMap
import app.mousike.android.enums.DownloadedStateMedia
import app.mousike.android.ui.items.ArtistItemPlaceholder
import app.mousike.android.ui.items.SongItemPlaceholder
import app.mousike.android.ui.items.VideoItem
import app.mousike.android.ui.items.VideoItemPlaceholder
import app.mousike.android.components.SongItem
import app.mousike.android.components.menu.video.VideoItemMenu
import app.mousike.android.components.menu.album.OnlineAlbumItemMenu
import app.mousike.android.components.menu.artist.OnlineArtistItemMenu
import app.mousike.android.components.menu.playlist.OnlinePlaylistItemMenu
import app.mousike.android.core.utils.Toaster
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.runtime.remember
import androidx.media3.common.util.UnstableApi
import app.mousike.android.R
import app.mousike.android.utils.preferences
import app.mousike.android.utils.showButtonPlayerVideoKey
import app.mousike.android.enums.NavRoutes
import app.mousike.android.typography
import app.mousike.android.ui.items.ArtistItem
import app.mousike.android.ui.items.AlbumItem
import app.mousike.android.ui.items.AlbumItemGridPlaceholder
import app.mousike.android.ui.items.PlaylistItem
import app.mousike.android.ui.styling.Dimensions
import app.mousike.android.ui.styling.px
import app.mousike.android.utils.conditional
import app.mousike.innertube.models.MusicShelfRenderer

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun OnlineSearchGrid(
    topContentInsetProvider: () -> Dp,
    query: String,
    tabIndex: Int,
    filterContentType: ContentType,
    navController: NavController,
    disableScrollingText: Boolean,
    headerContent: @Composable (textButton: (@Composable () -> Unit)?) -> Unit,
    emptyItemsText: String
) {
    val thumbnailSizeDp = when (tabIndex) {
        1 -> Dimensions.thumbnails.album + 8.dp
        2 -> 64.dp
        else -> Dimensions.thumbnails.playlist + 8.dp
    }
    val thumbnailSizePx = thumbnailSizeDp.px

    val preview = LocalThemePreview.current != null
    val context = LocalContext.current
    val binder = LocalPlayerServiceBinder.current
    val menuState = LocalMenuState.current
    val hapticFeedback = LocalHapticFeedback.current
    val isVideoEnabled = remember { context.preferences.getBoolean(showButtonPlayerVideoKey, showButtonPlayerVideoDefault) }

    val itemContent: @Composable LazyGridItemScope.(Innertube.Item) -> Unit = { item ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(
                vertical = Dimensions.itemsVerticalPadding,
                horizontal = 0.dp
            )
        ) {
            when (item) {
                is Innertube.SongItem -> {
                    val isDownloaded = !preview && isDownloadedSong(item.asMediaItem.mediaId)
                    SwipeablePlaylistItem(
                        mediaItem = item.asMediaItem,
                        onPlayNext = { binder?.player?.addNext(item.asMediaItem) },
                        onDownload = {
                            binder?.cache?.removeResource(item.asMediaItem.mediaId ?: "")
                            Database.asyncTransaction {
                                Database.formatTable.updateContentLengthOf(item.key ?: "")
                            }
                            manageDownload(context, item.asMediaItem, isDownloaded)
                        },
                        onEnqueue = { binder?.player?.enqueue(item.asMediaItem) }
                    ) {
                        SongItem(
                            song = item.asSong,
                            navController = navController,
                            modifier = Modifier,
                            onClick = {
                                binder?.startRadio(item.asMediaItem, false, item.info?.endpoint)
                            }
                        )
                    }
                }
                is Innertube.VideoItem -> {
                    SwipeablePlaylistItem(
                        mediaItem = item.asMediaItem,
                        onPlayNext = { binder?.player?.addNext(item.asMediaItem) },
                        onDownload = { Toaster.w(R.string.search_video_download_unsupported_message) },
                        onEnqueue = { binder?.player?.enqueue(item.asMediaItem) }
                    ) {
                        VideoItem(
                            video = item,
                            thumbnailWidthDp = 128.dp,
                            thumbnailHeightDp = 72.dp,
                            modifier = Modifier
                                .background(colorPalette().background0)
                                .clip(uiRoundnessShape()).combinedClickable(
                                    onLongClick = {
                                        menuState.display {
                                            VideoItemMenu(
                                                navController = navController,
                                                song = item.asMediaItem.asSong ?: Song.makePlaceholder("")
                                            ).MenuComponent()
                                        }
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    onClick = {
                                        binder?.stopRadio()
                                        if (isVideoEnabled) binder?.player?.playVideo(item.asMediaItem)
                                        else binder?.player?.forcePlay(item.asMediaItem)
                                    }
                                ),
                            disableScrollingText = disableScrollingText
                        )
                    }
                }
                is Innertube.AlbumItem -> {
                    AlbumItem(
                        yearCentered = false,
                        album = item,
                        thumbnailSizePx = thumbnailSizePx,
                        thumbnailSizeDp = thumbnailSizeDp,
                        alternative = true,
                        showInfo = false,
                        modifier = Modifier
                            .clip(uiRoundnessShape()).combinedClickable(
                                onClick = {
                                    navController.navigate("${NavRoutes.album.name}/${item.key}")
                                },
                                onLongClick = {
                                    menuState.display {
                                        OnlineAlbumItemMenu(
                                            navController = navController,
                                            album = item
                                        ).MenuComponent()
                                    }
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            ),
                        disableScrollingText = disableScrollingText
                    )
                }
                is Innertube.PlaylistItem -> {

                    PlaylistItem(
                        playlist = item,
                        thumbnailSizePx = thumbnailSizePx,
                        thumbnailSizeDp = thumbnailSizeDp,
                        showSongsCount = false,
                        alternative = true,
                        showName = false,
                        showInfo = false,
                        isYoutubePlaylist = true,
                        modifier = Modifier.clip(uiRoundnessShape()).combinedClickable(
                            onClick = {
                                when (tabIndex) {
                                    4, 5 -> navController.navigate("${NavRoutes.playlist.name}/${item.key}")
                                    6 -> navController.navigate("${NavRoutes.podcast.name}/${item.key}")
                                }
                            },
                            onLongClick = {
                                menuState.display {
                                    OnlinePlaylistItemMenu(
                                        navController = navController,
                                        playlist = item
                                    ).MenuComponent()
                                }
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        ),
                        disableScrollingText = disableScrollingText
                    )
                }
                is Innertube.ArtistItem -> {

                    ArtistItem(
                        artist = item,
                        thumbnailSizePx = thumbnailSizePx,
                        thumbnailSizeDp = thumbnailSizeDp,
                        alternative = true,
                        showName = false,
                        modifier = Modifier
                            .clip(uiRoundnessShape()).combinedClickable(
                                onClick = {
                                    navController.navigate("${NavRoutes.artist.name}/${item.key}")
                                },
                                onLongClick = {
                                    menuState.display {
                                        OnlineArtistItemMenu(navController = navController, artist = item).MenuComponent()
                                    }
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            ),
                        disableScrollingText = disableScrollingText
                    )
                }
                else -> {}
            }
            if (item !is Innertube.SongItem && item !is Innertube.VideoItem) {
                BasicText(
                    text = (if (item is Innertube.AlbumItem) item.title else if (item is Innertube.PlaylistItem) item.title else if (item is Innertube.ArtistItem) item.title else "") ?: "",
                    style = typography().s.copy(textAlign = TextAlign.Center),
                    maxLines = 2,
                    modifier = Modifier
                        .width((thumbnailSizeDp - 8.dp) * 0.8f)
                        .conditional(!disableScrollingText) { basicMarquee(iterations = Int.MAX_VALUE) }
                )
            }
            if (item is Innertube.AlbumItem) {
                item.authors.artistEntryNames().joinToString(", ").let { authors ->
                    if (authors.isNotBlank()) {
                        BasicText(
                            text = authors,
                            style = typography().xs.copy(color = Color.Gray, textAlign = TextAlign.Center),
                            maxLines = 1,
                            modifier = Modifier
                                .width((thumbnailSizeDp - 8.dp) * 0.8f)
                                .conditional(!disableScrollingText) { basicMarquee(iterations = Int.MAX_VALUE) }
                        )
                    }
                }
                item.year?.let { year ->
                    if (year.isNotBlank()) {
                        BasicText(
                            text = year,
                            style = typography().xs.copy(color = Color.Gray, textAlign = TextAlign.Center),
                            maxLines = 1,
                            modifier = Modifier
                                .width((thumbnailSizeDp - 8.dp) * 0.8f)
                                .conditional(!disableScrollingText) { basicMarquee(iterations = Int.MAX_VALUE) }
                        )
                    }
                }
                item.description?.split(" â€¢ ")?.forEach { segment ->
                    if (segment.isNotBlank()) {
                        BasicText(
                            text = segment,
                            style = typography().xs.copy(color = Color.Gray, textAlign = TextAlign.Center),
                            maxLines = 1,
                            modifier = Modifier
                                .width((thumbnailSizeDp - 8.dp) * 0.8f)
                                .conditional(!disableScrollingText) { basicMarquee(iterations = Int.MAX_VALUE) }
                        )
                    }
                }
            }
            if (item is Innertube.ArtistItem) {
                item.description?.split(" â€¢ ")?.forEach { segment ->
                    if (segment.isNotBlank()) {
                        BasicText(
                            text = segment,
                            style = typography().xs.copy(color = Color.Gray, textAlign = TextAlign.Center),
                            maxLines = 1,
                            modifier = Modifier
                                .width((thumbnailSizeDp - 8.dp) * 0.8f)
                                .conditional(!disableScrollingText) { basicMarquee(iterations = Int.MAX_VALUE) }
                        )
                    }
                }
            }
            if (item is Innertube.PlaylistItem) {
                val channelMetadata = item.channel?.name
                if (channelMetadata?.isNotBlank() == true) {
                    BasicText(
                        text = channelMetadata,
                        style = typography().xs.copy(color = Color.Gray, textAlign = TextAlign.Center),
                        maxLines = 1,
                        modifier = Modifier
                            .width((thumbnailSizeDp - 8.dp) * 0.8f)
                            .conditional(!disableScrollingText) { basicMarquee(iterations = Int.MAX_VALUE) }
                    )
                }
                if (tabIndex != 5) {
                    item.description?.split(" â€¢ ")?.forEach { segment ->
                        if (segment.isNotBlank()) {
                            BasicText(
                                text = segment,
                                style = typography().xs.copy(color = Color.Gray, textAlign = TextAlign.Center),
                                maxLines = 1,
                                modifier = Modifier
                                    .width((thumbnailSizeDp - 8.dp) * 0.8f)
                                    .conditional(!disableScrollingText) { basicMarquee(iterations = Int.MAX_VALUE) }
                            )
                        }
                    }
                }
            }
        }
    }

    val downloadStatesMap = remember { mutableStateMapOf<String, DownloadedStateMedia>() }

    CompositionLocalProvider(LocalDownloadStatesMap provides downloadStatesMap) {
    ItemsGridPage(
        contentPadding = rememberPageChromeContentPadding(
            base = PaddingValues(bottom = Dimensions.bottomSpacer),
            topInsetProvider = topContentInsetProvider,
        ),
        searchQuery = query,
        filterContentType = filterContentType,
        tag = "searchResults/$query/${getTabName(tabIndex)}",
        itemsPageProvider = { continuation ->
            if (preview) {
                Result.success(themePreviewSearchPage(tabIndex, continuation))
            } else if (continuation == null) {
                Innertube.searchPage(
                    query = query,
                    params = getSearchParams(tabIndex),
                    fromMusicShelfRendererContent = getItemFrom(tabIndex)
                )
            } else {
                Innertube.searchPageContinuation(
                    continuation = continuation,
                    fromMusicShelfRendererContent = getItemFrom(tabIndex)
                )
            }
        },
        emptyItemsText = emptyItemsText,
        headerContent = headerContent,
        itemContent = { itemContent(it) },
        itemPlaceholderContent = {
            when (tabIndex) {
                0 -> Column { repeat(8) { SongItemPlaceholder() } }
                2 -> Column { repeat(8) { ArtistItemPlaceholder(thumbnailSizeDp = thumbnailSizeDp) } }
                3 -> VideoItemPlaceholder(thumbnailWidthDp = 128.dp, thumbnailHeightDp = 72.dp)
                else -> AlbumItemGridPlaceholder(thumbnailSizeDp = thumbnailSizeDp)
            }
        },
        thumbnailSizeDp = thumbnailSizeDp
    )
    }
}

private fun getTabName(tabIndex: Int): String = when (tabIndex) {
    0 -> "songs"
    1 -> "albums"
    2 -> "artists"
    3 -> "videos"
    4 -> "playlists"
    5 -> "featured"
    6 -> "podcasts"
    else -> ""
}

private fun getSearchParams(tabIndex: Int): String = when (tabIndex) {
    0 -> Innertube.SearchFilter.Song.value
    1 -> Innertube.SearchFilter.Album.value
    2 -> Innertube.SearchFilter.Artist.value
    3 -> Innertube.SearchFilter.Video.value
    4 -> Innertube.SearchFilter.CommunityPlaylist.value
    5 -> Innertube.SearchFilter.FeaturedPlaylist.value
    6 -> Innertube.SearchFilter.Podcast.value
    else -> ""
}

private fun getItemFrom(tabIndex: Int): (MusicShelfRenderer.Content) -> Innertube.Item? = when (tabIndex) {
    0 -> Innertube.SongItem.Companion::from
    1 -> Innertube.AlbumItem.Companion::from
    2 -> Innertube.ArtistItem.Companion::from
    3 -> Innertube.VideoItem.Companion::from
    else -> Innertube.PlaylistItem.Companion::from
}









