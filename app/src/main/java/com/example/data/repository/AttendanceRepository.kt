package com.example.data.repository

import com.example.data.local.AttendanceDao
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.DailyAttendanceCount
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.StudentAttendanceStat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.UUID

class AttendanceRepository(private val dao: AttendanceDao) {

    val allClasses: Flow<List<SchoolClass>> = dao.getAllClassesFlow()
    val allStudents: Flow<List<Student>> = dao.getAllStudentsFlow()

    fun getStudentsForClass(classId: String): Flow<List<Student>> {
        return dao.getStudentsByClassFlow(classId)
    }

    fun getAttendanceForClassAndDate(classId: String, date: String): Flow<List<AttendanceRecord>> {
        return dao.getAttendanceByClassAndDateFlow(classId, date)
    }

    fun getAllAttendanceForClass(classId: String): Flow<List<AttendanceRecord>> {
        return dao.getAllAttendanceByClassFlow(classId)
    }

    suspend fun addClass(name: String, teacher: String, group: String): String {
        val id = "c_" + UUID.randomUUID().toString().take(8)
        val schoolClass = SchoolClass(
            id = id,
            name = name.trim(),
            teacher = teacher.trim(),
            group = group.trim()
        )
        dao.insertClass(schoolClass)
        return id
    }

    suspend fun updateClass(schoolClass: SchoolClass) {
        dao.updateClass(schoolClass)
    }

    suspend fun deleteClass(classId: String) {
        dao.deleteAttendanceByClass(classId)
        dao.deleteStudentsByClass(classId)
        dao.deleteClassById(classId)
    }

    suspend fun addStudent(classId: String, roll: String, name: String, fatherName: String): String {
        val id = "s_" + UUID.randomUUID().toString().take(8)
        val student = Student(
            id = id,
            classId = classId,
            roll = roll.trim(),
            name = name.trim(),
            fatherName = fatherName.trim()
        )
        dao.insertStudent(student)
        return id
    }

    suspend fun updateStudent(student: Student) {
        dao.updateStudent(student)
    }

    suspend fun deleteStudent(studentId: String) {
        dao.deleteAttendanceByStudent(studentId)
        dao.deleteStudentById(studentId)
    }

    suspend fun setAttendance(date: String, studentId: String, classId: String, status: AttendanceStatus) {
        val id = "${date}_${studentId}"
        val record = AttendanceRecord(
            id = id,
            date = date,
            studentId = studentId,
            classId = classId,
            status = status.code
        )
        dao.insertOrUpdateAttendance(record)
    }

    suspend fun toggleAttendance(date: String, studentId: String, classId: String, status: AttendanceStatus) {
        val id = "${date}_${studentId}"
        val existing = dao.getAttendanceByClassAndDate(classId, date).find { it.studentId == studentId }
        if (existing?.status == status.code) {
            // Deselect / Remove
            dao.deleteAttendanceById(id)
        } else {
            setAttendance(date, studentId, classId, status)
        }
    }

    suspend fun markAllPresent(date: String, classId: String, students: List<Student>) {
        val records = students.map { student ->
            AttendanceRecord(
                id = "${date}_${student.id}",
                date = date,
                studentId = student.id,
                classId = classId,
                status = AttendanceStatus.PRESENT.code
            )
        }
        dao.insertOrUpdateAttendanceList(records)
    }

    fun buildDailyCount(students: List<Student>, records: List<AttendanceRecord>): DailyAttendanceCount {
        var p = 0
        var a = 0
        var l = 0
        val recordMap = records.associateBy { it.studentId }
        students.forEach { s ->
            when (recordMap[s.id]?.status) {
                AttendanceStatus.PRESENT.code -> p++
                AttendanceStatus.ABSENT.code -> a++
                AttendanceStatus.LEAVE.code -> l++
            }
        }
        val marked = p + a + l
        val notMarked = (students.size - marked).coerceAtLeast(0)
        return DailyAttendanceCount(
            present = p,
            absent = a,
            leave = l,
            notMarked = notMarked,
            total = students.size
        )
    }

    fun buildStudentStats(students: List<Student>, allRecords: List<AttendanceRecord>): List<StudentAttendanceStat> {
        val recordsByStudent = allRecords.groupBy { it.studentId }
        val allDates = allRecords.map { it.date }.distinct().size
        return students.map { student ->
            val stRecords = recordsByStudent[student.id] ?: emptyList()
            var p = 0
            var a = 0
            var l = 0
            stRecords.forEach { r ->
                when (r.status) {
                    AttendanceStatus.PRESENT.code -> p++
                    AttendanceStatus.ABSENT.code -> a++
                    AttendanceStatus.LEAVE.code -> l++
                }
            }
            StudentAttendanceStat(
                student = student,
                presentCount = p,
                absentCount = a,
                leaveCount = l,
                totalDays = allDates
            )
        }
    }

    fun formatDailyReportText(
        schoolClass: SchoolClass,
        date: String,
        students: List<Student>,
        records: List<AttendanceRecord>
    ): String {
        val count = buildDailyCount(students, records)
        val recordMap = records.associateBy { it.studentId }
        val sb = StringBuilder()
        sb.append("🏫 *Al Noor Roza Tul Ilm Public High School*\n")
        sb.append("📍 Manki, Swabi\n")
        sb.append("📋 *Daily Attendance Report*\n\n")
        sb.append("📅 Date: $date\n")
        sb.append("🏷️ Class: ${schoolClass.name}")
        if (schoolClass.group.isNotBlank()) sb.append(" [${schoolClass.group}]")
        sb.append("\n")
        if (schoolClass.teacher.isNotBlank()) sb.append("👨‍🏫 Teacher: ${schoolClass.teacher}\n")
        sb.append("----------------------------\n")
        sb.append("✅ Present: ${count.present} | ❌ Absent: ${count.absent} | ⏳ Leave: ${count.leave}\n")
        sb.append("📊 Total Students: ${count.total} (Rate: ${count.attendanceRate}%)\n")
        sb.append("----------------------------\n")
        students.forEach { s ->
            val status = when (recordMap[s.id]?.status) {
                AttendanceStatus.PRESENT.code -> "✅ Present"
                AttendanceStatus.ABSENT.code -> "❌ Absent"
                AttendanceStatus.LEAVE.code -> "⏳ Leave"
                else -> "⚪ Not Marked"
            }
            val father = if (s.fatherName.isNotBlank()) " (S/O ${s.fatherName})" else ""
            sb.append("• [Roll ${s.roll}] ${s.name}$father: $status\n")
        }
        sb.append("\n----------------------------\n")
        sb.append("Prepared by: Muhammad Ishfaq\nPhone: 03149768207")
        return sb.toString()
    }
}
