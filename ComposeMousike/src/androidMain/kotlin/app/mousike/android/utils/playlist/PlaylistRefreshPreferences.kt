package app.mousike.android.utils.playlist

import android.content.Context
import app.mousike.android.models.Playlist
import app.mousike.android.utils.preferences
import app.mousike.android.core.database.canRefreshPlaylist
import app.mousike.android.core.database.isReadOnlyPlaylistSource

/** Per-profile source refresh preference, independent of account synchronization. */
class PlaylistRefreshPreferences(context: Context) {
    private val context = context.applicationContext

    fun get(playlist: Playlist): Boolean = canRefreshPlaylist(playlist) &&
        context.preferences.getBoolean(key(playlist.id), isReadOnlyPlaylistSource(playlist))

    fun set(playlistId: Long, enabled: Boolean) {
        context.preferences.edit().putBoolean(key(playlistId), enabled).apply()
    }

    companion object {
        fun key(playlistId: Long): String = "playlist_refresh_on_open_$playlistId"
    }
}
