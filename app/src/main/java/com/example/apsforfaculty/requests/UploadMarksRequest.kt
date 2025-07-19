package com.example.apsforfaculty.requests

data class UploadMarksRequest(
    val exam_id: String,
    val section_id: String,
    val sst_id: String,
    val marks_data: List<UploadMarksData>
)

data class UploadMarksData(
    val student_id: String,
    val marks_obtained: String
)
