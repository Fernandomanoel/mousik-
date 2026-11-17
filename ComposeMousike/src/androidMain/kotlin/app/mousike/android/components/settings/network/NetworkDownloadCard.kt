package app.mousike.android.components.settings.network

import app.mousike.android.components.settings.common.settingsSearchTarget
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.mousike.android.R
import app.mousike.android.utils.autoDownloadSongKey
import app.mousike.android.utils.autoDownloadSongWhenAlbumBookmarkedKey
import app.mousike.android.utils.autoDownloadSongWhenLikedKey
import app.mousike.android.utils.rememberPreference
import app.mousike.android.components.settings.common.SettingsSectionCard
import app.mousike.android.components.settings.common.OtherSwitchSettingEntry
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
internal fun NetworkDownloadCard(query: String) {
    var autoDownloadSong by rememberPreference(autoDownloadSongKey, false)
    var autoDownloadSongWhenLiked by rememberPreference(autoDownloadSongWhenLikedKey, false)
    var autoDownloadSongWhenAlbumBookmarked by rememberPreference(autoDownloadSongWhenAlbumBookmarkedKey, false)
    val context = androidx.compose.ui.platform.LocalContext.current
    var backupFolder by rememberPreference(downloadsBackupFolderKey, "")
    val folderPicker = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        } catch (_: Exception) {}
        backupFolder = uri.toString()
    }

    SettingsSectionCard(
        title = stringResource(R.string.downloads_title),
        icon = R.drawable.download,
        content = {
            // Copy of finished downloads outside the app (device Downloads folder or SD card)
            app.mousike.android.components.settings.common.OtherSettingsEntry(
                title = "Pasta de cópia dos downloads",
                text = "Copia cada música baixada para uma pasta do aparelho ou do cartão SD. Se o app for desinstalado, as músicas continuam salvas.",
                selectedValueText = if (backupFolder.isBlank()) "Desativado" else android.net.Uri.parse(backupFolder).lastPathSegment.orEmpty(),
                icon = R.drawable.download,
                onClick = { folderPicker.launch(null) }
            )
            if (backupFolder.isNotBlank())
                app.mousike.android.components.settings.common.OtherSettingsEntry(
                    title = "Parar de copiar downloads",
                    text = "As músicas já copiadas continuam na pasta.",
                    selectedValueText = "",
                    icon = R.drawable.trash,
                    onClick = { backupFolder = "" }
                )
            if (query.isBlank() || stringResource(R.string.settings_enable_autodownload_song).contains(query, true) || stringResource(R.string.settings_downloads_automatic_description).contains(query, true)) {
                OtherSwitchSettingEntry(
                    modifier = Modifier.settingsSearchTarget("network.download.settings_enable_autodownload_song"),
                    title = stringResource(R.string.settings_enable_autodownload_song),
                    text = stringResource(R.string.settings_downloads_automatic_description),
                    isChecked = autoDownloadSong,
                    onCheckedChange = {
                        autoDownloadSong = it
                    },
                    icon = R.drawable.download
                )
            }

            AnimatedVisibility(
                visible = autoDownloadSong,
                enter = fadeIn(animationSpec = tween(400)) + scaleIn(
                    animationSpec = tween(400),
                    initialScale = 0.9f
                ),
                exit = fadeOut(animationSpec = tween(200)) + scaleOut(
                    animationSpec = tween(200),
                    targetScale = 0.9f
                )
            ) {
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    if (query.isBlank() || stringResource(R.string.settings_enable_autodownload_song_when_liked).contains(query, true) || stringResource(R.string.settings_downloads_on_like_description).contains(query, true)) {
                        OtherSwitchSettingEntry(
                            modifier = Modifier.settingsSearchTarget("network.download.settings_enable_autodownload_song_when_liked"),
                            title = stringResource(R.string.settings_enable_autodownload_song_when_liked),
                            text = stringResource(R.string.settings_downloads_on_like_description),
                            isChecked = autoDownloadSongWhenLiked,
                            onCheckedChange = {
                                autoDownloadSongWhenLiked = it
                            },
                            icon = R.drawable.heart
                        )
                    }

                    if (query.isBlank() || stringResource(R.string.settings_enable_autodownload_song_when_album_bookmarked).contains(query, true) || stringResource(R.string.settings_downloads_on_album_bookmark_description).contains(query, true)) {
                        OtherSwitchSettingEntry(
                            modifier = Modifier.settingsSearchTarget("network.download.settings_enable_autodownload_song_when_album_bookmarked"),
                            title = stringResource(R.string.settings_enable_autodownload_song_when_album_bookmarked),
                            text = stringResource(R.string.settings_downloads_on_album_bookmark_description),
                            isChecked = autoDownloadSongWhenAlbumBookmarked,
                            onCheckedChange = {
                                autoDownloadSongWhenAlbumBookmarked = it
                            },
                            icon = R.drawable.bookmark
                        )
                    }
                }
            }
        }
    )

}
