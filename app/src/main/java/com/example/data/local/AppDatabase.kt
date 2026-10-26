package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AttendanceRecord
import com.example.data.model.SchoolClass
import com.example.data.model.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [SchoolClass::class, Student::class, AttendanceRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "al_noor_attendance.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed default school data on creation
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { seedInitialData(it.attendanceDao()) }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(dao: AttendanceDao) {
            val class1 = SchoolClass(
                id = "c_hifz_1",
                name = "Class 1 (Hifz)",
                teacher = "Muhammad Ishfaq",
                group = "Hifaz ul Quran"
            )
            val class2 = SchoolClass(
                id = "c_nazira_1",
                name = "Nazira Section A",
                teacher = "Qari Noor",
                group = "Nazira"
            )
            val class3 = SchoolClass(
                id = "c_general_5a",
                name = "Class 5-A",
                teacher = "Sir Tariq",
                group = ""
            )
            dao.insertClasses(listOf(class1, class2, class3))

            val studentsClass1 = listOf(
                Student(id = "s_1", classId = class1.id, roll = "1", name = "Ahmed Khan", fatherName = "Gul Khan"),
                Student(id = "s_2", classId = class1.id, roll = "2", name = "Bilal Shah", fatherName = "Noor Shah"),
                Student(id = "s_3", classId = class1.id, roll = "3", name = "Usman Ali", fatherName = "Tariq Ali"),
                Student(id = "s_4", classId = class1.id, roll = "4", name = "Hamza Khan", fatherName = "Javed Khan"),
                Student(id = "s_5", classId = class1.id, roll = "5", name = "Abdullah Jan", fatherName = "Sher Jan"),
                Student(id = "s_6", classId = class1.id, roll = "6", name = "Abu Bakar", fatherName = "Ibrahim Khan")
            )

            val studentsClass2 = listOf(
                Student(id = "s_7", classId = class2.id, roll = "1", name = "Muhammad Zaid", fatherName = "Rashid Khan"),
                Student(id = "s_8", classId = class2.id, roll = "2", name = "Huzaifa Noor", fatherName = "Noor ul Amin"),
                Student(id = "s_9", classId = class2.id, roll = "3", name = "Saad Liaqat", fatherName = "Liaqat Ali")
            )

            val studentsClass3 = listOf(
                Student(id = "s_10", classId = class3.id, roll = "1", name = "Farhan Ahmad", fatherName = "Mushtaq Ahmad"),
                Student(id = "s_11", classId = class3.id, roll = "2", name = "Daniyal Khan", fatherName = "Anwar Khan")
            )

            dao.insertStudents(studentsClass1 + studentsClass2 + studentsClass3)

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val initialAttendance = listOf(
                AttendanceRecord(id = "${today}_s_1", date = today, studentId = "s_1", classId = class1.id, status = "P"),
                AttendanceRecord(id = "${today}_s_2", date = today, studentId = "s_2", classId = class1.id, status = "P"),
                AttendanceRecord(id = "${today}_s_3", date = today, studentId = "s_3", classId = class1.id, status = "A"),
                AttendanceRecord(id = "${today}_s_4", date = today, studentId = "s_4", classId = class1.id, status = "P"),
                AttendanceRecord(id = "${today}_s_5", date = today, studentId = "s_5", classId = class1.id, status = "L"),
                AttendanceRecord(id = "${today}_s_6", date = today, studentId = "s_6", classId = class1.id, status = "P")
            )
            dao.insertOrUpdateAttendanceList(initialAttendance)
        }
    }
}
