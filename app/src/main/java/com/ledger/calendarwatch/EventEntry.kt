package com.ledger.calendarwatch

/**
 * Mirrors one entry from the Firestore document (calendar/main → entries →
 * "2026-6-12" → array of these):
 *   { id: "1783296109276-micx1", title: "Work", startTime: "06:30", endTime: "14:30", note: "" }
 */
data class EventEntry(
    val id: String = "",
    val title: String = "",
    val start: String? = null,   // from startTime, "HH:mm" 24-hour, null for an untimed entry
    val end: String? = null,     // from endTime, "HH:mm" 24-hour, optional
    val note: String? = null
)
