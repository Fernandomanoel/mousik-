package app.mousike.android.core.database.ext

import androidx.room.Embedded
import app.mousike.android.models.Format
import app.mousike.android.models.Song

data class FormatWithSong(
    @Embedded val format: Format,
    @Embedded val song: Song
)

