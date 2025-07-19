package com.example.apsforfaculty.requests

data class TimeTableRequest(
    val teacher_id: String,
    val day: String,
    val date: String
)