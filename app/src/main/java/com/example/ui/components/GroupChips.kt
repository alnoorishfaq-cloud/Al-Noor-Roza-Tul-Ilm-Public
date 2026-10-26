package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GroupChips(
    selectedGroup: String,
    groupCounts: Map<String, Int>,
    onGroupSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val groups = listOf(
        "" to "All groups",
        "Hifaz ul Quran" to "Hifaz ul Quran",
        "Nazira" to "Nazira"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        groups.forEach { (groupId, groupLabel) ->
            val isSelected = selectedGroup.equals(groupId, ignoreCase = true)
            val count = groupCounts[groupId] ?: 0

            val bgBrush = when {
                isSelected && groupId == "Hifaz ul Quran" -> Brush.linearGradient(
                    listOf(Color(0xFF12A37C), Color(0xFF0F6B57))
                )
                isSelected && groupId == "Nazira" -> Brush.linearGradient(
                    listOf(Color(0xFF9B6BFF), Color(0xFF6232D1))
                )
                isSelected -> Brush.linearGradient(
                    listOf(Color(0xFF3B82F6), Color(0xFF1D4FC4))
                )
                else -> null
            }

            val borderColor = when (groupId) {
                "Hifaz ul Quran" -> Color(0xFFB0DFD1)
                "Nazira" -> Color(0xFFD6C8F6)
                else -> Color(0xFFCBDDF7)
            }

            val textColor = when {
                isSelected -> Color.White
                groupId == "Hifaz ul Quran" -> Color(0xFF0F6B57)
                groupId == "Nazira" -> Color(0xFF6232D1)
                else -> Color(0xFF1D4FC4)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .then(
                        if (bgBrush != null) Modifier.background(bgBrush)
                        else Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
                    )
                    .clickable { onGroupSelected(groupId) }
                    .padding(vertical = 9.dp, horizontal = 4.dp)
                    .testTag("group_chip_${if (groupId.isEmpty()) "all" else groupId.lowercase().replace(" ", "_")}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$groupLabel ($count)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = textColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 11.5.sp
                    ),
                    maxLines = 1
                )
            }
        }
    }
}
