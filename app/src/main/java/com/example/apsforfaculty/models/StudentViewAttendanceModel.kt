package com.example.apsforfaculty.models

import com.example.apsforfaculty.R


data class StudentViewAttendanceModel(
    val name: String,
    val studentId: String,
    val rollNumber: String,
    val isPresent: Boolean
) {
    fun getDisplayName(): String {
        return name
    }

    fun getFormattedStudentId(): String {
        return "ID: $studentId"
    }

    fun getAttendanceStatus(): String {
        return if (isPresent) "Present" else "Absent"
    }

    fun getStatusColor(): Int {
        return if (isPresent) {
            android.R.color.holo_green_dark
        } else {
            android.R.color.holo_red_dark
        }
    }

    fun getStatusBackground(): Int {
        return if (isPresent) {
            R.drawable.status_present
        } else {
            R.drawable.status_absent
        }
    }
}