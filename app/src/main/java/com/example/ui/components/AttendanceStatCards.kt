package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyAttendanceCount

@Composable
fun AttendanceStatCards(
    dailyCount: DailyAttendanceCount,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Present Card
        StatCard(
            count = dailyCount.present,
            label = "Present",
            brush = Brush.linearGradient(listOf(Color(0xFF22B88F), Color(0xFF0F6B57))),
            tag = "stat_present",
            modifier = Modifier.weight(1f)
        )

        // Absent Card
        StatCard(
            count = dailyCount.absent,
            label = "Absent",
            brush = Brush.linearGradient(listOf(Color(0xFFF0646A), Color(0xFFB3261E))),
            tag = "stat_absent",
            modifier = Modifier.weight(1f)
        )

        // Leave Card
        StatCard(
            count = dailyCount.leave,
            label = "Leave",
            brush = Brush.linearGradient(listOf(Color(0xFFF7B733), Color(0xFFC77700))),
            tag = "stat_leave",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    count: Int,
    label: String,
    brush: Brush,
    tag: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(brush)
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White.copy(alpha = 0.95f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            )
        }
    }
}
