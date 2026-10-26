package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class AttendanceStatus(val code: String, val displayName: String) {
    PRESENT("P", "Present"),
    ABSENT("A", "Absent"),
    LEAVE("L", "Leave");

    companion object {
        fun fromCode(code: String?): AttendanceStatus? {
            return entries.find { it.code.equals(code, ignoreCase = true) }
        }
    }
}

@Entity(
    tableName = "attendance_records",
    indices = [
        Index(value = ["date", "classId"]),
        Index(value = ["studentId"])
    ]
)
data class AttendanceRecord(
    @PrimaryKey
    val id: String, // e.g. "${date}_${studentId}"
    val date: String, // YYYY-MM-DD
    val studentId: String,
    val classId: String,
    val status: String, // "P", "A", "L"
    val timestamp: Long = System.currentTimeMillis()
)
