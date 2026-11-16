package app.n_zik.android.ui.screens.searchresult

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.n_zik.android.colorPalette
import app.n_zik.android.core.coil.ImageCacheFactory
import app.n_zik.android.enums.NavRoutes
import app.n_zik.android.typography
import app.n_zik.android.artistThumbnailShape
import app.n_zik.innertube.Innertube
import app.n_zik.innertube.requests.searchPage
import app.n_zik.innertube.utils.from

/**
 * Artists matching the query, shown above the songs (Spotify-style) so a band/artist
 * search offers the artist page to follow or explore, not only its songs.
 */
@Composable
fun SearchArtistsStrip( query: String, navController: NavController ) {
    var artists by remember( query ) { mutableStateOf( emptyList<Innertube.ArtistItem>() ) }
    LaunchedEffect( query ) {
        artists = Innertube.searchPage<Innertube.ArtistItem>(
            query = query,
            params = Innertube.SearchFilter.Artist.value,
            fromMusicShelfRendererContent = Innertube.ArtistItem.Companion::from
        )?.getOrNull()?.items.orEmpty().take( 8 )
    }
    if( artists.isEmpty() ) return

    Column( Modifier.padding( top = 8.dp ) ) {
        BasicText(
            "Artistas",
            Modifier.padding( horizontal = 16.dp, vertical = 4.dp ),
            style = typography().s.copy( color = colorPalette().text )
        )
        LazyRow(
            contentPadding = androidx.compose.foundation.layout.PaddingValues( horizontal = 16.dp ),
            horizontalArrangement = Arrangement.spacedBy( 16.dp )
        ) {
            items( artists, key = { it.key } ) { artist ->
                Column(
                    Modifier.width( 88.dp ).clickable { navController.navigate( "${NavRoutes.artist.name}/${artist.key}" ) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ImageCacheFactory.Thumbnail(
                        shape = artistThumbnailShape(),
                        thumbnailUrl = artist.thumbnail?.url,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size( 72.dp ).clip( artistThumbnailShape() )
                    )
                    BasicText(
                        artist.title.orEmpty(),
                        Modifier.padding( top = 4.dp ),
                        maxLines = 2, overflow = TextOverflow.Ellipsis,
                        style = typography().xs.copy( color = colorPalette().text, textAlign = TextAlign.Center )
                    )
                }
            }
        }
    }
}
