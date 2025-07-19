package com.example.apsforfaculty.responses

data class ExamListResponse(
    val status: Int,
    val Msg: String,
    val data: List<ExamListExam>
)

data class ExamListExam(
    val id: String,
    val name: String,
    val max_mark: String,
    val created_by: String,
    val created_on: String,
    val status: String,
    val sessionid: String
)