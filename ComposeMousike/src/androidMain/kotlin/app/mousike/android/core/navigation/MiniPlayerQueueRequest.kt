package app.mousike.android.core.navigation

import androidx.compose.runtime.staticCompositionLocalOf

/** Opens the page queue without adding a transient destination that changes navigation insets. */
val LocalMiniPlayerQueueRequest = staticCompositionLocalOf<(() -> Unit)?> { null }
