package app.n_zik.android.components.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import app.n_zik.android.LocalPlayerServiceBinder
import app.n_zik.android.colorPalette
import app.n_zik.android.core.database.Database
import app.n_zik.android.core.utils.Toaster
import app.n_zik.android.enums.QueueLoopType
import app.n_zik.android.models.Playlist
import app.n_zik.android.models.Song
import app.n_zik.android.typography
import app.n_zik.android.utils.*
import app.n_zik.android.utils.coroutines.NzikDispatchers
import kotlinx.coroutines.withContext

private val Tabs = listOf( "Músicas", "Artistas", "Álbuns", "Playlists" )

/** Preference: tapping a downloaded song shuffles the rest (true) or plays the list in order (false). */
const val downloadsShuffleOnTapKey = "downloadsShuffleOnTap"

/**
 * Hub for the Downloaded library: songs / artists / albums / playlists tabs plus the
 * usual player controls (play in order, shuffle toggle, repeat). Songs tab = [songsContent].
 */
@UnstableApi
@Composable
fun DownloadsHub(
    getSongs: () -> List<Song>,
    headerPadding: Dp,
    songsContent: @Composable (Dp) -> Unit,
) {
    val binder = LocalPlayerServiceBinder.current
    var tab by rememberSaveable { mutableIntStateOf( 0 ) }
    var shuffleOnTap by rememberPreference( downloadsShuffleOnTapKey, true )
    var repeatMode by remember { mutableIntStateOf( binder?.player?.repeatMode ?: 0 ) }
    val songs = getSongs()

    fun play( list: List<Song>, startAt: Int = 0 ) {
        if( list.isEmpty() ) return
        binder?.stopRadio()
        val items = list.map( Song::asMediaItem )
        if( shuffleOnTap ) {
            val first = items[startAt.coerceIn( items.indices )]
            binder?.player?.forcePlayAtIndex( listOf( first ) + items.filter { it !== first }.shuffled(), 0 )
        } else
            binder?.player?.forcePlayAtIndex( items, startAt )
    }

    Column( Modifier.fillMaxSize().padding( top = headerPadding ) ) {
        // Player controls
        Row(
            Modifier.fillMaxWidth().padding( horizontal = 12.dp, vertical = 6.dp ),
            horizontalArrangement = Arrangement.spacedBy( 8.dp )
        ) {
            Chip( "Tocar em ordem", false ) {
                binder?.stopRadio()
                binder?.player?.forcePlayFromBeginning( songs.map( Song::asMediaItem ) )
            }
            Chip( "Aleatório", shuffleOnTap ) { shuffleOnTap = !shuffleOnTap }
            Chip(
                when( QueueLoopType.from( repeatMode ) ) {
                    QueueLoopType.Default -> "Repetir: não"
                    QueueLoopType.RepeatOne -> "Repetir: 1"
                    QueueLoopType.RepeatAll -> "Repetir: todas"
                },
                repeatMode != QueueLoopType.Default.type
            ) {
                val next = QueueLoopType.from( repeatMode ).next().type
                binder?.player?.repeatMode = next
                repeatMode = next
            }
        }

        // Tabs
        Row( Modifier.fillMaxWidth().padding( horizontal = 12.dp ), horizontalArrangement = Arrangement.spacedBy( 8.dp ) ) {
            Tabs.forEachIndexed { i, name -> Chip( name, tab == i ) { tab = i } }
        }

        Box( Modifier.fillMaxSize().padding( top = 6.dp ) ) {
            when( tab ) {
                0 -> songsContent( 0.dp )
                1 -> GroupedSongs(
                    groups = remember( songs ) { songs.groupBy { it.artistsText?.takeIf( String::isNotBlank ) ?: "Desconhecido" }.toSortedMap() },
                    onPlay = ::play
                )
                2 -> AlbumsTab( songs, ::play )
                else -> PlaylistsTab( songs )
            }
        }
    }
}

@Composable
private fun AlbumsTab( songs: List<Song>, onPlay: (List<Song>, Int) -> Unit ) {
    var groups by remember { mutableStateOf( emptyMap<String, List<Song>>() ) }
    LaunchedEffect( songs ) {
        groups = withContext( NzikDispatchers.DATA ) {
            songs
                .groupBy { song ->
                    Database.songAlbumMapTable.findAlbumsOfDirect( song.id ).firstOrNull()?.title ?: "Sem álbum"
                }
                .toSortedMap()
        }
    }
    GroupedSongs( groups, onPlay )
}

@Composable
private fun GroupedSongs( groups: Map<String, List<Song>>, onPlay: (List<Song>, Int) -> Unit ) {
    val expanded = remember { mutableStateMapOf<String, Boolean>() }
    if( groups.isEmpty() ) {
        BasicText( "Nenhuma música baixada.", Modifier.padding( 16.dp ), style = typography().xs.copy( color = colorPalette().textSecondary ) )
        return
    }
    LazyColumn( Modifier.fillMaxSize() ) {
        groups.forEach { (name, list) ->
            item( key = "g:$name" ) {
                Row(
                    Modifier.fillMaxWidth()
                        .clickable { expanded[name] = expanded[name] != true }
                        .padding( horizontal = 16.dp, vertical = 12.dp ),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BasicText( name, Modifier.weight( 1f ), maxLines = 1, overflow = TextOverflow.Ellipsis, style = typography().s.copy( color = colorPalette().text ) )
                    BasicText( "${list.size}  ▶", Modifier.clickable { onPlay( list, 0 ) }.padding( start = 12.dp ), style = typography().xs.copy( color = colorPalette().accent ) )
                }
            }
            if( expanded[name] == true )
                items( list, key = { "s:$name:${it.id}" } ) { song ->
                    BasicText(
                        song.title,
                        Modifier.fillMaxWidth().clickable { onPlay( list, list.indexOf( song ) ) }.padding( start = 32.dp, end = 16.dp, top = 8.dp, bottom = 8.dp ),
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        style = typography().xs.copy( color = colorPalette().textSecondary )
                    )
                }
        }
    }
}

@Composable
private fun PlaylistsTab( songs: List<Song> ) {
    Column( Modifier.padding( 16.dp ), verticalArrangement = Arrangement.spacedBy( 12.dp ) ) {
        BasicText(
            "Crie uma playlist com as músicas baixadas. Depois você pode editar nome e capa na biblioteca.",
            style = typography().xs.copy( color = colorPalette().textSecondary )
        )
        Chip( "Criar playlist com todas (${songs.size})", true ) {
            if( songs.isEmpty() ) return@Chip
            val ids = songs.map( Song::id )
            Database.asyncTransaction {
                val playlistId = playlistTable.insert( Playlist( name = "Baixadas ${java.text.SimpleDateFormat( "dd/MM HH:mm", java.util.Locale.getDefault() ).format( java.util.Date() )}" ) )
                ids.forEachIndexed { i, id -> songPlaylistMapTable.mapAtPosition( id, playlistId, i ) }
            }
            Toaster.done()
        }
    }
}

@Composable
private fun Chip( text: String, selected: Boolean, onClick: () -> Unit ) {
    BasicText(
        text,
        Modifier
            .clip( CircleShape )
            .background( if( selected ) colorPalette().accent else colorPalette().background2 )
            .clickable( onClick = onClick )
            .padding( horizontal = 14.dp, vertical = 8.dp ),
        maxLines = 1,
        style = typography().xs.copy( color = if( selected ) colorPalette().onAccent else colorPalette().text )
    )
}
