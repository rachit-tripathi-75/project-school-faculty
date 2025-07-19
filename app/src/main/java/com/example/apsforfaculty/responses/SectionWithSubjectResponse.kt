package com.example.apsforfaculty.responses

data class SectionWithSubjectResponse(
    val status: Int,
    val Msg: String,
    val data: List<Section>
)

data class Section(
    val section_id: String,
    val section_name: String,
    val subjects: List<Subject>
)

data class Subject(
    val sub_id: String,
    val sub_name: String,
    val sub_type: String
)