package app.mousike.android.components.settings.ai

import android.content.Context
import androidx.core.content.edit
import app.mousike.android.enums.LocalRecommandationsNumber
import app.mousike.android.enums.MaxStatisticsItems
import app.mousike.android.enums.MaxTopPlaylistItems
import app.mousike.android.enums.PlayEventsType
import app.mousike.android.enums.RecommendationsNumber
import app.mousike.android.utils.MaxTopPlaylistItemsCustomValueKey
import app.mousike.android.utils.MaxTopPlaylistItemsKey
import app.mousike.android.utils.enableQuickPicksPageKey
import app.mousike.android.utils.maxStatisticsItemsCustomValueKey
import app.mousike.android.utils.maxStatisticsItemsKey
import app.mousike.android.utils.playEventsTypeKey
import app.mousike.android.utils.preferences
import app.mousike.android.utils.putEnum
import app.mousike.android.utils.recommendationsNumberKey
import app.mousike.android.utils.selectedCountryCodeKey
import app.mousike.android.utils.showChartsKey
import app.mousike.android.utils.showMonthlyPlaylistInQuickPicksKey
import app.mousike.android.utils.showMoodsAndGenresKey
import app.mousike.android.utils.showMyTopPlaylistKey
import app.mousike.android.utils.showNewAlbumsArtistsKey
import app.mousike.android.utils.showNewAlbumsKey
import app.mousike.android.utils.showPlaylistMightLikeKey
import app.mousike.android.utils.showRelatedAlbumsKey
import app.mousike.android.utils.showSimilarArtistsKey
import app.mousike.android.utils.showStatsListeningTimeKey
import app.mousike.android.utils.showTipsKey
import app.mousike.android.components.dialog.settings.QuickPicksContentSettingsDialog

/** Restores the original AI defaults once for the active profile. */
internal fun applyAiDefaults(context: Context) {
    QuickPicksContentSettingsDialog.reset(context)
    context.preferences.edit {
        putEnum(playEventsTypeKey, PlayEventsType.MostPlayed)
        putBoolean(showTipsKey, true)
        putBoolean(showRelatedAlbumsKey, true)
        putBoolean(showSimilarArtistsKey, true)
        putBoolean(showNewAlbumsArtistsKey, true)
        putBoolean(showNewAlbumsKey, true)
        putBoolean(showPlaylistMightLikeKey, true)
        putBoolean(showMoodsAndGenresKey, true)
        putBoolean(showMonthlyPlaylistInQuickPicksKey, true)
        putBoolean(showChartsKey, true)
        putBoolean(enableQuickPicksPageKey, true)
        putEnum("LocalRecommandationsNumber", LocalRecommandationsNumber.SixQ)
        putEnum(recommendationsNumberKey, RecommendationsNumber.Adaptive)
        putBoolean(showMyTopPlaylistKey, true)
        putBoolean(showStatsListeningTimeKey, true)
        putEnum(maxStatisticsItemsKey, MaxStatisticsItems.`10`)
        putInt(maxStatisticsItemsCustomValueKey, 10)
        putEnum(MaxTopPlaylistItemsKey, MaxTopPlaylistItems.`10`)
        putInt(MaxTopPlaylistItemsCustomValueKey, 10)
        remove(selectedCountryCodeKey)
    }
}
