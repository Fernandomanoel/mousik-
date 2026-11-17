package app.mousike.innertube.requests

import app.mousike.innertube.Innertube

data class LibraryContinuationPage(
    val items: List<Innertube.Item>,
    val continuation: String?,
)