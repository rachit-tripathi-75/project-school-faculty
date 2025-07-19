package com.example.apsforfaculty.responses

data class TeacherDetailsResponse(
    val status: Int,
    val Msg: String,
    val data: TeacherDetails
)

data class TeacherDetails(
    val id: String,
    val emp_code: String,
    val username: String,
    val name: String,
    val father_name: String,
    val mother_name: String,
    val gender: String,
    val dob: String,
    val selfpic: String,
    val blood_group: String,
    val phone: String,
    val homecontact: String,
    val email: String,
    val pass: String,
    val address: String,
    val designation: String?, // nullable
    val per_address: String,
    val adhaar: String,
    val pan: String,
    val bank: String,
    val accountholdername: String,
    val bankaccountnumber: String,
    val ifsc: String,
    val joindate: String,
    val role: String,
    val type: String,
    val academic_year: String,
    val adharfile: String,
    val panfile: String,
    val teachingqualification: String,
    val bankpassbook: String,
    val subject_id: String,
    val status: String,
    val created_at: String,
    val created_by: String,
    val image_url: String
)
