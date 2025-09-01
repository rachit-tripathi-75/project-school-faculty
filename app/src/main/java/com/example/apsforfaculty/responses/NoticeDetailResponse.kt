package com.example.apsforfaculty.responses

data class NoticeDetailResponse(
    val Msg: String,
    val type: String,
    val status: Int,
    val path: String,
    val data: List<NoticeData>
)

data class NoticeData(
    val id: String,
    val title: String,
    val date_from: String,
    val date: String,
    val type: String,
    val doc: String,
    val created_on: String,
    val status: String,
    val NoticeType: String,
    val sessionid: String
)