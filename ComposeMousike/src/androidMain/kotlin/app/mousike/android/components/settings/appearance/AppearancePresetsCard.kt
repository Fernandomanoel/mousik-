package app.mousike.android.components.settings.appearance

import app.mousike.android.components.settings.common.settingsSearchTarget
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import app.mousike.android.R
import app.mousike.android.enums.AnimatedGradient
import app.mousike.android.enums.PlayerBackgroundColors
import app.mousike.android.enums.PlayerControlsType
import app.mousike.android.enums.PlayerInfoType
import app.mousike.android.enums.PlayerType
import app.mousike.android.enums.QueueType
import app.mousike.android.enums.ThumbnailType
import app.mousike.android.utils.playerBackgroundColorsKey
import app.mousike.android.utils.playerControlsTypeKey
import app.mousike.android.utils.playerInfoShowIconsKey
import app.mousike.android.utils.playerInfoTypeKey
import app.mousike.android.utils.playerTypeKey
import app.mousike.android.utils.queueTypeKey
import app.mousike.android.utils.rememberPreference
import app.mousike.android.utils.thumbnailTypeKey
import app.mousike.android.components.tab.Search
import app.mousike.android.components.settings.common.SettingsSectionCard
import app.mousike.android.components.settings.common.SettingsDescription
import app.mousike.android.components.settings.common.OtherSwitchSettingEntry
import app.mousike.android.components.settings.common.OtherEnumValueSelectorSettingsEntry
import androidx.compose.ui.platform.testTag
import app.mousike.android.components.settings.common.settingsCardEnter
import app.mousike.android.components.settings.common.settingsCardExit

@OptIn(ExperimentalAnimationApi::class)
@UnstableApi
@Composable
internal fun AppearancePresetsCard(search: Search) {
    var playerBackgroundColors by rememberPreference(
        playerBackgroundColorsKey,
        PlayerBackgroundColors.AnimatedGradient
    )
    var playerControlsType by rememberPreference(playerControlsTypeKey, PlayerControlsType.Essential)
    var playerInfoType by rememberPreference(playerInfoTypeKey, PlayerInfoType.Modern)
    var thumbnailType by rememberPreference(thumbnailTypeKey, ThumbnailType.Modern)
    var playerType by rememberPreference(playerTypeKey, PlayerType.Essential)
    var queueType by rememberPreference(queueTypeKey, QueueType.Modern)
    var playerInfoShowIcons by rememberPreference(playerInfoShowIconsKey, true)
    val searchCtx_0_1 = search.inputValue.isBlank() || stringResource(R.string.player_presets_and_layout).contains(search.inputValue, true) || stringResource(R.string.player_type).contains(search.inputValue, true) || stringResource(R.string.settings_player_queue_style_label).contains(search.inputValue, true) || stringResource(R.string.settings_player_artwork_style_label).contains(search.inputValue, true) || stringResource(R.string.settings_player_header_style_label).contains(search.inputValue, true) || stringResource(R.string.settings_player_header_album_artist_description).contains(search.inputValue, true) || stringResource(R.string.settings_player_header_icons_visible_label).contains(search.inputValue, true) || stringResource(R.string.settings_player_controls_style_label).contains(search.inputValue, true)
    AnimatedVisibility(
                visible = searchCtx_0_1,
                enter = settingsCardEnter,
                exit = settingsCardExit
            ) {
                SettingsSectionCard(
                    modifier = Modifier.testTag("appearance.card.presets"),
                    title = stringResource(R.string.player_presets_and_layout),
                    icon = R.drawable.images_sharp,
                    content = {

            if (search.inputValue.isBlank() || stringResource(R.string.player_type).contains(
                    search.inputValue,
                    true
                )
            )
                if (search.inputValue.isBlank() || stringResource(R.string.player_type).contains(search.inputValue, true)) {
                    OtherEnumValueSelectorSettingsEntry(
                        text = stringResource(R.string.settings_description_with_value, stringResource(R.string.player_layout_player_style_description), playerType.text),
                        modifier = Modifier.settingsSearchTarget("appearance.player_presets_and_layout.playertype"),
                        icon = R.drawable.play,
                        title = stringResource(R.string.player_type),
                        selectedValue = playerType,
                        onValueSelected = {
                            playerType = it
                        },
                        valueText = { it.text },
                    )
                }

            if (search.inputValue.isBlank() || stringResource(R.string.settings_player_queue_style_label).contains(
                    search.inputValue,
                    true
                )
            )
                if (search.inputValue.isBlank() || stringResource(R.string.settings_player_queue_style_label).contains(search.inputValue, true)) {
                    OtherEnumValueSelectorSettingsEntry(
                        text = stringResource(R.string.settings_description_with_value, stringResource(R.string.player_layout_queue_style_description), queueType.text),
                        modifier = Modifier.settingsSearchTarget("appearance.player_presets_and_layout.queuetype"),
                        icon = R.drawable.sort_grid,
                        title = stringResource(R.string.settings_player_queue_style_label),
                        selectedValue = queueType,
                        onValueSelected = {
                            queueType = it
                        },
                        valueText = { it.text },
                    )
                }

            if (search.inputValue.isBlank() || stringResource(R.string.settings_player_artwork_style_label).contains(
                    search.inputValue,
                    true
                )
            )
                if (search.inputValue.isBlank() || stringResource(R.string.settings_player_artwork_style_label).contains(search.inputValue, true)) {
                    OtherEnumValueSelectorSettingsEntry(
                        text = stringResource(R.string.settings_description_with_value, stringResource(R.string.player_layout_artwork_style_description), thumbnailType.text),
                        icon = R.drawable.cd,
                        title = stringResource(R.string.settings_player_artwork_style_label),
                        selectedValue = thumbnailType,
                        onValueSelected = {
                            thumbnailType = it
                        },
                        valueText = { it.text },
                        modifier = (Modifier.padding(start = if (playerBackgroundColors == PlayerBackgroundColors.BlurredCoverColor) 25.dp else 0.dp)).settingsSearchTarget("appearance.player_presets_and_layout.thumbnailtype")
                    )
                }

            if (search.inputValue.isBlank() || stringResource(R.string.settings_player_header_style_label).contains(
                    search.inputValue,
                    true
                )
            ) {
                if (search.inputValue.isBlank() || stringResource(R.string.settings_player_header_style_label).contains(search.inputValue, true)) {
                    OtherEnumValueSelectorSettingsEntry(
                        text = stringResource(R.string.settings_description_with_value, stringResource(R.string.player_layout_information_style_description), playerInfoType.text),
                        modifier = Modifier.settingsSearchTarget("appearance.player_presets_and_layout.pinfo_type"),
                        icon = R.drawable.information,
                        title = stringResource(R.string.settings_player_header_style_label),
                        selectedValue = playerInfoType,
                        onValueSelected = {
                            playerInfoType = it
                        },
                        valueText = { it.text },
                    )
                }
                SettingsDescription(text = stringResource(R.string.settings_player_header_album_artist_description))

                AnimatedVisibility( visible = playerInfoType == PlayerInfoType.Modern) {
                    Column {
                        if (search.inputValue.isBlank() || stringResource(R.string.settings_player_header_icons_visible_label).contains(
                                search.inputValue,
                                true
                            )
                        )
                            if (search.inputValue.isBlank() || stringResource(R.string.settings_player_header_icons_visible_label).contains(search.inputValue, true)) {
                                OtherSwitchSettingEntry(
                        icon = R.drawable.information,
                                    title = stringResource(R.string.settings_player_header_icons_visible_label),
                                    text = stringResource(R.string.player_layout_information_icons_description),
                                    isChecked = playerInfoShowIcons,
                                    onCheckedChange = { playerInfoShowIcons = it },
                                    modifier = (Modifier
                                        .padding(start = 25.dp)).settingsSearchTarget("appearance.player_presets_and_layout.pinfo_show_icons")
                                )
                            }
                    }
                }

            }

            if (search.inputValue.isBlank() || stringResource(R.string.settings_player_controls_style_label).contains(
                    search.inputValue,
                    true
                )
            )
                if (search.inputValue.isBlank() || stringResource(R.string.settings_player_controls_style_label).contains(search.inputValue, true)) {
                    OtherEnumValueSelectorSettingsEntry(
                        text = stringResource(R.string.settings_description_with_value, stringResource(R.string.player_layout_controls_style_description), playerControlsType.text),
                        modifier = Modifier.settingsSearchTarget("appearance.player_presets_and_layout.pcontrols_type"),
                        icon = R.drawable.burger,
                        title = stringResource(R.string.settings_player_controls_style_label),
                        selectedValue = playerControlsType,
                        onValueSelected = {
                            playerControlsType = it
                        },
                        valueText = { it.text }
                    )
                }




            }
        )
            }
}
