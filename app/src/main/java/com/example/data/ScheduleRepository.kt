package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.EventCategory
import com.example.model.ScheduleEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ScheduleRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("schedule_prefs", Context.MODE_PRIVATE)
    private val _events = MutableStateFlow<List<ScheduleEvent>>(emptyList())
    val events: StateFlow<List<ScheduleEvent>> = _events.asStateFlow()

    init {
        loadEvents()
    }

    private fun loadEvents() {
        val rawJson = prefs.getString(KEY_EVENTS, null)
        if (rawJson != null) {
            try {
                val jsonArray = JSONArray(rawJson)
                val list = mutableListOf<ScheduleEvent>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(
                        ScheduleEvent(
                            id = obj.optLong("id", System.currentTimeMillis()),
                            title = obj.getString("title"),
                            date = obj.getString("date"),
                            time = obj.getString("time"),
                            category = try {
                                EventCategory.valueOf(obj.getString("category"))
                            } catch (_: Exception) {
                                EventCategory.WORK
                            },
                            description = obj.optString("description", ""),
                            isCompleted = obj.optBoolean("isCompleted", false)
                        )
                    )
                }
                _events.value = list
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Initialize with default sample events matching the web app demo
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
        val sampleEvents = listOf(
            ScheduleEvent(
                id = 1L,
                title = "Team meeting",
                date = today,
                time = "09:00",
                category = EventCategory.WORK,
                description = "Quarterly planning sync with product team"
            ),
            ScheduleEvent(
                id = 2L,
                title = "Product Review",
                date = today,
                time = "14:00",
                category = EventCategory.IMPORTANT,
                description = "Review Q4 feature roadmap and deliverables"
            ),
            ScheduleEvent(
                id = 3L,
                title = "Gym & Workout",
                date = today,
                time = "17:30",
                category = EventCategory.HEALTH,
                description = "Strength training and cardio session"
            ),
            ScheduleEvent(
                id = 4L,
                title = "Dinner with Sarah",
                date = tomorrow,
                time = "19:00",
                category = EventCategory.PERSONAL,
                description = "Catch up at downtown bistro"
            )
        )
        saveList(sampleEvents)
    }

    private fun saveList(list: List<ScheduleEvent>) {
        _events.value = list
        try {
            val jsonArray = JSONArray()
            for (item in list) {
                val obj = JSONObject()
                obj.put("id", item.id)
                obj.put("title", item.title)
                obj.put("date", item.date)
                obj.put("time", item.time)
                obj.put("category", item.category.name)
                obj.put("description", item.description)
                obj.put("isCompleted", item.isCompleted)
                jsonArray.put(obj)
            }
            prefs.edit().putString(KEY_EVENTS, jsonArray.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addEvent(event: ScheduleEvent) {
        val current = _events.value.toMutableList()
        current.add(event)
        saveList(current)
    }

    fun deleteEvent(id: Long) {
        val current = _events.value.filter { it.id != id }
        saveList(current)
    }

    fun toggleComplete(id: Long) {
        val current = _events.value.map {
            if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
        }
        saveList(current)
    }

    companion object {
        private const val KEY_EVENTS = "saved_schedule_events"
    }
}
