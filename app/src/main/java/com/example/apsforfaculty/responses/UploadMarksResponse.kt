package com.example.apsforfaculty.responses

data class UploadMarksResponse(
    val Msg: String,
    val type: String,
    val status: Int,
    val data: UploadMarksResult
)

data class UploadMarksResult(
    val success: Int,
    val errors: List<String>
)
