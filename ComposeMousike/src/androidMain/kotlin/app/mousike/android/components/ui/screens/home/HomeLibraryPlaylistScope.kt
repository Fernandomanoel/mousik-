package app.mousike.android.components.ui.screens.home

import app.mousike.android.PINNED_PREFIX
import app.mousike.android.enums.PlaylistsType
import app.mousike.android.models.PlaylistPreview
import app.mousike.android.models.Song
import app.mousike.android.core.database.Database
import app.mousike.android.core.database.isYouTubePlaylistSource
import app.mousike.android.core.rewind.RewindPlaylists
import kotlinx.coroutines.flow.first

internal data class HomeLibraryPlaylistScope(
    val type: PlaylistsType,
    val youtubeFilter: YouTubeLibraryFilter,
    val query: String,
    val showRewind: Boolean,
    val showPinned: Boolean,
    val monthlyEnabled: Boolean,
    val yearlyEnabled: Boolean,
    val rewindFilter: RewindPlaylists.Filter,
) {
    fun visible(candidates: List<PlaylistPreview>): List<PlaylistPreview> = candidates.filter { preview ->
        val playlist = preview.playlist
        playlist.name.contains(query, ignoreCase = true) && when (type) {
            PlaylistsType.YTPlaylist -> matchesYouTubeLibraryFilter(playlist, youtubeFilter)
            PlaylistsType.Playlist, PlaylistsType.OnDevice ->
                (type != PlaylistsType.OnDevice || !isYouTubePlaylistSource(playlist.browseId, playlist.isYoutubePlaylist)) &&
                (!RewindPlaylists.isRewind(playlist.name) || showRewind) &&
                (!playlist.name.startsWith(PINNED_PREFIX, true) || showPinned)
            PlaylistsType.MonthlyPlaylist -> RewindPlaylists.isShown(
                playlist.name, monthlyEnabled, yearlyEnabled, rewindFilter,
            )
            PlaylistsType.PinnedPlaylist -> playlist.name.startsWith(PINNED_PREFIX, true)
        }
    }
}

/** Hidden selections never broaden a bulk action or fall back to all visible rows. */
internal fun homeLibraryActionTargets(
    visible: List<PlaylistPreview>,
    selected: List<PlaylistPreview>,
): List<PlaylistPreview> {
    if (selected.isEmpty()) return visible
    val selectedIds = selected.mapTo(HashSet()) { it.playlist.id }
    return visible.filter { it.playlist.id in selectedIds }
}

/** Playback and export resolve the same scoped playlist IDs and deduplicate shared songs. */
internal suspend fun loadHomeLibraryActionSongs(
    targets: List<PlaylistPreview>,
    loadSongs: suspend (Long) -> List<Song> = { Database.songPlaylistMapTable.allSongsOf(it).first() },
): List<Song> {
    val seen = HashSet<String>()
    return buildList {
        targets.forEach { preview ->
            loadSongs(preview.playlist.id).forEach { song -> if (seen.add(song.id)) add(song) }
        }
    }
}
