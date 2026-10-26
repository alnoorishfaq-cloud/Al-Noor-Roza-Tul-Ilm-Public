package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SchoolClass

@Composable
fun ClassSelectorBar(
    currentClass: SchoolClass?,
    allClasses: List<SchoolClass>,
    studentCount: Int,
    onClassSelected: (String) -> Unit,
    onNewClassClick: () -> Unit,
    onEditClassClick: (SchoolClass) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Class Picker Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.5.dp, Color(0xFFCAD8D3), RoundedCornerShape(10.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .testTag("class_selector_dropdown"),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentClass?.let { c ->
                            buildString {
                                append(c.name)
                                if (c.group.isNotBlank()) append(" [${c.group}]")
                            }
                        } ?: "Select Class",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Open classes dropdown",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    if (allClasses.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No classes available") },
                            onClick = { expanded = false }
                        )
                    } else {
                        allClasses.forEach { schoolClass ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = schoolClass.name,
                                            fontWeight = if (schoolClass.id == currentClass?.id) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (schoolClass.teacher.isNotBlank() || schoolClass.group.isNotBlank()) {
                                            Text(
                                                text = listOfNotNull(
                                                    schoolClass.group.takeIf { it.isNotBlank() },
                                                    schoolClass.teacher.takeIf { it.isNotBlank() }
                                                ).joinToString(" • "),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    onClassSelected(schoolClass.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // "+ New Class" Button
            OutlinedButton(
                onClick = onNewClassClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("new_class_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Class",
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = "Class",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // Teacher Information Banner
        if (currentClass != null) {
            Spacer(modifier = Modifier.padding(top = 8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFD6EFE6), Color(0xFFDBE8FF))
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildString {
                        if (currentClass.group.isNotBlank()) append("${currentClass.group} | ")
                        append("Teacher: ${currentClass.teacher.ifBlank { "Not set" }}")
                        append(" • $studentCount ${if (studentCount == 1) "student" else "students"}")
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF0F3B32),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp
                    ),
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onEditClassClick(currentClass) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("edit_class_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Class",
                            tint = Color(0xFF0F6B57),
                            modifier = Modifier.padding(end = 3.dp)
                        )
                        Text(
                            text = "Edit",
                            color = Color(0xFF0F6B57),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
