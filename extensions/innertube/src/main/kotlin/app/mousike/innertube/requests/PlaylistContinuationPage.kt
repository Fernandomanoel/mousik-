package app.mousike.innertube.requests

import app.mousike.innertube.Innertube


data class PlaylistContinuationPage(
    val songs: List<Innertube.SongItem>,
    val continuation: String?,
)
