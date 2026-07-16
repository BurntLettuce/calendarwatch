package com.ledger.calendarwatch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DailyScheduleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("CalWatch", "DailyScheduleReceiver.onReceive fired at ${System.currentTimeMillis()}")

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                AlarmSyncCoordinator.syncTodayAndTomorrow(context)
            } finally {
                AlarmScheduler.scheduleDailyUpdate(context)
                Log.d("CalWatch", "Re-armed daily job for tomorrow night")
                pendingResult.finish()
            }
        }
    }
}