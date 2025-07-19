package com.example.apsforfaculty.responses

data class SectionWiseStudentListResponse(
    val Msg: String,
    val type: String,
    val status: Int,
    val data: List<SectionWiseStudent>
)

data class SectionWiseStudent(
    val sec_id: String,
    val category_id: String,
    val sectionname: String,
    val name: String,
    val email: String,
    val enrollment: String,
    val roll_no: String?, // Nullable as it can be null in the JSON
    val dob: String,
    val mobile: String?, // Nullable
    val father: String,
    val mother: String,
    val adhaar: String?, // Nullable
    val address: String,
    val AcademicYear: String,
    val sessionid: String,
    val class_id: String,
    val standard: String,
    val stu_id: String,
    val sidinc: String,
    val IsLeft: String,
    val father_no: String?, // Nullable
    val stu_img: String,
    val IsNew: String
)