package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SchoolClass
import com.example.data.model.Student

@Composable
fun StudentsScreen(
    currentClass: SchoolClass?,
    students: List<Student>,
    suggestedRoll: String,
    onAddStudent: (roll: String, name: String, fatherName: String) -> Unit,
    onUpdateStudent: (Student) -> Unit,
    onDeleteStudent: (String) -> Unit,
    onDeleteClass: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rollInput by remember(suggestedRoll) { mutableStateOf(suggestedRoll) }
    var nameInput by remember { mutableStateOf("") }
    var fatherInput by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    var studentToEdit by remember { mutableStateOf<Student?>(null) }
    var studentToDelete by remember { mutableStateOf<Student?>(null) }
    var showDeleteClassConfirm by remember { mutableStateOf(false) }

    // Dialog for Editing Student
    if (studentToEdit != null) {
        StudentFormDialog(
            initialStudent = studentToEdit,
            onDismiss = { studentToEdit = null },
            onSave = { r, n, f ->
                studentToEdit?.let {
                    onUpdateStudent(it.copy(roll = r, name = n, fatherName = f))
                }
                studentToEdit = null
            },
            onDelete = {
                studentToEdit?.let { onDeleteStudent(it.id) }
                studentToEdit = null
            }
        )
    }

    // Delete confirmation dialog
    if (studentToDelete != null) {
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            title = { Text("Remove Student?") },
            text = { Text("Are you sure you want to remove '${studentToDelete?.name}' from this class?") },
            confirmButton = {
                Button(
                    onClick = {
                        studentToDelete?.let { onDeleteStudent(it.id) }
                        studentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { studentToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Class confirmation
    if (showDeleteClassConfirm && currentClass != null) {
        AlertDialog(
            onDismissRequest = { showDeleteClassConfirm = false },
            title = { Text("Delete Class?") },
            text = { Text("Delete class '${currentClass.name}' and all its student records?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteClassConfirm = false
                        onDeleteClass()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteClassConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("students_screen")
    ) {
        // Add Student Form Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Add Student to ${currentClass?.name ?: "Class"}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Roll Input
                    OutlinedTextField(
                        value = rollInput,
                        onValueChange = { rollInput = it },
                        label = { Text("Roll") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .width(84.dp)
                            .testTag("add_student_roll_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Name Input
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = {
                            nameInput = it
                            nameError = false
                        },
                        label = { Text("Student Name *") },
                        isError = nameError,
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_student_name_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Father Name Input
                OutlinedTextField(
                    value = fatherInput,
                    onValueChange = { fatherInput = it },
                    label = { Text("Father Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_student_father_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                // Add Student Button
                Button(
                    onClick = {
                        if (nameInput.isBlank()) {
                            nameError = true
                        } else {
                            onAddStudent(rollInput, nameInput, fatherInput)
                            nameInput = ""
                            fatherInput = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_student_submit_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Student",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Student Roster Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Students Roster (${students.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Students List
        if (students.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCAD8D3))
            ) {
                Text(
                    text = "Type a roll number and student name above, then tap Add Student.",
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
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                itemsIndexed(students, key = { _, s -> s.id }) { index, student ->
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { studentToEdit = student },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0EAE6)),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = student.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                                if (student.fatherName.isNotBlank()) {
                                    Text(
                                        text = "Father: ${student.fatherName}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }

                            IconButton(
                                onClick = { studentToEdit = student },
                                modifier = Modifier.size(36.dp).testTag("edit_student_${student.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit student",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = { studentToDelete = student },
                                modifier = Modifier.size(36.dp).testTag("delete_student_${student.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete student",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showDeleteClassConfirm = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .testTag("delete_class_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delete This Class", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
