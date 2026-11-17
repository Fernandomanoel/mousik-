package app.mousike.android.core.database.ext

import androidx.room.Embedded
import app.mousike.android.models.PlaylistPreview

data class PlaylistListeningStat(
    @Embedded val playlist: PlaylistPreview,
    val playTimeMs: Long
)
