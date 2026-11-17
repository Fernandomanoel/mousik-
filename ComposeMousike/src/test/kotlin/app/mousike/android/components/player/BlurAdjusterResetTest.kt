package app.mousike.android.components.player

import android.app.Application
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import app.mousike.android.ui.styling.Appearance
import app.mousike.android.ui.styling.DefaultDarkColorPalette
import app.mousike.android.ui.styling.LocalAppearance
import app.mousike.android.ui.styling.Typography
import app.mousike.android.utils.blurStrengthKey
import app.mousike.android.utils.playerBackdropKey
import app.mousike.android.utils.preferences
import app.mousike.android.utils.rotatingAlbumCoverKey
import app.mousike.android.utils.setActiveProfile
import app.mousike.android.Dependencies
import app.mousike.android.MainApplication
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = Application::class)
class BlurAdjusterResetTest {
    @get:Rule val compose = createComposeRule()
    private val context get() = ApplicationProvider.getApplicationContext<Application>()

    @Before fun setUp() {
        val application = mockk<MainApplication>()
        every { application.applicationContext } returns context
        Dependencies.init(application)
        setActiveProfile("blur-reset-test", context)
        context.preferences.edit().clear().putFloat(blurStrengthKey, 68f)
            .putFloat(playerBackdropKey, 42f).putBoolean(rotatingAlbumCoverKey, true).commit()
    }

    @After fun tearDown() {
        context.preferences.edit().clear().commit()
        setActiveProfile("default", context)
    }

    @Test fun `rotation reset restores false and preserves the two adjacent slider settings`() {
        compose.setContent {
            CompositionLocalProvider(LocalAppearance provides Appearance(
                DefaultDarkColorPalette, Typography.fallback, CircleShape, CircleShape, CircleShape,
            )) { BlurAdjuster().DialogBody() }
        }
        compose.onNodeWithTag("player.blur.reset-rotation").performClick()
        compose.runOnIdle {
            assertFalse(context.preferences.getBoolean(rotatingAlbumCoverKey, true))
            assertEquals(42f, context.preferences.getFloat(playerBackdropKey, -1f), 0f)
            assertEquals(68f, context.preferences.getFloat(blurStrengthKey, -1f), 0f)
        }
    }
}
