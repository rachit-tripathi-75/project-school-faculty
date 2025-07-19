package com.example.apsforfaculty.responses

data class LoginResponse(
    val Msg: String,
    val type: String,
    val status: Int,
    val data: Data
)

data class Data(
    val id: String,
    val name: String,
    val email: String,
    val emp_code: String,
    val phone: String?,
    val token: String
)