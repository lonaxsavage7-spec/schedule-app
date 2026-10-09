package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EventCategory
import com.example.model.ScheduleEvent
import com.example.ui.components.AddEventBottomSheet
import com.example.ui.components.EventRowItem
import com.example.ui.components.MonthlyCalendarView
import com.example.ui.components.ScheduleStatsGrid
import com.example.ui.components.UpcomingEventsCard
import com.example.viewmodel.ScheduleTab
import com.example.viewmodel.ScheduleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleMainScreen(
    viewModel: ScheduleViewModel,
    isExpandedScreen: Boolean = false
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val events by viewModel.events.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val currentYearMonth by viewModel.currentYearMonth.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val isAddDialogVisible by viewModel.isAddDialogVisible.collectAsState()

    // Add event bottom sheet
    if (isAddDialogVisible) {
        AddEventBottomSheet(
            initialDate = selectedDate,
            onDismiss = { viewModel.setAddDialogVisible(false) },
            onSave = { title, date, time, category, desc ->
                viewModel.addEvent(title, date, time, category, desc)
            }
        )
    }

    if (isExpandedScreen) {
        // Tablet / Foldable side navigation rail layout
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail(
                containerColor = Color(0xFF111827),
                contentColor = Color.White,
                header = {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Schedule",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = ".",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = Color(0xFF60A5FA)
                            )
                        }
                    }
                }
            ) {
                NavigationRailItem(
                    selected = currentTab == ScheduleTab.CALENDAR,
                    onClick = { viewModel.setTab(ScheduleTab.CALENDAR) },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar") },
                    label = { Text("Calendar") }
                )
                NavigationRailItem(
                    selected = currentTab == ScheduleTab.TASKS,
                    onClick = { viewModel.setTab(ScheduleTab.TASKS) },
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Tasks") },
                    label = { Text("Tasks") }
                )
                NavigationRailItem(
                    selected = currentTab == ScheduleTab.REMINDERS,
                    onClick = { viewModel.setTab(ScheduleTab.REMINDERS) },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Reminders") },
                    label = { Text("Reminders") }
                )
                NavigationRailItem(
                    selected = currentTab == ScheduleTab.OVERVIEW,
                    onClick = { viewModel.setTab(ScheduleTab.OVERVIEW) },
                    icon = { Icon(Icons.Default.PieChart, contentDescription = "Overview") },
                    label = { Text("Overview") }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                ScreenContent(
                    currentTab = currentTab,
                    viewModel = viewModel,
                    events = events,
                    stats = stats,
                    currentYearMonth = currentYearMonth,
                    selectedDate = selectedDate
                )
            }
        }
    } else {
        // Mobile layout with topbar, scrollable view, floating action button, and bottom navigation
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == ScheduleTab.CALENDAR,
                        onClick = { viewModel.setTab(ScheduleTab.CALENDAR) },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar") },
                        label = { Text("Calendar") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF2563EB),
                            indicatorColor = Color(0xFFDBEAFE)
                        )
                    )
                    NavigationBarItem(
                        selected = currentTab == ScheduleTab.TASKS,
                        onClick = { viewModel.setTab(ScheduleTab.TASKS) },
                        icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Tasks") },
                        label = { Text("Tasks") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF2563EB),
                            indicatorColor = Color(0xFFDBEAFE)
                        )
                    )
                    NavigationBarItem(
                        selected = currentTab == ScheduleTab.REMINDERS,
                        onClick = { viewModel.setTab(ScheduleTab.REMINDERS) },
                        icon = { Icon(Icons.Default.Notifications, contentDescription = "Reminders") },
                        label = { Text("Reminders") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF2563EB),
                            indicatorColor = Color(0xFFDBEAFE)
                        )
                    )
                    NavigationBarItem(
                        selected = currentTab == ScheduleTab.OVERVIEW,
                        onClick = { viewModel.setTab(ScheduleTab.OVERVIEW) },
                        icon = { Icon(Icons.Default.PieChart, contentDescription = "Overview") },
                        label = { Text("Overview") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF2563EB),
                            indicatorColor = Color(0xFFDBEAFE)
                        )
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.setAddDialogVisible(true) },
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_add_event")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Event")
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                ScreenContent(
                    currentTab = currentTab,
                    viewModel = viewModel,
                    events = events,
                    stats = stats,
                    currentYearMonth = currentYearMonth,
                    selectedDate = selectedDate
                )
            }
        }
    }
}

@Composable
fun ScreenContent(
    currentTab: ScheduleTab,
    viewModel: ScheduleViewModel,
    events: List<ScheduleEvent>,
    stats: com.example.model.ScheduleStats,
    currentYearMonth: java.time.YearMonth,
    selectedDate: LocalDate
) {
    when (currentTab) {
        ScheduleTab.CALENDAR -> CalendarTabContent(
            viewModel = viewModel,
            events = events,
            stats = stats,
            currentYearMonth = currentYearMonth,
            selectedDate = selectedDate
        )
        ScheduleTab.TASKS -> TasksTabContent(
            viewModel = viewModel,
            events = events
        )
        ScheduleTab.REMINDERS -> RemindersTabContent(
            viewModel = viewModel,
            events = events
        )
        ScheduleTab.OVERVIEW -> OverviewTabContent(
            stats = stats,
            events = events
        )
    }
}

@Composable
fun CalendarTabContent(
    viewModel: ScheduleViewModel,
    events: List<ScheduleEvent>,
    stats: com.example.model.ScheduleStats,
    currentYearMonth: java.time.YearMonth,
    selectedDate: LocalDate
) {
    val todayFormatted = LocalDate.now().format(
        DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
    )

    // Upcoming events sorted chronologically
    val upcomingEvents = events
        .sortedWith(compareBy({ it.date }, { it.time }))

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Topbar: App header + Date subtitle + Add event button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Schedule",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = todayFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { viewModel.setAddDialogVisible(true) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_topbar_add_event")
                ) {
                    Text("+ Add event", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Stats cards (Today's, This week, Important, All events)
        item {
            ScheduleStatsGrid(stats = stats)
        }

        // Monthly Interactive Calendar
        item {
            MonthlyCalendarView(
                currentYearMonth = currentYearMonth,
                selectedDate = selectedDate,
                events = events,
                onDateSelected = { date ->
                    viewModel.selectDate(date)
                    viewModel.setAddDialogVisible(true)
                },
                onPreviousMonth = { viewModel.previousMonth() },
                onNextMonth = { viewModel.nextMonth() }
            )
        }

        // Upcoming Events Card
        item {
            UpcomingEventsCard(
                events = upcomingEvents,
                onDeleteEvent = { viewModel.deleteEvent(it) }
            )
        }
    }
}

@Composable
fun TasksTabContent(
    viewModel: ScheduleViewModel,
    events: List<ScheduleEvent>
) {
    val completedCount = events.count { it.isCompleted }
    val progress = if (events.isNotEmpty()) completedCount.toFloat() / events.size else 0f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Tasks & Checklists",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Track your event completion and daily agenda",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Progress", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("$completedCount / ${events.size} Done", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF10B981)
                    )
                }
            }
        }

        items(events, key = { it.id }) { event ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (event.isCompleted) Color(0xFF10B981) else Color.Transparent
                            )
                            .border(
                                width = 2.dp,
                                color = if (event.isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                            .clickable { viewModel.toggleComplete(event.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (event.isCompleted) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = event.title,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (event.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${event.date} at ${event.time} • ${event.category.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RemindersTabContent(
    viewModel: ScheduleViewModel,
    events: List<ScheduleEvent>
) {
    val importantEvents = events.filter { it.category == EventCategory.IMPORTANT }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Priority Reminders",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "High priority and time-sensitive reminders",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (importantEvents.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("No important reminders marked.", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Add an event with the 'Important' tag to view it here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(importantEvents, key = { it.id }) { event ->
                EventRowItem(
                    event = event,
                    onDelete = { viewModel.deleteEvent(event.id) }
                )
            }
        }
    }
}

@Composable
fun OverviewTabContent(
    stats: com.example.model.ScheduleStats,
    events: List<ScheduleEvent>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Schedule Overview",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Category breakdown and analytics",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            ScheduleStatsGrid(stats = stats)
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Distribution by Category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    EventCategory.values().forEach { category ->
                        val count = events.count { it.category == category }
                        val fraction = if (events.isNotEmpty()) count.toFloat() / events.size else 0f
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = category.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "$count (${(fraction * 100).toInt()}%)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            LinearProgressIndicator(
                                progress = { fraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color(category.hexColor)
                            )
                        }
                    }
                }
            }
        }
    }
}
