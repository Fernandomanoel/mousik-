package app.mousike.android.components.dialog.album

import android.app.Application
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.mousike.android.MODIFIED_PREFIX
import app.mousike.android.models.Album
import app.mousike.android.models.Song
import app.mousike.android.models.SongAlbumMap
import app.mousike.android.core.utils.Toaster
import app.mousike.android.appContext
import app.mousike.android.core.coil.ImageCacheFactory
import app.mousike.android.core.database.DatabaseInitializer
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = Application::class)
class ChangeAlbumCoverDialogConfirmationTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var db: DatabaseInitializer

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        db = Room.inMemoryDatabaseBuilder(context, DatabaseInitializer::class.java)
            .allowMainThreadQueries()
            .build()
        mockkStatic("app.mousike.android.GlobalVarsKt")
        every { appContext() } returns context
        mockkObject(DatabaseInitializer.Companion, ImageCacheFactory, Toaster)
        every { DatabaseInitializer.Instance } returns db
        every { ImageCacheFactory.clearCacheForKey(any(), any(), any()) } just Runs
        every { Toaster.done() } just Runs
    }

    @After
    fun cleanup() {
        unmockkObject(DatabaseInitializer.Companion, ImageCacheFactory, Toaster)
        unmockkStatic("app.mousike.android.GlobalVarsKt")
        db.close()
    }

    @Test
    fun `album details confirmation changes the album cover and preserves track artwork`() {
        val album = Album("album-1", thumbnailUrl = "https://example.com/album.jpg")
        val trackCover = "${MODIFIED_PREFIX}https://example.com/custom-track.jpg"
        val track = Song.makePlaceholder("song-1").copy(thumbnailUrl = trackCover)
        val replacement = "https://example.com/replacement.jpg"
        db.albumTable.insertIgnore(album)
        db.songTable.upsert(track)
        db.songAlbumMapTable.upsert(listOf(SongAlbumMap(track.id, album.id, position = 0)))

        lateinit var dialog: ChangeAlbumCoverDialog
        compose.setContent {
            dialog = ChangeAlbumCoverDialog.forAlbumDetails { album }
        }
        compose.runOnIdle {
            dialog.showDialog()
            dialog.onSet(replacement)
        }
        compose.waitUntil(timeoutMillis = 5_000) { !dialog.isActive }

        assertEquals("$MODIFIED_PREFIX$replacement", db.albumTable.findByIdDirect(album.id)?.thumbnailUrl)
        assertEquals(trackCover, db.songTable.findByIdDirect(track.id)?.thumbnailUrl)
    }
}
