package com.example.apsforfaculty.models

import java.util.regex.Pattern

data class PasswordRequirement(
    val text: String,
    val pattern: Pattern
)