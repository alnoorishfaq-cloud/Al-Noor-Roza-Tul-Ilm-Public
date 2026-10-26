package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceStatus
import com.example.data.model.DailyAttendanceCount
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import com.example.data.model.StudentAttendanceStat
import com.example.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AttendanceRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = AttendanceRepository(db.attendanceDao())
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val _selectedGroup = MutableStateFlow("") // "" = All, "Hifaz ul Quran", "Nazira"
    val selectedGroup: StateFlow<String> = _selectedGroup.asStateFlow()

    private val _selectedClassId = MutableStateFlow<String?>(null)
    val selectedClassId: StateFlow<String?> = _selectedClassId.asStateFlow()

    private val _selectedDate = MutableStateFlow(dateFormat.format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val allClasses: StateFlow<List<SchoolClass>> = repository.allClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudents: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Group student counts: Map of group name to student count
    val groupCounts: StateFlow<Map<String, Int>> = combine(allClasses, allStudents) { classes, students ->
        val classGroupMap = classes.associate { it.id to it.group }
        var allCount = students.size
        var hifzCount = 0
        var naziraCount = 0
        students.forEach { s ->
            val g = classGroupMap[s.classId].orEmpty()
            if (g.contains("Hifz", ignoreCase = true) || g.contains("Hifaz", ignoreCase = true)) {
                hifzCount++
            } else if (g.contains("Nazira", ignoreCase = true)) {
                naziraCount++
            }
        }
        mapOf("" to allCount, "Hifaz ul Quran" to hifzCount, "Nazira" to naziraCount)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val filteredClasses: StateFlow<List<SchoolClass>> = combine(allClasses, _selectedGroup) { classes, group ->
        if (group.isBlank()) classes
        else classes.filter { it.group.equals(group, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentClass: StateFlow<SchoolClass?> = combine(allClasses, _selectedClassId, filteredClasses) { all, selectedId, filtered ->
        val found = all.find { it.id == selectedId }
        if (found != null && (selectedId in filtered.map { it.id })) {
            found
        } else {
            val fallback = filtered.firstOrNull() ?: all.firstOrNull()
            if (fallback != null && _selectedClassId.value != fallback.id) {
                _selectedClassId.value = fallback.id
            }
            fallback
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current students for selected class
    val currentStudents: StateFlow<List<Student>> = _selectedClassId.flatMapLatest { classId ->
        if (classId != null) repository.getStudentsForClass(classId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current attendance records for selected class & date
    val currentAttendance: StateFlow<List<AttendanceRecord>> = combine(_selectedClassId, _selectedDate) { classId, date ->
        Pair(classId, date)
    }.flatMapLatest { (classId, date) ->
        if (classId != null) repository.getAttendanceForClassAndDate(classId, date) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All attendance records for class across all dates (for reports)
    val allClassAttendance: StateFlow<List<AttendanceRecord>> = _selectedClassId.flatMapLatest { classId ->
        if (classId != null) repository.getAllAttendanceForClass(classId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Daily count: Present, Absent, Leave, etc.
    val dailyCount: StateFlow<DailyAttendanceCount> = combine(currentStudents, currentAttendance) { students, records ->
        repository.buildDailyCount(students, records)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyAttendanceCount())

    // Student stats for report
    val studentStats: StateFlow<List<StudentAttendanceStat>> = combine(currentStudents, allClassAttendance) { students, records ->
        repository.buildStudentStats(students, records)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dates with recorded attendance for current class
    val recordedDates: StateFlow<List<String>> = allClassAttendance.combine(currentStudents) { records, students ->
        val studentIds = students.map { it.id }.toSet()
        records.filter { it.studentId in studentIds }
            .map { it.date }
            .distinct()
            .sortedDescending()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setGroupFilter(group: String) {
        _selectedGroup.value = group
        // If current class not in this group, switch to first available
        val available = if (group.isBlank()) allClasses.value else allClasses.value.filter { it.group.equals(group, ignoreCase = true) }
        _selectedClassId.value = available.firstOrNull()?.id
    }

    fun selectClass(classId: String) {
        _selectedClassId.value = classId
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun resetDateToToday() {
        _selectedDate.value = dateFormat.format(Date())
    }

    fun previousDate() {
        shiftDate(-1)
    }

    fun nextDate() {
        shiftDate(1)
    }

    private fun shiftDate(days: Int) {
        try {
            val cal = Calendar.getInstance()
            val parsed = dateFormat.parse(_selectedDate.value)
            if (parsed != null) {
                cal.time = parsed
                cal.add(Calendar.DAY_OF_YEAR, days)
                _selectedDate.value = dateFormat.format(cal.time)
            }
        } catch (_: Exception) {}
    }

    fun toggleAttendance(studentId: String, status: AttendanceStatus) {
        val classId = _selectedClassId.value ?: return
        val date = _selectedDate.value
        viewModelScope.launch {
            repository.toggleAttendance(date, studentId, classId, status)
        }
    }

    fun markAllPresent() {
        val classId = _selectedClassId.value ?: return
        val date = _selectedDate.value
        val students = currentStudents.value
        if (students.isEmpty()) return
        viewModelScope.launch {
            repository.markAllPresent(date, classId, students)
        }
    }

    fun addClass(name: String, teacher: String, group: String, onComplete: (String) -> Unit = {}) {
        viewModelScope.launch {
            val newId = repository.addClass(name, teacher, group)
            _selectedClassId.value = newId
            onComplete(newId)
        }
    }

    fun updateClass(schoolClass: SchoolClass) {
        viewModelScope.launch {
            repository.updateClass(schoolClass)
        }
    }

    fun deleteClass(classId: String) {
        viewModelScope.launch {
            repository.deleteClass(classId)
            val remaining = allClasses.value.filter { it.id != classId }
            _selectedClassId.value = remaining.firstOrNull()?.id
        }
    }

    fun addStudent(roll: String, name: String, fatherName: String, onComplete: () -> Unit = {}) {
        val classId = _selectedClassId.value ?: return
        viewModelScope.launch {
            repository.addStudent(classId, roll, name, fatherName)
            onComplete()
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            repository.updateStudent(student)
        }
    }

    fun deleteStudent(studentId: String) {
        viewModelScope.launch {
            repository.deleteStudent(studentId)
        }
    }

    fun getNextRollNumber(): String {
        val students = currentStudents.value
        val maxRoll = students.mapNotNull { it.roll.toIntOrNull() }.maxOrNull() ?: 0
        return (maxRoll + 1).toString()
    }

    fun getShareableDailyReport(): String {
        val currentCls = currentClass.value ?: return ""
        val students = currentStudents.value
        val records = currentAttendance.value
        val date = _selectedDate.value
        return repository.formatDailyReportText(currentCls, date, students, records)
    }

    fun getShareableStudentReport(): String {
        val currentCls = currentClass.value ?: return ""
        val stats = studentStats.value
        val sb = StringBuilder()
        sb.append("🏫 *Al Noor Roza Tul Ilm Public High School*\n")
        sb.append("📍 Manki, Swabi\n")
        sb.append("📊 *Overall Attendance Summary - ${currentCls.name}*\n\n")
        if (currentCls.teacher.isNotBlank()) sb.append("👨‍🏫 Teacher: ${currentCls.teacher}\n")
        sb.append("----------------------------\n")
        stats.forEach { s ->
            val father = if (s.student.fatherName.isNotBlank()) " (S/O ${s.student.fatherName})" else ""
            sb.append("• [Roll ${s.student.roll}] ${s.student.name}$father: ${s.percentage}% (P: ${s.presentCount}, A: ${s.absentCount}, L: ${s.leaveCount})\n")
        }
        sb.append("\n----------------------------\n")
        sb.append("Prepared by: Muhammad Ishfaq\nPhone: 03149768207")
        return sb.toString()
    }
}
