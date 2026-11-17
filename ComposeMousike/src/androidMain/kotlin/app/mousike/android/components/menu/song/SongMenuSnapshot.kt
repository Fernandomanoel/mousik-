package app.mousike.android.components.menu.song

import androidx.compose.runtime.Composable
import app.mousike.android.models.Song
import app.mousike.android.components.menu.EntityMenuSnapshot
import app.mousike.android.components.menu.ObserveEntityMenuSnapshot
import app.mousike.android.core.database.Database

/** Keeps retained menu callbacks and the header on the same committed song. */
internal class SongMenuSnapshot(entry: Song, val profile: String) {
    internal val entity = EntityMenuSnapshot(entry, profile, Song::id)
    val songId get() = entity.id
    val song get() = entity.value

    fun accept(row: Song?, activeProfile: String) = entity.accept(row, activeProfile)
}

/** A profile switch closes the old menu instead of attaching it to another database. */
@Composable
internal fun ObserveSongMenuSnapshot(snapshot: SongMenuSnapshot, onProfileChanged: () -> Unit) {
    ObserveEntityMenuSnapshot(snapshot.entity, { Database.songTable.findById(it) }, onProfileChanged)
}
