package app.mousike.innertube.requests

import io.ktor.client.call.body
import app.mousike.innertube.Innertube
import app.mousike.innertube.models.GetQueueResponse
import app.mousike.innertube.utils.runCatchingNonCancellable

suspend fun Innertube.queue(videoIds: List<String>, playlistId: String? = null) = runCatchingNonCancellable {
    val response = getQueue(videoIds = videoIds, playlistId = playlistId).body<GetQueueResponse>()

    response
        .queueDatas
        ?.mapNotNull { queueData ->
            queueData
                .content
                ?.playlistPanelVideoRenderer
                ?.let(Innertube.SongItem::parse)
        }
}

suspend fun Innertube.song(videoId: String): Result<Innertube.SongItem?>? =
    queue(videoIds = listOf(videoId))?.map { it?.firstOrNull() }
