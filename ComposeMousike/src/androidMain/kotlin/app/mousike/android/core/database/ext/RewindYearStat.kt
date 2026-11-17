package app.mousike.android.core.database.ext

/** Play totals for one calendar year (local timezone), as returned by [app.mousike.android.core.database.RewindEventSource.rewindYearTotals]. */
data class RewindYearStat(
    val year: Int,
    val plays: Int,
    val playTimeMs: Long
)
