package app.mousike.innertube.requests

import app.mousike.innertube.Innertube
import app.mousike.innertube.Innertube.getBestQuality
import app.mousike.innertube.models.MusicTwoRowItemRenderer
import app.mousike.innertube.models.oddElements
import app.mousike.innertube.models.splitBySeparator
import app.mousike.innertube.utils.InnertubeLogger


object NewReleaseAlbumPage {
    fun fromMusicTwoRowItemRenderer(renderer: MusicTwoRowItemRenderer): Innertube.AlbumItem {
        InnertubeLogger.d("NewReleaseAlbumPage", "fromMusicTwoRowItemRenderer: ${renderer.navigationEndpoint?.browseEndpoint}")
        return Innertube.AlbumItem(
            info = Innertube.Info(
                name = renderer.title?.runs?.firstOrNull()?.text ?: "",
                endpoint = renderer.navigationEndpoint?.browseEndpoint
            ),
//            playlistId = renderer.thumbnailOverlay
//                ?.musicItemThumbnailOverlayRenderer?.content
//                ?.musicPlayButtonRenderer?.playNavigationEndpoint
//                ?.watchPlaylistEndpoint?.playlistId ?: return null,
            authors = renderer.subtitle?.runs?.splitBySeparator()?.getOrNull(1)?.oddElements()
                ?.map {
                    Innertube.Info(
                        name = it.text,
                        endpoint = it.navigationEndpoint?.browseEndpoint
                    )
                } ?: emptyList(),
            year = renderer.subtitle?.runs?.lastOrNull()?.text,
            thumbnail = renderer.thumbnailRenderer?.musicThumbnailRenderer?.thumbnail?.thumbnails?.getBestQuality(),
//            explicit = renderer.subtitleBadges?.find {
//                it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
//            } != null
        )
    }
}
