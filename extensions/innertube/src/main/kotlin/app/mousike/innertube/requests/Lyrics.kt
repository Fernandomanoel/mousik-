package app.mousike.innertube.requests

import io.ktor.client.call.body
import app.mousike.innertube.Innertube
import app.mousike.innertube.models.BrowseResponse
import app.mousike.innertube.models.NextResponse
import app.mousike.innertube.utils.runCatchingNonCancellable

suspend fun Innertube.lyrics(videoId: String): Result<String?>? = runCatchingNonCancellable {
    val nextResponse = next(videoId = videoId).body<NextResponse>()

    val browseId = nextResponse
        .contents
        ?.singleColumnMusicWatchNextResultsRenderer
        ?.tabbedRenderer
        ?.watchNextTabbedResultsRenderer
        ?.tabs
        ?.getOrNull(1)
        ?.tabRenderer
        ?.endpoint
        ?.browseEndpoint
        ?.browseId
        ?: return@runCatchingNonCancellable null

    val response = browse(browseId = browseId).body<BrowseResponse>()

    response.contents
        ?.sectionListRenderer
        ?.contents
        ?.firstOrNull()
        ?.musicDescriptionShelfRenderer
        ?.description
        ?.text
}
