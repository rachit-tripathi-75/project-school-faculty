package com.example.apsforfaculty.models

data class Student(
    val id: String,
    val name: String,
    val rollNumber: String,
    var isPresent: Boolean = false
)

data class AttendanceRecord(
    val studentId: Int,
    val date: String,
    val isPresent: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class AttendanceSession(
    val sessionId: String,
    val className: String,
    val date: String,
    val teacherId: String,
    val attendanceRecords: List<AttendanceRecord>
)