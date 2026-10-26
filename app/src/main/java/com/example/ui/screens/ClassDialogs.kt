package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SchoolClass
import com.example.data.model.Student

@Composable
fun ClassFormDialog(
    initialClass: SchoolClass? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, teacher: String, group: String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var name by remember { mutableStateOf(initialClass?.name ?: "") }
    var teacher by remember { mutableStateOf(initialClass?.teacher ?: "") }
    var group by remember { mutableStateOf(initialClass?.group ?: "") }
    var nameError by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val groups = listOf(
        "" to "No group (General)",
        "Hifaz ul Quran" to "Hifaz ul Quran",
        "Nazira" to "Nazira"
    )

    if (showDeleteConfirm && onDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Class?") },
            text = { Text("Are you sure you want to delete '${initialClass?.name}'? All its students and attendance records will also be removed.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialClass == null) "Add New Class" else "Edit Class",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text("Class Name (e.g. Class 1, 5-A)") },
                    isError = nameError,
                    supportingText = if (nameError) { { Text("Class name is required") } } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_class_name")
                )

                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text("Teacher Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_teacher_name")
                )

                Text(
                    text = "Select Group / Category:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                groups.forEach { (gValue, gLabel) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { group = gValue }
                            .padding(vertical = 2.dp)
                    ) {
                        RadioButton(
                            selected = group.equals(gValue, ignoreCase = true),
                            onClick = { group = gValue }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = gLabel, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                    } else {
                        onSave(name.trim(), teacher.trim(), group.trim())
                    }
                },
                modifier = Modifier.testTag("save_class_confirm_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onDelete != null) {
                    TextButton(
                        onClick = { showDeleteConfirm = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete Class")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
fun StudentFormDialog(
    initialStudent: Student? = null,
    suggestedRoll: String = "1",
    onDismiss: () -> Unit,
    onSave: (roll: String, name: String, fatherName: String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var roll by remember { mutableStateOf(initialStudent?.roll ?: suggestedRoll) }
    var name by remember { mutableStateOf(initialStudent?.name ?: "") }
    var fatherName by remember { mutableStateOf(initialStudent?.fatherName ?: "") }
    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialStudent == null) "Add New Student" else "Edit Student",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = roll,
                    onValueChange = { roll = it },
                    label = { Text("Roll Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_student_roll")
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text("Student Name *") },
                    isError = nameError,
                    supportingText = if (nameError) { { Text("Name is required") } } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_student_name")
                )

                OutlinedTextField(
                    value = fatherName,
                    onValueChange = { fatherName = it },
                    label = { Text("Father Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_student_father")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                    } else {
                        onSave(roll.trim(), name.trim(), fatherName.trim())
                    }
                },
                modifier = Modifier.testTag("save_student_confirm_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                if (onDelete != null) {
                    TextButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Remove")
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
