package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.SchoolClass
import com.example.ui.components.ClassSelectorBar
import com.example.ui.components.GroupChips
import com.example.ui.components.PreparedByFooter
import com.example.ui.components.SchoolHeader
import com.example.ui.viewmodel.AttendanceViewModel
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    ATTENDANCE("Attendance"),
    STUDENTS("Students"),
    REPORT("Report")
}

@Composable
fun MainAppScreen(
    viewModel: AttendanceViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTab by remember { mutableStateOf(AppTab.ATTENDANCE) }
    var showNewClassDialog by remember { mutableStateOf(false) }
    var classToEdit by remember { mutableStateOf<SchoolClass?>(null) }

    val currentClass by viewModel.currentClass.collectAsState()
    val filteredClasses by viewModel.filteredClasses.collectAsState()
    val selectedGroup by viewModel.selectedGroup.collectAsState()
    val groupCounts by viewModel.groupCounts.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    val currentStudents by viewModel.currentStudents.collectAsState()
    val currentAttendance by viewModel.currentAttendance.collectAsState()
    val allClassAttendance by viewModel.allClassAttendance.collectAsState()
    val dailyCount by viewModel.dailyCount.collectAsState()
    val studentStats by viewModel.studentStats.collectAsState()
    val recordedDates by viewModel.recordedDates.collectAsState()

    BackHandler(enabled = selectedTab != AppTab.ATTENDANCE) {
        selectedTab = AppTab.ATTENDANCE
    }

    // New Class Dialog
    if (showNewClassDialog) {
        ClassFormDialog(
            initialClass = null,
            onDismiss = { showNewClassDialog = false },
            onSave = { name, teacher, group ->
                viewModel.addClass(name, teacher, group) {
                    showNewClassDialog = false
                    selectedTab = AppTab.STUDENTS
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Class '$name' created!")
                    }
                }
            }
        )
    }

    // Edit Class Dialog
    if (classToEdit != null) {
        ClassFormDialog(
            initialClass = classToEdit,
            onDismiss = { classToEdit = null },
            onSave = { name, teacher, group ->
                classToEdit?.let {
                    viewModel.updateClass(it.copy(name = name, teacher = teacher, group = group))
                }
                classToEdit = null
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Class updated!")
                }
            },
            onDelete = {
                classToEdit?.let {
                    viewModel.deleteClass(it.id)
                }
                classToEdit = null
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Class deleted.")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // Header with authentic emblem
            SchoolHeader()

            Spacer(modifier = Modifier.height(10.dp))

            // Group Chips
            GroupChips(
                selectedGroup = selectedGroup,
                groupCounts = groupCounts,
                onGroupSelected = { viewModel.setGroupFilter(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Class Selector & Teacher info
            ClassSelectorBar(
                currentClass = currentClass,
                allClasses = filteredClasses,
                studentCount = currentStudents.size,
                onClassSelected = { viewModel.selectClass(it) },
                onNewClassClick = { showNewClassDialog = true },
                onEditClassClick = { classToEdit = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Navigation Tabs (Attendance, Students, Report)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AppTabButton(
                    tab = AppTab.ATTENDANCE,
                    isSelected = selectedTab == AppTab.ATTENDANCE,
                    icon = Icons.Default.AssignmentTurnedIn,
                    activeBrush = Brush.linearGradient(listOf(Color(0xFF12A37C), Color(0xFF0F6B57))),
                    activeTextColor = Color.White,
                    inactiveBorderColor = Color(0xFF0F6B57),
                    inactiveTextColor = Color(0xFF0F6B57),
                    testTag = "tab_attendance",
                    onClick = { selectedTab = AppTab.ATTENDANCE },
                    modifier = Modifier.weight(1f)
                )

                AppTabButton(
                    tab = AppTab.STUDENTS,
                    isSelected = selectedTab == AppTab.STUDENTS,
                    icon = Icons.Default.Group,
                    activeBrush = Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF1D4FC4))),
                    activeTextColor = Color.White,
                    inactiveBorderColor = Color(0xFF1D4FC4),
                    inactiveTextColor = Color(0xFF1D4FC4),
                    testTag = "tab_students",
                    onClick = { selectedTab = AppTab.STUDENTS },
                    modifier = Modifier.weight(1f)
                )

                AppTabButton(
                    tab = AppTab.REPORT,
                    isSelected = selectedTab == AppTab.REPORT,
                    icon = Icons.Default.Assessment,
                    activeBrush = Brush.linearGradient(listOf(Color(0xFF9B6BFF), Color(0xFF6232D1))),
                    activeTextColor = Color.White,
                    inactiveBorderColor = Color(0xFF6232D1),
                    inactiveTextColor = Color(0xFF6232D1),
                    testTag = "tab_report",
                    onClick = { selectedTab = AppTab.REPORT },
                    modifier = Modifier.weight(1f)
                )
            }

            // Main Screen Content Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    AppTab.ATTENDANCE -> {
                        AttendanceScreen(
                            currentClass = currentClass,
                            selectedDate = selectedDate,
                            students = currentStudents,
                            records = currentAttendance,
                            dailyCount = dailyCount,
                            onDateSelected = { viewModel.selectDate(it) },
                            onPreviousDate = { viewModel.previousDate() },
                            onNextDate = { viewModel.nextDate() },
                            onTodayClick = { viewModel.resetDateToToday() },
                            onStatusToggle = { studentId, status ->
                                viewModel.toggleAttendance(studentId, status)
                            },
                            onMarkAllPresent = {
                                viewModel.markAllPresent()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("All students marked present!")
                                }
                            },
                            onShareReport = {
                                val report = viewModel.getShareableDailyReport()
                                if (report.isNotBlank()) {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, report)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Attendance Report via"))
                                }
                            },
                            onNavigateToStudents = {
                                selectedTab = AppTab.STUDENTS
                            }
                        )
                    }

                    AppTab.STUDENTS -> {
                        StudentsScreen(
                            currentClass = currentClass,
                            students = currentStudents,
                            suggestedRoll = viewModel.getNextRollNumber(),
                            onAddStudent = { roll, name, father ->
                                viewModel.addStudent(roll, name, father) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Student '$name' added!")
                                    }
                                }
                            },
                            onUpdateStudent = { student ->
                                viewModel.updateStudent(student)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Student updated.")
                                }
                            },
                            onDeleteStudent = { studentId ->
                                viewModel.deleteStudent(studentId)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Student removed.")
                                }
                            },
                            onDeleteClass = {
                                currentClass?.let { viewModel.deleteClass(it.id) }
                            }
                        )
                    }

                    AppTab.REPORT -> {
                        ReportsScreen(
                            currentClass = currentClass,
                            recordedDates = recordedDates,
                            studentStats = studentStats,
                            allRecords = allClassAttendance,
                            students = currentStudents,
                            onSelectDate = { date ->
                                viewModel.selectDate(date)
                            },
                            onShareDailyReportForDate = { date ->
                                viewModel.selectDate(date)
                                val report = viewModel.getShareableDailyReport()
                                if (report.isNotBlank()) {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, report)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Day Report via"))
                                }
                            },
                            onShareAllReport = {
                                val report = viewModel.getShareableStudentReport()
                                if (report.isNotBlank()) {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, report)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Class Report via"))
                                }
                            }
                        )
                    }
                }
            }

            // Prepared by Muhammad Ishfaq footer
            PreparedByFooter()
        }
    }
}

@Composable
private fun AppTabButton(
    tab: AppTab,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    activeBrush: Brush,
    activeTextColor: Color,
    inactiveBorderColor: Color,
    inactiveTextColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (isSelected) Modifier.background(activeBrush)
                else Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .border(2.dp, inactiveBorderColor, RoundedCornerShape(10.dp))
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) activeTextColor else inactiveTextColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = tab.title,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = if (isSelected) activeTextColor else inactiveTextColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            )
        }
    }
}
