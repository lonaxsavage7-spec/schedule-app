package com.example.model

enum class EventCategory(val displayName: String, val hexColor: Long) {
    WORK("Work", 0xFF2563EB),
    PERSONAL("Personal", 0xFF8B5CF6),
    IMPORTANT("Important", 0xFFEF4444),
    HEALTH("Health", 0xFF10B981)
}

data class ScheduleEvent(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val date: String, // Format: YYYY-MM-DD
    val time: String, // Format: HH:mm
    val category: EventCategory,
    val description: String = "",
    val isCompleted: Boolean = false
)

data class ScheduleStats(
    val todayCount: Int,
    val weekCount: Int,
    val importantCount: Int,
    val allCount: Int
)
