package com.example

import com.example.model.EventCategory
import com.example.model.ScheduleEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScheduleEventUnitTest {
    @Test
    fun scheduleEvent_defaultsAndProperties() {
        val event = ScheduleEvent(
            id = 101L,
            title = "Design Sync",
            date = "2026-10-08",
            time = "10:30",
            category = EventCategory.WORK,
            description = "Sprint kickoff"
        )

        assertEquals("Design Sync", event.title)
        assertEquals("2026-10-08", event.date)
        assertEquals("10:30", event.time)
        assertEquals(EventCategory.WORK, event.category)
        assertFalse(event.isCompleted)
    }

    @Test
    fun eventCategory_colorsAndDisplayNames() {
        assertEquals("Work", EventCategory.WORK.displayName)
        assertEquals("Personal", EventCategory.PERSONAL.displayName)
        assertEquals("Important", EventCategory.IMPORTANT.displayName)
        assertEquals("Health", EventCategory.HEALTH.displayName)

        assertTrue(EventCategory.WORK.hexColor != 0L)
    }
}
