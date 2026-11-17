package app.mousike.innertube.requests

import io.ktor.client.call.body
import app.mousike.innertube.Innertube
import app.mousike.innertube.models.ContinuationResponse
import app.mousike.innertube.models.NextResponse
import app.mousike.innertube.utils.runCatchingNonCancellable


suspend fun Innertube.nextPage(videoId: String?, playlistId: String? = null, playlistSetVideoId: String? = null, index: Int? = null, params: String? = null): Result<Innertube.NextPage>? =
    runCatchingNonCancellable {
        val response = next(
            videoId = videoId,
            playlistId = playlistId,
            playlistSetVideoId = playlistSetVideoId,
            index = index,
            params = params,
        ).body<NextResponse>()

        val tabs = response
            .contents
            ?.singleColumnMusicWatchNextResultsRenderer
            ?.tabbedRenderer
            ?.watchNextTabbedResultsRenderer
            ?.tabs

        val playlistPanelRenderer = tabs
            ?.getOrNull(0)
            ?.tabRenderer
            ?.content
            ?.musicQueueRenderer
            ?.content
            ?.playlistPanelRenderer

        if (playlistId == null) {
            val endpoint = playlistPanelRenderer
                ?.contents
                ?.lastOrNull()
                ?.automixPreviewVideoRenderer
                ?.content
                ?.automixPlaylistVideoRenderer
                ?.navigationEndpoint
                ?.watchPlaylistEndpoint

            if (endpoint != null) {
                return nextPage(
                    videoId = videoId,
                    playlistId = endpoint.playlistId,
                    params = endpoint.params,
                )
            }
        }

        Innertube.NextPage(
            playlistId = playlistId,
            playlistSetVideoId = playlistSetVideoId,
            params = params,
            itemsPage = playlistPanelRenderer
                ?.toSongsPage()
        )
    }

suspend fun Innertube.nextPageContinuation(continuation: String) = runCatchingNonCancellable {
    val response = next(
        videoId = null,
        continuation = continuation,
    ).body<ContinuationResponse>()

    response
        .continuationContents
        ?.playlistPanelContinuation
        ?.toSongsPage()
}

private fun NextResponse.MusicQueueRenderer.Content.PlaylistPanelRenderer?.toSongsPage() =
    Innertube.ItemsPage(
        items = this
            ?.contents
            ?.mapNotNull(NextResponse.MusicQueueRenderer.Content.PlaylistPanelRenderer.Content::playlistPanelVideoRenderer)
            ?.map(Innertube.SongItem::parse),
        continuation = this
            ?.continuations
            ?.firstOrNull()
            ?.nextContinuationData
            ?.continuation
    )
