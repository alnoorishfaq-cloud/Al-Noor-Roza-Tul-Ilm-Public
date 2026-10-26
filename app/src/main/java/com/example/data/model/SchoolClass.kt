package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classes")
data class SchoolClass(
    @PrimaryKey
    val id: String,
    val name: String,
    val teacher: String = "",
    val group: String = "", // e.g. "Hifaz ul Quran", "Nazira", or ""
    val createdAt: Long = System.currentTimeMillis()
)
