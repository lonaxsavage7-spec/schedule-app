package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ScheduleRepository
import com.example.model.EventCategory
import com.example.model.ScheduleEvent
import com.example.model.ScheduleStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

enum class ScheduleTab {
    CALENDAR,
    TASKS,
    REMINDERS,
    OVERVIEW
}

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ScheduleRepository(application)
    val events = repository.events

    // Current navigation tab
    private val _currentTab = MutableStateFlow(ScheduleTab.CALENDAR)
    val currentTab: StateFlow<ScheduleTab> = _currentTab.asStateFlow()

    // Calendar selected month & selected day
    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Add Dialog / BottomSheet state
    private val _isAddDialogVisible = MutableStateFlow(false)
    val isAddDialogVisible: StateFlow<Boolean> = _isAddDialogVisible.asStateFlow()

    // Filter by Category
    private val _selectedCategoryFilter = MutableStateFlow<EventCategory?>(null)
    val selectedCategoryFilter: StateFlow<EventCategory?> = _selectedCategoryFilter.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Statistics calculated reactively
    val stats: StateFlow<ScheduleStats> = events.combine(selectedDate) { eventList, _ ->
        val todayStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val today = LocalDate.now()
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))

        val todayCount = eventList.count { it.date == todayStr }
        val weekCount = eventList.count {
            try {
                val d = LocalDate.parse(it.date)
                !d.isBefore(startOfWeek) && !d.isAfter(endOfWeek)
            } catch (_: Exception) {
                false
            }
        }
        val importantCount = eventList.count { it.category == EventCategory.IMPORTANT }
        val allCount = eventList.size

        ScheduleStats(
            todayCount = todayCount,
            weekCount = weekCount,
            importantCount = importantCount,
            allCount = allCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScheduleStats(0, 0, 0, 0)
    )

    fun setTab(tab: ScheduleTab) {
        _currentTab.value = tab
    }

    fun nextMonth() {
        _currentYearMonth.value = _currentYearMonth.value.plusMonths(1)
    }

    fun previousMonth() {
        _currentYearMonth.value = _currentYearMonth.value.minusMonths(1)
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun setAddDialogVisible(visible: Boolean) {
        _isAddDialogVisible.value = visible
    }

    fun setCategoryFilter(category: EventCategory?) {
        _selectedCategoryFilter.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addEvent(
        title: String,
        date: LocalDate,
        time: String,
        category: EventCategory,
        description: String
    ) {
        val event = ScheduleEvent(
            id = System.currentTimeMillis(),
            title = title.trim(),
            date = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
            time = time.trim(),
            category = category,
            description = description.trim()
        )
        repository.addEvent(event)
    }

    fun deleteEvent(id: Long) {
        repository.deleteEvent(id)
    }

    fun toggleComplete(id: Long) {
        repository.toggleComplete(id)
    }
}
