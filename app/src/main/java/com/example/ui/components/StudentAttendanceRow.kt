package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceStatus
import com.example.data.model.Student

@Composable
fun StudentAttendanceRow(
    student: Student,
    status: AttendanceStatus?,
    index: Int,
    onStatusToggle: (AttendanceStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val leftBorderColor = when (status) {
        AttendanceStatus.PRESENT -> Color(0xFF1AA57F)
        AttendanceStatus.ABSENT -> Color(0xFFE5484D)
        AttendanceStatus.LEAVE -> Color(0xFFF5A524)
        null -> Color(0xFFD5E2DE)
    }

    val rollBgColor = when (index % 4) {
        0 -> Color(0xFFD6EFE6)
        1 -> Color(0xFFDBE8FF)
        2 -> Color(0xFFE8DEFC)
        else -> Color(0xFFFFE6D1)
    }
    val rollTextColor = when (index % 4) {
        0 -> Color(0xFF0F6B57)
        1 -> Color(0xFF1D5FD1)
        2 -> Color(0xFF6D3BD2)
        else -> Color(0xFFD9630B)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("student_row_${student.id}"),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0EAE6))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Indicator Bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(38.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(leftBorderColor)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Roll Number Badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(rollBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.roll.ifBlank { "-" },
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = rollTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Name and Father Name
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    ),
                    maxLines = 1
                )
                if (student.fatherName.isNotBlank()) {
                    Text(
                        text = "Father: ${student.fatherName}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        ),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // P, A, L Segmented Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SegmentButton(
                    label = "P",
                    isSelected = status == AttendanceStatus.PRESENT,
                    selectedColor = Color(0xFF0F6B57),
                    unselectedBorder = Color(0xFFBBE5D8),
                    unselectedText = Color(0xFF0F6B57),
                    testTag = "btn_p_${student.id}",
                    onClick = { onStatusToggle(AttendanceStatus.PRESENT) }
                )
                SegmentButton(
                    label = "A",
                    isSelected = status == AttendanceStatus.ABSENT,
                    selectedColor = Color(0xFFB3261E),
                    unselectedBorder = Color(0xFFF7C5C2),
                    unselectedText = Color(0xFFB3261E),
                    testTag = "btn_a_${student.id}",
                    onClick = { onStatusToggle(AttendanceStatus.ABSENT) }
                )
                SegmentButton(
                    label = "L",
                    isSelected = status == AttendanceStatus.LEAVE,
                    selectedColor = Color(0xFFC77700),
                    unselectedBorder = Color(0xFFF9E4B5),
                    unselectedText = Color(0xFF8A5A00),
                    testTag = "btn_l_${student.id}",
                    onClick = { onStatusToggle(AttendanceStatus.LEAVE) }
                )
            }
        }
    }
}

@Composable
private fun SegmentButton(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    unselectedBorder: Color,
    unselectedText: Color,
    testTag: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 44.dp) // Accessibility min 44-48dp
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (isSelected) Modifier.background(selectedColor)
                else Modifier
                    .background(Color.Transparent)
                    .border(2.dp, unselectedBorder, RoundedCornerShape(10.dp))
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                color = if (isSelected) Color.White else unselectedText,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        )
    }
}
