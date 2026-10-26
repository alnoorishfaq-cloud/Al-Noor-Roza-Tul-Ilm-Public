package com.example.ui.screens

import android.app.DatePickerDialog
import android.content.Intent
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.DailyAttendanceCount
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.ui.components.AttendanceStatCards
import com.example.ui.components.StudentAttendanceRow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AttendanceScreen(
    currentClass: SchoolClass?,
    selectedDate: String,
    students: List<Student>,
    records: List<AttendanceRecord>,
    dailyCount: DailyAttendanceCount,
    onDateSelected: (String) -> Unit,
    onPreviousDate: () -> Unit,
    onNextDate: () -> Unit,
    onTodayClick: () -> Unit,
    onStatusToggle: (studentId: String, status: AttendanceStatus) -> Unit,
    onMarkAllPresent: () -> Unit,
    onShareReport: () -> Unit,
    onNavigateToStudents: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val formattedDateText = remember(selectedDate) {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val d = parser.parse(selectedDate)
            if (d != null) {
                SimpleDateFormat("EEE, dd MMM yyyy", Locale.US).format(d)
            } else selectedDate
        } catch (_: Exception) {
            selectedDate
        }
    }

    val recordMap = remember(records) {
        records.associate { it.studentId to AttendanceStatus.fromCode(it.status) }
    }

    val filteredStudents = remember(students, searchQuery) {
        if (searchQuery.isBlank()) students
        else {
            val q = searchQuery.trim().lowercase()
            students.filter {
                it.name.lowercase().contains(q) ||
                it.roll.contains(q) ||
                it.fatherName.lowercase().contains(q)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("attendance_screen")
    ) {
        // Date Selector & Mark All Present Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date Switcher Box
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.5.dp, Color(0xFFCAD8D3), RoundedCornerShape(10.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPreviousDate,
                    modifier = Modifier.size(36.dp).testTag("date_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Day",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            // Show DatePickerDialog
                            val cal = Calendar.getInstance()
                            try {
                                val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                val d = parser.parse(selectedDate)
                                if (d != null) cal.time = d
                            } catch (_: Exception) {}

                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    val newDate = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)
                                    onDateSelected(newDate)
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Select Date",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formattedDateText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.5.sp
                        )
                    )
                }

                IconButton(
                    onClick = onNextDate,
                    modifier = Modifier.size(36.dp).testTag("date_next_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Day",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Mark All Present Button
            OutlinedButton(
                onClick = onMarkAllPresent,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF0F6B57)
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0F6B57)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("mark_all_present_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp).padding(end = 4.dp)
                )
                Text(
                    text = "All Present",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Large Stat Cards (Present, Absent, Leave)
        AttendanceStatCards(dailyCount = dailyCount)

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar for Students
        if (students.size > 4) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search student by name or roll...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color(0xFFCAD8D3)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .testTag("student_search_input")
            )
        }

        // Student Attendance List
        if (students.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAD8D3))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No students in this class yet.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onNavigateToStudents,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("goto_add_students_button")
                    ) {
                        Text("Add Students Now")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                itemsIndexed(filteredStudents, key = { _, s -> s.id }) { index, student ->
                    StudentAttendanceRow(
                        student = student,
                        status = recordMap[student.id],
                        index = index,
                        onStatusToggle = { status ->
                            onStatusToggle(student.id, status)
                        }
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "P = Present, A = Absent, L = Leave. Every tap is saved automatically.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.5.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onShareReport,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("share_day_report_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Share Daily Attendance Report",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
