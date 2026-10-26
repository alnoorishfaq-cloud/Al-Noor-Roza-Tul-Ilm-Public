package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "students",
    indices = [Index(value = ["classId"])]
)
data class Student(
    @PrimaryKey
    val id: String,
    val classId: String,
    val roll: String,
    val name: String,
    val fatherName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
