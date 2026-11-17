package app.mousike.android.core.database.ext

import androidx.room.Embedded
import app.mousike.android.models.Artist

data class ArtistListeningStat(
    @Embedded val artist: Artist,
    val playTimeMs: Long,
    val songCount: Long
)
