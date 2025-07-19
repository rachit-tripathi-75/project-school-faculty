package com.example.apsforfaculty.responses

data class ViewAttendanceResponse(
    val status: Int,
    val Msg: String,
    val data: List<AttendanceRecord>
)

data class AttendanceRecord(
    val student_id: String,
    val student_name: String,
    val enrollment: String,
    val attendance: String
)
