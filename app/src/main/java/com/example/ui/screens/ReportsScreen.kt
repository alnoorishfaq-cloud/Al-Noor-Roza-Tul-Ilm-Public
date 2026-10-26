package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.StudentAttendanceStat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReportsScreen(
    currentClass: SchoolClass?,
    recordedDates: List<String>,
    studentStats: List<StudentAttendanceStat>,
    allRecords: List<AttendanceRecord>,
    students: List<Student>,
    onSelectDate: (String) -> Unit,
    onShareDailyReportForDate: (String) -> Unit,
    onShareAllReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val recordsByDate = allRecords.groupBy { it.date }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("reports_screen")
    ) {
        // Summary hint
        Text(
            text = buildString {
                if (currentClass?.teacher?.isNotBlank() == true) {
                    append("Teacher: ${currentClass.teacher}. ")
                }
                if (recordedDates.isNotEmpty()) {
                    append("${recordedDates.size} ${if (recordedDates.size == 1) "day" else "days"} of attendance recorded.")
                }
            },
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.5.sp
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        if (studentStats.isEmpty() || recordedDates.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAD8D3))
            ) {
                Text(
                    text = "The report appears after you mark attendance for at least one day.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(24.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Share Overall Report Button
                item {
                    Button(
                        onClick = onShareAllReport,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D3BD2)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("share_overall_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Share Overall Class Report",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Daily Reports History Section
                item {
                    Text(
                        text = "Daily Attendance Reports",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6D3BD2)
                        ),
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                    )
                }

                items(recordedDates.take(10)) { date ->
                    val dayRecords = recordsByDate[date].orEmpty()
                    val pCount = dayRecords.count { it.status == AttendanceStatus.PRESENT.code }
                    val aCount = dayRecords.count { it.status == AttendanceStatus.ABSENT.code }
                    val lCount = dayRecords.count { it.status == AttendanceStatus.LEAVE.code }

                    val formattedDate = try {
                        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        val d = parser.parse(date)
                        if (d != null) SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.US).format(d) else date
                    } catch (_: Exception) {
                        date
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0EAE6)),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Present: $pCount  •  Absent: $aCount  •  Leave: $lCount",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    onSelectDate(date)
                                    onShareDailyReportForDate(date)
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("share_date_report_$date")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Attendance by Student Section
                item {
                    Text(
                        text = "Attendance by Student",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D5FD1)
                        ),
                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                    )
                }

                items(studentStats, key = { it.student.id }) { stat ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0EAE6)),
                        shadowElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD6EFE6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stat.student.roll.ifBlank { "-" },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF0F6B57),
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stat.student.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.5.sp
                                        )
                                    )
                                    if (stat.student.fatherName.isNotBlank()) {
                                        Text(
                                            text = "Father: ${stat.student.fatherName}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.5.sp
                                            )
                                        )
                                    }
                                }

                                Text(
                                    text = "${stat.percentage}%",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = if (stat.percentage >= 75) Color(0xFF0F6B57) else if (stat.percentage >= 50) Color(0xFFC77700) else Color(0xFFB3261E),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Colored Progress Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFE4ECE9))
                            ) {
                                val progressFraction = (stat.percentage / 100f).coerceIn(0f, 1f)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction = progressFraction)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFF22B88F), Color(0xFF3B82F6), Color(0xFF9B6BFF))
                                            )
                                        )
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Present ${stat.presentCount}  ·  Absent ${stat.absentCount}  ·  Leave ${stat.leaveCount}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
