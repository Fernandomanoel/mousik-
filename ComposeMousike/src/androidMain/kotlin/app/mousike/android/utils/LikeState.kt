package app.mousike.android.utils

import app.mousike.android.components.settings.appearance.preview.LocalThemePreview
import app.mousike.android.core.database.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.mousike.android.core.database.Database
import app.mousike.android.utils.coroutines.MousikeDispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber

@Composable
fun getLikeState(mediaId: String): Int {
    if (LocalThemePreview.current != null) return getUnlikedIcon()
    val songLikeState by remember( mediaId ) {
        Database.songTable
                .likeState( mediaId )
                .distinctUntilChanged()
    }.collectAsStateWithLifecycle(null, context = MousikeDispatchers.DATA)

    return when( songLikeState ) {
        false -> getDislikedIcon()
        null -> getUnlikedIcon()
        true -> getLikedIcon()
    }
}

fun setLikeState(likedAt: Long?): Long? {
    val current =
     when (likedAt) {
        -1L -> null
        null -> System.currentTimeMillis()
        else -> -1L
    }
    //Timber.d("mediaItem setLikeState: $current")
    return current

}

fun setDisLikeState(likedAt: Long?): Long? {
    return when (likedAt) {
        -1L -> null        // dislike -> neutral
        null -> -1L        // neutral -> dislike
        else -> -1L        // like -> dislike
    }
}




