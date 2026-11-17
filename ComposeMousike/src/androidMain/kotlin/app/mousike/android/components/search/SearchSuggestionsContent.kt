package app.mousike.android.components.search

import app.mousike.innertube.Innertube

/** A suggestions heading is meaningful only when at least one row can be rendered. */
fun Innertube.SearchSuggestions.hasContent(): Boolean =
    queries.isNotEmpty() || recommendedSong != null || recommendedAlbum != null ||
        recommendedArtist != null || recommendedPlaylist != null || recommendedVideo != null
