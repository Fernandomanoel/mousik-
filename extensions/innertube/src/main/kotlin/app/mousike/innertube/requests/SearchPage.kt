package app.mousike.innertube.requests

import io.ktor.client.call.body
import app.mousike.innertube.Innertube
import app.mousike.innertube.models.ContinuationResponse
import app.mousike.innertube.models.MusicResponsiveListItemRenderer
import app.mousike.innertube.models.MusicShelfRenderer
import app.mousike.innertube.models.SearchResponse
import app.mousike.innertube.utils.runCatchingNonCancellable

suspend fun <T : Innertube.Item> Innertube.searchPage(
    query: String,
    params: String? = null,
    fromMusicShelfRendererContent: (MusicShelfRenderer.Content) -> T?
) = runCatchingNonCancellable {
    val response = search(query = query, params = params).body<SearchResponse>()

    response
        .contents
        ?.tabbedSearchResultsRenderer
        ?.tabs
        ?.firstOrNull()
        ?.tabRenderer
        ?.content
        ?.sectionListRenderer
        ?.contents
        ?.lastOrNull()
        ?.musicShelfRenderer
        ?.toItemsPage(fromMusicShelfRendererContent)
}

suspend fun <T : Innertube.Item> Innertube.searchPageContinuation(
    continuation: String,
    fromMusicShelfRendererContent: (MusicShelfRenderer.Content) -> T?
) = runCatchingNonCancellable {
    val response = search(continuation = continuation).body<ContinuationResponse>()

    response
        .continuationContents
        ?.musicShelfContinuation
        ?.toItemsPage(fromMusicShelfRendererContent)
}

private fun <T : Innertube.Item> MusicShelfRenderer?.toItemsPage(mapper: (MusicShelfRenderer.Content) -> T?) =
    Innertube.ItemsPage(
        items = this
            ?.contents
            ?.mapNotNull(mapper),
        continuation = this
            ?.continuations
            ?.firstOrNull()
            ?.nextContinuationData
            ?.continuation
    )

private fun <T : Innertube.Item> MusicResponsiveListItemRenderer?.toItemsPage(mapper: (MusicResponsiveListItemRenderer.FlexColumn) -> T?) =
    Innertube.ItemsPage(
        items = this
            ?.flexColumns
            ?.mapNotNull(mapper),
        continuation = null
    )
