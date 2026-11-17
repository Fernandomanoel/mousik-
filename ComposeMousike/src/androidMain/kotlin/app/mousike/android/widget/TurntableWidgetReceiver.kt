package app.mousike.android.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import app.mousike.android.playback.services.PlayerServiceModern
import app.mousike.android.utils.coroutines.MousikeDispatchers
import kotlinx.coroutines.launch

class TurntableWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pendingResult = goAsync()
        MousikeDispatchers.fireAndForget(MousikeDispatchers.UI).launch {
            try {
                MousikeWidgetManager.updateIdleWidgets(context)
                if (PlayerServiceModern.isRunning) {
                    val intent = Intent(context, PlayerServiceModern::class.java).apply {
                        action = ACTION_UPDATE_TURNTABLE_WIDGET
                    }
                    try {
                        context.startService(intent)
                    } catch (_: Exception) {
                    }
                }
            } catch (_: Exception) {
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle,
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        val pendingResult = goAsync()
        MousikeDispatchers.fireAndForget(MousikeDispatchers.UI).launch {
            try {
                MousikeWidgetManager.updateIdleWidgets(context)
                if (PlayerServiceModern.isRunning) {
                    val intent = Intent(context, PlayerServiceModern::class.java).apply {
                        action = ACTION_UPDATE_TURNTABLE_WIDGET
                    }
                    try {
                        context.startService(intent)
                    } catch (_: Exception) {
                    }
                }
            } catch (_: Exception) {
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_TURNTABLE_PLAY_PAUSE -> {
                if (PlayerServiceModern.isRunning) {
                    PlayerServiceModern.Action.pause.pendingIntent.send()
                } else {
                    PlayerServiceModern.Action.play.pendingIntent.send()
                }
            }
            ACTION_TURNTABLE_NEXT -> {
                if (PlayerServiceModern.isRunning) {
                    PlayerServiceModern.Action.next.pendingIntent.send()
                }
            }
            ACTION_TURNTABLE_PREVIOUS -> {
                if (PlayerServiceModern.isRunning) {
                    PlayerServiceModern.Action.previous.pendingIntent.send()
                }
            }
        }
    }

    companion object {
        const val ACTION_TURNTABLE_PLAY_PAUSE = "app.mousike.android.widget.TURNTABLE_PLAY_PAUSE"
        const val ACTION_TURNTABLE_NEXT = "app.mousike.android.widget.TURNTABLE_NEXT"
        const val ACTION_TURNTABLE_PREVIOUS = "app.mousike.android.widget.TURNTABLE_PREVIOUS"
        const val ACTION_UPDATE_TURNTABLE_WIDGET = "app.mousike.android.widget.UPDATE_TURNTABLE_WIDGET"
    }
}
