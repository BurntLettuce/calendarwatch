package com.ledger.calendarwatch

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class EventAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "event_alarms"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Event"
        val note = intent.getStringExtra("note")

        ensureChannel(context)

        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("title", title)
            putExtra("note", note)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, title.hashCode(), fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // A full-screen-intent notification
        // - a plain Activity start from a BroadcastReceiver is blocked.
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(note ?: "It's time.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(title.hashCode(), notification)
    }

    private fun ensureChannel(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Event alarms", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarms for scheduled calendar events"
                enableVibration(true)
            }
            nm.createNotificationChannel(channel)
        }
    }
}
