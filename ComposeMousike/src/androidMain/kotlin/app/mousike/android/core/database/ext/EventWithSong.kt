package app.mousike.android.core.database.ext

import androidx.room.Embedded
import androidx.room.Relation
import app.mousike.android.models.Event
import app.mousike.android.models.Song

data class EventWithSong(
    @Embedded val event: Event,
    @Relation(
        entity = Song::class,
        parentColumn = "songId",
        entityColumn = "id"
    )
    val song: Song
)

