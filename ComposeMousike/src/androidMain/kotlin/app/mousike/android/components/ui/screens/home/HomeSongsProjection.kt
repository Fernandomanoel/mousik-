package app.mousike.android.components.ui.screens.home

import app.mousike.android.EXPLICIT_PREFIX
import app.mousike.android.models.Song
import app.mousike.android.utils.coroutines.MousikeDispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/** Lists/maps are replaced, never mutated by HomeSongs; searched Song fields are immutable. */
internal data class HomeSongsProjectionInput(
    val items: List<Song>,
    val query: String,
    val recommendationsEnabled: Boolean,
    val recommendations: Map<Song, Int>,
    val parentalControlEnabled: Boolean,
)

internal suspend fun projectHomeSongsOnData(input: HomeSongsProjectionInput): List<Song> =
    withContext(MousikeDispatchers.DATA) {
        val context = currentCoroutineContext()
        val ordered = input.items.toMutableList()
        if (input.recommendationsEnabled) {
            input.recommendations.forEach { (song, position) ->
                context.ensureActive()
                ordered.add(position.coerceIn(0, ordered.size), song)
            }
        }
        val seenIds = HashSet<String>()
        ordered.filter { song ->
            context.ensureActive()
            // Deduplicate before filtering, exactly as the original projection did.
            seenIds.add(song.id) &&
                (!input.parentalControlEnabled || !song.title.startsWith(EXPLICIT_PREFIX, true)) &&
                (song.cleanTitle().contains(input.query, true) ||
                    song.cleanArtistsText().contains(input.query, true))
        }
    }
