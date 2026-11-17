package app.mousike.android.core.database.ext

import androidx.room.Embedded
import app.mousike.android.models.Album

data class AlbumListeningStat(
    @Embedded val album: Album,
    val playTimeMs: Long,
    val songCount: Long
)
