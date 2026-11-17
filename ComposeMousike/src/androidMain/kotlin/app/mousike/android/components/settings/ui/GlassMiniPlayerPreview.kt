package app.mousike.android.components.settings.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import app.mousike.android.cleanPrefix
import app.mousike.android.ui.components.themed.IconButton
import app.mousike.android.ui.styling.Dimensions
import app.mousike.android.utils.miniPlayerButtonOrderKey
import app.mousike.android.utils.playerControlsColorsKey
import app.mousike.android.utils.rememberPreference
import app.mousike.android.utils.semiBold
import app.mousike.android.utils.showMiniPlayerAddToPlaylistKey
import app.mousike.android.utils.showMiniPlayerAudioOutputKey
import app.mousike.android.utils.showMiniPlayerDiscoverKey
import app.mousike.android.utils.showMiniPlayerDownloadKey
import app.mousike.android.utils.showMiniPlayerLikeKey
import app.mousike.android.utils.showMiniPlayerLyricsKey
import app.mousike.android.utils.showMiniPlayerPlayPauseKey
import app.mousike.android.utils.showMiniPlayerQueueKey
import app.mousike.android.utils.showMiniPlayerRadioKey
import app.mousike.android.utils.showMiniPlayerRepeatKey
import app.mousike.android.utils.showMiniPlayerShareKey
import app.mousike.android.utils.showMiniPlayerShuffleKey
import app.mousike.android.utils.showMiniPlayerSkipBackKey
import app.mousike.android.utils.showMiniPlayerSkipForwardKey
import app.mousike.android.utils.showMiniPlayerSleepTimerKey
import app.mousike.android.utils.showMiniPlayerVideoKey
import app.mousike.android.utils.showMiniPlayerVisualizerKey
import app.mousike.android.LocalPlayerServiceBinder
import app.mousike.android.R
import app.mousike.android.colorPalette
import app.mousike.android.components.glass.GlassSettings
import app.mousike.android.components.glass.GlassArea
import app.mousike.android.components.glass.LocalGlassArtworkColor
import app.mousike.android.components.glass.LocalGlassBackdrop
import app.mousike.android.components.glass.backdrop.backdrops.rememberLayerBackdrop
import app.mousike.android.components.glass.glassMiniPlayerBackground
import app.mousike.android.components.glass.usesGlassMaterial
import app.mousike.android.components.player.miniPlayerButtonSpacing
import app.mousike.android.components.player.GlassMiniPlayerIconButton
import app.mousike.android.components.player.GlassMiniPlayerPlayPauseButton
import app.mousike.android.components.player.monochromeControlsColor
import app.mousike.android.core.coil.ImageCacheFactory
import app.mousike.android.enums.MiniPlayerButton
import app.mousike.android.enums.PlayerControlsColors
import app.mousike.android.components.glass.glassImageRim
import app.mousike.android.thumbnailShape
import app.mousike.android.typography
import app.mousike.android.uiRoundnessShape
import org.json.JSONArray
import org.json.JSONException
import timber.log.Timber

private data class PreviewButton(val id: String, val button: MiniPlayerButton, val preference: String)

// Keep the same order and defaults as MiniPlayer and its button settings dialog.
private val previewButtons = listOf(
    PreviewButton("skip_back", MiniPlayerButton.SkipBack, showMiniPlayerSkipBackKey),
    PreviewButton("play_pause", MiniPlayerButton.PlayPause, showMiniPlayerPlayPauseKey),
    PreviewButton("skip_forward", MiniPlayerButton.SkipForward, showMiniPlayerSkipForwardKey),
    PreviewButton("shuffle", MiniPlayerButton.Shuffle, showMiniPlayerShuffleKey),
    PreviewButton("repeat", MiniPlayerButton.Repeat, showMiniPlayerRepeatKey),
    PreviewButton("queue", MiniPlayerButton.Queue, showMiniPlayerQueueKey),
    PreviewButton("audio_output", MiniPlayerButton.AudioOutput, showMiniPlayerAudioOutputKey),
    PreviewButton("sleep_timer", MiniPlayerButton.SleepTimer, showMiniPlayerSleepTimerKey),
    PreviewButton("like", MiniPlayerButton.Like, showMiniPlayerLikeKey),
    PreviewButton("add_to_playlist", MiniPlayerButton.AddToPlaylist, showMiniPlayerAddToPlaylistKey),
    PreviewButton("download", MiniPlayerButton.Download, showMiniPlayerDownloadKey),
    PreviewButton("share", MiniPlayerButton.Share, showMiniPlayerShareKey),
    PreviewButton("radio", MiniPlayerButton.Radio, showMiniPlayerRadioKey),
    PreviewButton("discover", MiniPlayerButton.Discover, showMiniPlayerDiscoverKey),
    PreviewButton("lyrics", MiniPlayerButton.Lyrics, showMiniPlayerLyricsKey),
    PreviewButton("visualizer", MiniPlayerButton.Visualizer, showMiniPlayerVisualizerKey),
    PreviewButton("video", MiniPlayerButton.Video, showMiniPlayerVideoKey),
)
private val defaultPreviewButtons = setOf(
    MiniPlayerButton.SkipBack, MiniPlayerButton.PlayPause,
    MiniPlayerButton.SkipForward, MiniPlayerButton.AudioOutput,
)

private fun previewButtonOrder(serialized: String): List<PreviewButton> {
    if (serialized.isBlank()) return previewButtons
    return try {
        val array = JSONArray(serialized)
        val ids = List(array.length()) { array.getString(it) }
        val ordered = ids.mapNotNull { id -> previewButtons.find { it.id == id } }
        ordered + previewButtons.filter { it !in ordered }
    } catch (error: JSONException) {
        Timber.tag("GlassMiniPlayerPreview").w(error, "Invalid mini-player button order")
        previewButtons
    }
}

@Composable
private fun rememberPreviewMetadata(player: Player?): MediaMetadata {
    var metadata by remember(player) { mutableStateOf(player?.mediaMetadata ?: MediaMetadata.EMPTY) }
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                metadata = player.mediaMetadata
            }
        }
        player?.addListener(listener)
        metadata = player?.mediaMetadata ?: MediaMetadata.EMPTY
        onDispose { player?.removeListener(listener) }
    }
    return metadata
}

@Composable
private fun rememberPreviewPlayer(): Player? {
    val binder = LocalPlayerServiceBinder.current
    val generation by binder?.playerUpdateTrigger?.collectAsStateWithLifecycle(0)
        ?: remember { mutableStateOf(0) }
    return remember(binder, generation) { binder?.player }
}

/** Renders Mousike controls without invoking playback, navigation or settings mutations. */
@Composable
internal fun GlassMiniPlayerPreview(
    config: GlassSettings,
    modifier: Modifier = Modifier,
    player: Player? = rememberPreviewPlayer(),
) {
    val serialized by rememberPreference(miniPlayerButtonOrderKey, "")
    val order = remember(serialized) { previewButtonOrder(serialized) }
    val enabled = previewButtons.associate { definition ->
        val visible by rememberPreference(definition.preference, definition.button in defaultPreviewButtons)
        definition.button to visible
    }
    val buttons = order.filter { enabled[it.button] == true }.map { it.button }
    val metadata = rememberPreviewMetadata(player)
    val title = cleanPrefix(metadata.title?.toString().orEmpty())
        .ifBlank { stringResource(R.string.metadata_unknown_title) }
    val artist = cleanPrefix(metadata.artist?.toString().orEmpty())
        .ifBlank { stringResource(R.string.metadata_unknown_artist) }
    var playing by rememberSaveable { mutableStateOf(false) }
    val settings = config
    val colors = colorPalette()
    val controlsColors by rememberPreference(playerControlsColorsKey, PlayerControlsColors.Monochrome)
    val controlsColor = when (controlsColors) {
        PlayerControlsColors.Monochrome -> monochromeControlsColor(colors)
        PlayerControlsColors.Cover -> LocalGlassArtworkColor.current ?: colors.accent
        else -> colors.accent
    }
    val backdrop = LocalGlassBackdrop.current
    val stageBackdrop = rememberPreviewStageBackdrop(backdrop)
    val material = rememberLayerBackdrop()
    val exportedMaterial = material.takeIf { settings.usesGlassMaterial(GlassArea.MiniPlayer) }
    val controlsBackdrop = exportedMaterial ?: backdrop
    val shape = uiRoundnessShape()
    val height = Dimensions.miniPlayerHeight

    BoxWithConstraints(modifier.fillMaxWidth().testTag("settings.glass.miniPlayer")) {
        // Scale the native row together so a narrow preview still shows the song and all configured controls.
        val nativeWidth = maxOf(maxWidth, (164 + buttons.size * 58).dp)
        val scale = (maxWidth / nativeWidth).coerceAtMost(1f)
        Box(Modifier.fillMaxWidth().height(height * scale), contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier.requiredSize(nativeWidth, height)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .clip(shape)
                    .glassMiniPlayerBackground(settings, shape, stageBackdrop, colors.background2,
                        exportedBackdrop = exportedMaterial)
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Export only the row material; its own input remains the preview stage.
                CompositionLocalProvider(LocalGlassBackdrop provides controlsBackdrop) {
                    Box(Modifier.size(48.dp).clip(thumbnailShape())) {
                        if (metadata.artworkUri != null) {
                            ImageCacheFactory.Thumbnail(
                                thumbnailUrl = metadata.artworkUri.toString(),
                                contentDescription = title,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            Image(painterResource(R.drawable.ic_launcher_box), title,
                                Modifier.fillMaxSize().glassImageRim(thumbnailShape()))
                        }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                        BasicText(title, style = typography().xxs.semiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        BasicText(artist, style = typography().xxs.semiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    val buttonSpacing = miniPlayerButtonSpacing(settings.usesGlassMaterial(GlassArea.MiniPlayer))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(buttonSpacing),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        buttons.forEach { button ->
                            when (button) {
                                MiniPlayerButton.PlayPause -> GlassMiniPlayerPlayPauseButton(
                                    onClick = { playing = !playing },
                                    backgroundColor = colors.background2,
                                    contentDescription = stringResource(if (playing) R.string.player_notification_pause else R.string.player_action_play),
                                    settings = settings,
                                    backdrop = controlsBackdrop,
                                ) {
                                    Image(
                                        painterResource(if (playing) R.drawable.pause else R.drawable.play),
                                        contentDescription = null,
                                        colorFilter = ColorFilter.tint(controlsColor),
                                        modifier = Modifier.align(Alignment.Center).size(24.dp),
                                    )
                                }
                                MiniPlayerButton.SkipBack, MiniPlayerButton.SkipForward, MiniPlayerButton.AudioOutput ->
                                    GlassMiniPlayerIconButton(
                                        icon = button.iconRes,
                                        color = controlsColor,
                                        onClick = {},
                                        contentDescription = stringResource(button.labelRes),
                                        settings = settings,
                                        backdrop = controlsBackdrop,
                                    )
                                else -> {
                                    val description = stringResource(button.labelRes)
                                    IconButton(
                                        icon = button.iconRes,
                                        color = controlsColor,
                                        onClick = {},
                                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 8.dp)
                                            .size(24.dp).semantics { contentDescription = description },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
