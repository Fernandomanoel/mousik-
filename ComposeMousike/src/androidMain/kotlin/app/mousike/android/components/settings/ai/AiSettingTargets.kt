package app.mousike.android.components.settings.ai

import app.mousike.android.enums.LocalRecommandationsNumber
import app.mousike.android.utils.MaxTopPlaylistItemsCustomValueKey
import app.mousike.android.utils.MaxTopPlaylistItemsKey
import app.mousike.android.utils.enableQuickPicksPageKey
import app.mousike.android.utils.maxStatisticsItemsCustomValueKey
import app.mousike.android.utils.maxStatisticsItemsKey
import app.mousike.android.utils.playEventsTypeKey
import app.mousike.android.utils.recommendationsNumberKey
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

/** Stable local targets for future global search. */
internal object AiSettingTargets {
    val cards = listOf("ai.general", "ai.quick-picks", "ai.rewind", "ai.recommendations", "ai.statistics", "ai.top-playlists", "ai.data", "ai.reset")
    val preferenceKeys = listOf(playEventsTypeKey, showTipsKey, showRelatedAlbumsKey, showSimilarArtistsKey, showNewAlbumsArtistsKey, showNewAlbumsKey, showPlaylistMightLikeKey, showMoodsAndGenresKey, showMonthlyPlaylistInQuickPicksKey, showChartsKey, enableQuickPicksPageKey, "LocalRecommandationsNumber", recommendationsNumberKey, showMyTopPlaylistKey, showStatsListeningTimeKey, maxStatisticsItemsKey, maxStatisticsItemsCustomValueKey, MaxTopPlaylistItemsKey, MaxTopPlaylistItemsCustomValueKey)
    val actions = listOf("ai.quick-picks.content", "ai.rewind.open", "ai.statistics.open", "ai.data.clear-events", "ai.reset.apply")
}
