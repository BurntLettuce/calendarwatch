package com.ledger.calendarwatch

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import java.time.LocalDate

object AlarmSyncCoordinator {

    private const val REMINDER_LEAD_MILLIS = 30 * 60 * 1000L

    suspend fun syncTodayAndTomorrow(context: Context) {
        try {
            val entries = CalendarRepository.fetchEntries()
            val today = LocalDate.now()
            val tomorrow = today.plusDays(1)

            val todayEvents = CalendarRepository.eventsForDate(entries, today)
            val tomorrowEvents = CalendarRepository.eventsForDate(entries, tomorrow)

            Log.d("CalWatch", "Sync: today has ${todayEvents.size}, tomorrow has ${tomorrowEvents.size}")

            var armedCount = 0
            var requestCode = 1000
            for (event in todayEvents) {
                if (armEvent(context, event, today, requestCode)) armedCount++
                requestCode++
            }
            for (event in tomorrowEvents) {
                if (armEvent(context, event, tomorrow, requestCode)) armedCount++
                requestCode++
            }

            val totalFound = todayEvents.size + tomorrowEvents.size
            notifySyncComplete(context, armedCount, totalFound)
        } catch (e: Exception) {
            Log.e("CalWatch", "Sync failed", e)
        }
    }

    private fun armEvent(context: Context, event: EventEntry, date: LocalDate, requestCode: Int): Boolean {
        if (event.start.isNullOrBlank()) {
            Log.d("CalWatch", "Skipping '${event.title}' - no start time")
            return false
        }
        val exactStartAt = AlarmScheduler.epochMillisFor(date, event.start)
        val triggerAt = exactStartAt - REMINDER_LEAD_MILLIS
        if (triggerAt <= System.currentTimeMillis()) {
            Log.d("CalWatch", "Skipping '${event.title}' - reminder time already passed")
            return false
        }

        AlarmScheduler.scheduleEventAlarm(
            context = context,
            requestCode = requestCode,
            triggerAtEpochMilli = triggerAt,
            title = event.title,
            note = event.note
        )
        Log.d("CalWatch", "Armed alarm for '${event.title}' 30 min before ${event.start} ($triggerAt)")
        return true
    }

    private fun notifySyncComplete(context: Context, armedCount: Int, totalFound: Int) {
        val channelId = "sync_status"
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (nm.getNotificationChannel(channelId) == null) {
            val channel = NotificationChannel(
                channelId, "Sync status", NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Confirms when a calendar sync has completed"
            }
            nm.createNotificationChannel(channel)
        }

        val text = if (totalFound == 0) {
            "No events found for today or tomorrow"
        } else {
            val eventWord = if (totalFound == 1) "event" else "events"
            val verb = if (armedCount == 1) "has" else "have"
            "$armedCount of $totalFound $eventWord $verb an alarm set"
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("Schedule synced")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        nm.notify(9999, notification)
    }
}