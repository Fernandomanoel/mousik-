package app.n_zik.android.components.theme

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import app.n_zik.android.ui.styling.LocalAppearance
import app.n_zik.android.utils.coroutines.NzikDispatchers
import app.n_zik.android.utils.rememberPreference
import kotlinx.coroutines.withContext

/** Absolute path of the user's background photo (copied into app storage). Empty = off. */
const val appBackgroundPhotoKey = "appBackgroundPhoto"

private fun decodeScaled(path: String): ImageBitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(path, bounds)
    var sample = 1
    while (bounds.outWidth / (sample * 2) >= 1080) sample *= 2
    return BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = sample })?.asImageBitmap()
}

/**
 * Draws the user's photo under the app and makes the palette backgrounds translucent so
 * every screen shows it. With no photo set it is a no-op.
 */
@Composable
fun AppPhotoBackground(content: @Composable () -> Unit) {
    val path by rememberPreference(appBackgroundPhotoKey, "")
    val photo by produceState<ImageBitmap?>(null, path) {
        value = if (path.isBlank()) null else withContext(NzikDispatchers.DATA) { runCatching { decodeScaled(path) }.getOrNull() }
    }
    val bitmap = photo
    if (bitmap == null) { content(); return }

    val appearance = LocalAppearance.current
    val p = appearance.colorPalette
    Box(Modifier.fillMaxSize()) {
        Image(bitmap, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        CompositionLocalProvider(
            LocalAppearance provides appearance.copy(
                colorPalette = p.copy(
                    background0 = p.background0.copy(alpha = 0.70f),
                    background1 = p.background1.copy(alpha = 0.80f),
                    background2 = p.background2.copy(alpha = 0.85f),
                )
            ),
            content = content
        )
    }
}
