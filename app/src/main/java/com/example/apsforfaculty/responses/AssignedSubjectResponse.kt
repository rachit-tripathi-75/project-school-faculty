package com.example.apsforfaculty.responses

data class AssignedSubjectResponse(
    val Msg: String,
    val type: String,
    val status: Int,
    val data: List<AssignedSubjectData>
)

data class AssignedSubjectData(
    val sst_id: String,
    val section_id: String,
    val sub_id: String,
    val sub_name: String,
    val main_sec_name: String
)
