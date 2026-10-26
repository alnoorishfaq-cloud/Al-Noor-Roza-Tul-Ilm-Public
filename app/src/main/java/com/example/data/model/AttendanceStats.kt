package com.example.data.model

data class DailyAttendanceCount(
    val present: Int = 0,
    val absent: Int = 0,
    val leave: Int = 0,
    val notMarked: Int = 0,
    val total: Int = 0
) {
    val attendanceRate: Int
        get() = if (total > 0 && (present + absent + leave) > 0) {
            val marked = present + absent + leave
            if (marked > 0) ((present.toDouble() / marked.toDouble()) * 100).toInt() else 0
        } else 0
}

data class StudentAttendanceStat(
    val student: Student,
    val presentCount: Int,
    val absentCount: Int,
    val leaveCount: Int,
    val totalDays: Int
) {
    val percentage: Int
        get() {
            val total = presentCount + absentCount + leaveCount
            return if (total > 0) ((presentCount.toDouble() / total.toDouble()) * 100).toInt() else 0
        }
}

data class DayReportSummary(
    val date: String,
    val presentCount: Int,
    val absentCount: Int,
    val leaveCount: Int,
    val totalMarked: Int
)
