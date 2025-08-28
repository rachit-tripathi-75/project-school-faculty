package com.example.apsforfaculty.responses

import com.google.gson.annotations.SerializedName

data class ExamListResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("Msg") val msg: String,
    @SerializedName("data") val data: List<ExamListExam>
)

data class ExamListExam(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("max_mark") val maxMark: String,
    @SerializedName("created_by") val createdBy: String,
    @SerializedName("created_on") val createdOn: String,
    @SerializedName("status") val status: String,
    @SerializedName("sessionId") val sessionId: String, // Corrected from 'sessionid' to match JSON
    @SerializedName("academicsession") val academicSession: String // Added missing field
)