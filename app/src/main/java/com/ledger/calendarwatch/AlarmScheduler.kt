package com.ledger.calendarwatch

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object AlarmScheduler {

    private const val DAILY_REQUEST_CODE = 9000

    /** Schedules (or re-schedules) the once-a-day 23:59 job that pulls tomorrow's events. */
    fun scheduleDailyUpdate(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyScheduleReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, DAILY_REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        var next = LocalDateTime.now().with(LocalTime.of(23, 59))
        if (next.isBefore(LocalDateTime.now())) {
            next = next.plusDays(1)
        }
        val triggerAt = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent
        )
    }

    /** Schedules a single event's start-time alarm. requestCode must be stable/unique per event. */
    fun scheduleEventAlarm(
        context: Context,
        requestCode: Int,
        triggerAtEpochMilli: Long,
        title: String,
        note: String?
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EventAlarmReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("note", note)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, triggerAtEpochMilli, pendingIntent
        )
    }

    /** Turns "2026-06-06" + "HH:mm" into an epoch-millis trigger time. */
    fun epochMillisFor(date: LocalDate, time: String): Long {
        val parts = time.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 9
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return date.atTime(hour, minute)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}