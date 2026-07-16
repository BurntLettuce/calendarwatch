package com.ledger.calendarwatch

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

object CalendarRepository {

    private val db by lazy { FirebaseFirestore.getInstance() }

    suspend fun fetchEntries(): Map<*, *> {
        val snap = db.collection("calendar").document("main").get().await()
        return snap.get("entries") as? Map<*, *> ?: emptyMap<Any, Any>()
    }

    suspend fun getEventsForDate(date: LocalDate): List<EventEntry> {
        return eventsForDate(fetchEntries(), date)
    }

    /**
     * Pulls just [date]'s events out of an already-fetched entries map.
     * Confirmed against real Firestore data:
     *   entries: {
     *     "2026-6-12": [                     <- NOT zero-padded (year-M-d)
     *        { id: "...", title: "Work", startTime: "06:30", endTime: "14:30", note: "" },
     *        ...
     *     ]
     *   }
     */
    fun eventsForDate(entries: Map<*, *>, date: LocalDate): List<EventEntry> {
        val dateKey = dateKeyFor(date)
        val dayData = entries[dateKey] ?: return emptyList()

        val rawList: List<*> = when (dayData) {
            is List<*> -> dayData
            is Map<*, *> -> dayData.values.toList()
            else -> emptyList<Any>()
        }

        return rawList.mapIndexedNotNull { index, item ->
            val map = item as? Map<*, *> ?: return@mapIndexedNotNull null
            EventEntry(
                id = map["id"] as? String ?: "$dateKey-$index",
                title = (map["title"] as? String)?.ifBlank { "Untitled" } ?: "Untitled",
                start = map["startTime"] as? String,
                end = map["endTime"] as? String,
                note = (map["note"] as? String)?.ifBlank { null }
            )
        }
    }

    private fun dateKeyFor(date: LocalDate): String =
        "${date.year}-${date.monthValue}-${date.dayOfMonth}"
}