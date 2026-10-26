package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AttendanceRecord
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    // --- Classes ---
    @Query("SELECT * FROM classes ORDER BY createdAt ASC")
    fun getAllClassesFlow(): Flow<List<SchoolClass>>

    @Query("SELECT * FROM classes WHERE id = :classId LIMIT 1")
    suspend fun getClassById(classId: String): SchoolClass?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(schoolClass: SchoolClass)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClasses(classes: List<SchoolClass>)

    @Update
    suspend fun updateClass(schoolClass: SchoolClass)

    @Delete
    suspend fun deleteClass(schoolClass: SchoolClass)

    @Query("DELETE FROM classes WHERE id = :classId")
    suspend fun deleteClassById(classId: String)

    // --- Students ---
    @Query("SELECT * FROM students WHERE classId = :classId ORDER BY CAST(roll AS INTEGER) ASC, roll ASC")
    fun getStudentsByClassFlow(classId: String): Flow<List<Student>>

    @Query("SELECT * FROM students")
    fun getAllStudentsFlow(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE classId = :classId")
    suspend fun getStudentsByClass(classId: String): List<Student>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM students WHERE id = :studentId")
    suspend fun deleteStudentById(studentId: String)

    @Query("DELETE FROM students WHERE classId = :classId")
    suspend fun deleteStudentsByClass(classId: String)

    // --- Attendance Records ---
    @Query("SELECT * FROM attendance_records WHERE classId = :classId AND date = :date")
    fun getAttendanceByClassAndDateFlow(classId: String, date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE classId = :classId AND date = :date")
    suspend fun getAttendanceByClassAndDate(classId: String, date: String): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records WHERE classId = :classId")
    fun getAllAttendanceByClassFlow(classId: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId")
    fun getAttendanceForStudentFlow(studentId: String): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttendance(record: AttendanceRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttendanceList(records: List<AttendanceRecord>)

    @Query("DELETE FROM attendance_records WHERE id = :id")
    suspend fun deleteAttendanceById(id: String)

    @Query("DELETE FROM attendance_records WHERE studentId = :studentId")
    suspend fun deleteAttendanceByStudent(studentId: String)

    @Query("DELETE FROM attendance_records WHERE classId = :classId")
    suspend fun deleteAttendanceByClass(classId: String)
}
