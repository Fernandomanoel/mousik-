package app.mousike.android.components.playlist

import androidx.compose.ui.draw.clip

import app.mousike.android.uiRoundnessShape

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import app.mousike.android.enums.MenuStyle
import app.mousike.android.enums.PlaylistSongSortBy
import app.mousike.android.enums.SortOrder
import app.mousike.android.typography
import app.mousike.android.ui.components.LocalMenuState
import app.mousike.android.ui.components.MenuState
import app.mousike.android.utils.rememberPreference
import app.mousike.android.utils.menuStyleKey
import app.mousike.android.utils.localPlaylistSortMenuOrderKey
import app.mousike.android.utils.semiBold
import app.mousike.android.components.Sort

class PlaylistSongsSort private constructor(
    menuState: MenuState,
    sortByState: MutableState<PlaylistSongSortBy>,
    sortOrderState: MutableState<SortOrder>,
    styleState: MutableState<MenuStyle>,
    private val isRewind: Boolean
): Sort<PlaylistSongSortBy>(
    menuState, sortByState, sortOrderState, styleState,
    sortMenuOrderKey = localPlaylistSortMenuOrderKey,
    sortMenuPrefix = "pl"
) {

    companion object {
        @Composable
        operator fun invoke(playlistId: Long, isRewind: Boolean = false) = PlaylistSongsSort(
            LocalMenuState.current,
            rememberPreference("PlaylistSongsSortBy_$playlistId", PlaylistSongSortBy.Title),
            rememberPreference("PlaylistSongsSortOrder_$playlistId", SortOrder.Ascending),
            rememberPreference( menuStyleKey, MenuStyle.List ),
            isRewind
        )
    }

    /** Spec 2: "Rewind Top" exists only in the sort menu of the generated `rewind-*` playlists; every other playlist keeps the classic option list. */
    override fun getEnumConstants(): List<PlaylistSongSortBy> =
        super.getEnumConstants().filter { it != PlaylistSongSortBy.RewindTop || isRewind }

    override fun onLongClick() { /* Does nothing */ }

    @Composable
    override fun ToolBarButton() {
        super.ToolBarButton()

        BasicText(
            text = this.sortBy.text,
            style = typography().s.semiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clip(uiRoundnessShape()).clickable { super.onLongClick() }
        )
    }
}
