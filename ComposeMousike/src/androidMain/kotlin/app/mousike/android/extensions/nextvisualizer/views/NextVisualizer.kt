package app.mousike.android.extensions.nextvisualizer.views

import app.mousike.android.privacy.PermissionUsageJournal
import app.mousike.android.privacy.PermissionFeature
import app.mousike.android.privacy.JournalPermission
import app.mousike.android.privacy.PermissionEventKind

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import app.mousike.android.LocalPlayerServiceBinder
import app.mousike.android.R
import app.mousike.android.components.player.LocalPlayerCoverOverlay
import app.mousike.android.components.glass.OverlayFade
import app.mousike.android.components.glass.overlayViewportShape
import app.mousike.android.thumbnailShape
import app.mousike.android.typography
import app.mousike.android.ui.components.themed.SecondaryTextButton
import app.mousike.android.utils.hasPermission
import app.mousike.android.utils.isCompositionLaunched
import app.mousike.android.utils.rememberPreference
import app.mousike.android.utils.semiBold
import app.mousike.android.utils.showvisthumbnailKey
import app.mousike.android.utils.blackBackgroundForVisThumbnailKey
import app.mousike.android.utils.visualizerEnabledKey
import app.mousike.android.extensions.nextvisualizer.NextVisualizer

@UnstableApi
@Composable
fun NextVisualizer(
    isDisplayed: Boolean,
    onCover: Boolean? = null,
    fadeHosted: Boolean = false,
) {
    OverlayFade(active = isDisplayed, durationMillis = 500, hosted = fadeHosted) {
        val context = LocalContext.current
        val visualizerEnabled by rememberPreference(visualizerEnabledKey, false)
        val savedVisualizerOnCover by rememberPreference(showvisthumbnailKey, true)
        val showvisthumbnail = onCover ?: (LocalPlayerCoverOverlay.current || savedVisualizerOnCover)
        var blackBackgroundForVisThumbnail by rememberPreference(blackBackgroundForVisThumbnailKey, true)

        val binder = LocalPlayerServiceBinder.current
        var isPlaying by remember(binder) { mutableStateOf(binder?.player?.isPlaying == true) }

        DisposableEffect(binder) {
            val listener = object : Player.Listener {
                override fun onIsPlayingChanged(isPlayingChanged: Boolean) {
                    isPlaying = isPlayingChanged
                }
            }
            binder?.player?.addListener(listener)
            onDispose {
                binder?.player?.removeListener(listener)
            }
        }

        if (visualizerEnabled) {
            val permission = Manifest.permission.RECORD_AUDIO

            var relaunchPermission by remember {
                mutableStateOf(false)
            }

            var hasPermission by remember(isCompositionLaunched()) {
                mutableStateOf(context.applicationContext.hasPermission(permission))
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = {
                    hasPermission = it
                    PermissionUsageJournal.record(context, PermissionFeature.VISUALIZER, JournalPermission.MICROPHONE,
                        if (it) PermissionEventKind.GRANTED else PermissionEventKind.DENIED)
                }
            )

            if (!hasPermission) {
                LaunchedEffect(Unit, relaunchPermission) { PermissionUsageJournal.record(context, PermissionFeature.VISUALIZER, JournalPermission.MICROPHONE, PermissionEventKind.REQUESTED); launcher.launch(permission) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(overlayViewportShape(showvisthumbnail, thumbnailShape()))
                        .background(if (!showvisthumbnail) Color.Transparent else Color.Black.copy(0.8f)),
                    verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BasicText(
                        text = stringResource(R.string.visualizer_microphone_permission_message),
                        modifier = Modifier.fillMaxWidth(0.75f),
                        style = typography().xs.semiBold
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    SecondaryTextButton(
                        text = stringResource(R.string.visualizer_action_open_permission_settings),
                        onClick = {
                            context.startActivity(
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    setData(Uri.fromParts("package", context.packageName, null))
                                }
                            )
                        }
                    )
                }
            } else {
                Box(Modifier.fillMaxSize()) {
                    val backgroundColor by animateColorAsState(
                        targetValue = if (!showvisthumbnail || !blackBackgroundForVisThumbnail || !isPlaying) Color.Transparent else Color.Black.copy(0.6f),
                        animationSpec = tween(500),
                        label = stringResource(R.string.visualizer_debug_background)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(overlayViewportShape(showvisthumbnail, thumbnailShape()))
                    ) {
                        NextVisualizer(sceneBackground = backgroundColor, onCover = showvisthumbnail)
                    }
                }
            }
        }
    }
}



