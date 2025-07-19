package com.example.apsforfaculty.responses

data class ViewMarksResponse(
    val Msg: String,
    val type: String,
    val status: Int,
    val data: List<StudentViewedMark>
)

data class StudentViewedMark(
    val student_id: String,
    val name: String,
    val enrollment: String,
    val mark: String
)
