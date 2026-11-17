package app.mousike.android.components.settings.appearance

import android.content.SharedPreferences
import app.mousike.android.utils.karaokeRespectAgentPositionKey
import app.mousike.android.utils.landscapeControlsKey
import app.mousike.android.utils.lyricsAlignmentKey
import app.mousike.android.utils.lyricsBackgroundKey
import app.mousike.android.utils.lyricsHighlightKey
import app.mousike.android.utils.lyricsIntervalIndicatorKey
import app.mousike.android.utils.lyricsOutlineKey
import app.mousike.android.utils.lyricsSizeAnimateKey
import app.mousike.android.utils.lyricsSizeKey
import app.mousike.android.utils.lyricsSizeLKey
import app.mousike.android.utils.lyricsTypeKey
import app.mousike.android.utils.otherLanguageAppLyricsKey
import app.mousike.android.utils.romanizationEnabledKey
import app.mousike.android.utils.showSecondLineKey
import app.mousike.android.components.player.lyrics.LyricsProvider
import app.mousike.android.components.player.lyrics.karaokeClipRectsDebugKey
import app.mousike.android.enums.lyrics.LyricsType

/** Restore menu-only controls through their first-run readers without touching saved lyrics. */
internal fun SharedPreferences.Editor.resetLyricsMenuDefaults() {
    listOf(
        landscapeControlsKey, lyricsAlignmentKey, lyricsOutlineKey, romanizationEnabledKey,
        lyricsIntervalIndicatorKey, showSecondLineKey, lyricsSizeAnimateKey, lyricsHighlightKey,
        lyricsBackgroundKey, lyricsTypeKey, karaokeRespectAgentPositionKey,
        otherLanguageAppLyricsKey, karaokeClipRectsDebugKey, lyricsSizeKey, lyricsSizeLKey,
    ).forEach { key -> remove(key) }
    LyricsType.entries.forEach { mode -> remove(LyricsProvider.preferenceKey(mode)) }
}
