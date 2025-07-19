package com.example.apsforfaculty.responses

data class UploadWorkResponse(
    val Msg: String,
    val type: String,
    val status: Int,
    val data: UploadedWorkData?
)

data class UploadedWorkData(
    val sst_id: String,
    val sec_id: String,
    val sub_id: String,
    val subject: String,
    val file_note: String,
    val sessionid: String,
    val created_by: String,
    val created_at: String
)