package com.example.apsforfaculty.responses

data class MarkStudentAttendanceResponse(
    val status: Int,
    val Msg: String,
    val data: List<Any>
)
