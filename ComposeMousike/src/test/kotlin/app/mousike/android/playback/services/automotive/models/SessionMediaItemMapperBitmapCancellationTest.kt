package app.mousike.android.playback.services.automotive.models

import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import app.mousike.android.appContext
import app.mousike.android.utils.coroutines.MousikeDispatchers

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SessionMediaItemMapperBitmapCancellationTest {
    @After fun tearDown() = unmockkAll()

    @Test fun `cancelling after decode recycles owned bitmap and allows a fresh retry`() {
        // JUnit exposes a synchronous entry point for this coroutine cancellation scenario.
        runBlocking {
            mockkStatic("app.mousike.android.GlobalVarsKt")
            every { appContext() } returns ApplicationProvider.getApplicationContext<Application>()
            val source = File.createTempFile("mousike-cancel-owned-artwork", ".png")
            val firstBitmap = Bitmap.createBitmap(32, 24, Bitmap.Config.ARGB_8888)
            val decoded = CompletableDeferred<Unit>()
            val releaseDecode = CountDownLatch(1)
            val decodeCalls = AtomicInteger()
            mockkObject(ArtworkBitmapDecoder)
            every { ArtworkBitmapDecoder.file(source) } answers {
                if (decodeCalls.incrementAndGet() == 1) {
                    decoded.complete(Unit)
                    // Model a decoder that finishes despite interruption, as native codecs can.
                    var released = false
                    while (!released) {
                        try {
                            releaseDecode.await()
                            released = true
                        } catch (_: InterruptedException) {
                            // Return the owned bitmap after cancellation to exercise prompt return cleanup.
                        }
                    }
                    firstBitmap
                } else Bitmap.createBitmap(32, 24, Bitmap.Config.ARGB_8888)
            }
            val url = source.toURI().toASCIIString()
            val owner = launch(MousikeDispatchers.DATA) {
                SessionMediaItemMapper.mapArtistWithArtwork("artist", "cancel-owned", "Artist", url)
            }
            try {
                withTimeout(5_000) { decoded.await() }
                owner.cancel()
                releaseDecode.countDown()
                withTimeout(5_000) { owner.join() }
                assertTrue(owner.isCancelled)
                assertTrue("Cancelled decode must release its owned pixels", firstBitmap.isRecycled)

                val retried = withTimeout(5_000) {
                    SessionMediaItemMapper.mapArtistWithArtwork("artist", "retry-owned", "Artist", url)
                }
                assertEquals("Cancellation must not cache an incomplete result", 2, decodeCalls.get())
                val bytes = requireNotNull(retried.mediaMetadata.artworkData)
                val embedded = requireNotNull(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
                try {
                    assertEquals(24, embedded.width)
                    assertEquals(24, embedded.height)
                } finally {
                    embedded.recycle()
                }
            } finally {
                releaseDecode.countDown()
                owner.cancelAndJoin()
                if (!firstBitmap.isRecycled) firstBitmap.recycle()
                source.delete()
            }
        }
    }
}
