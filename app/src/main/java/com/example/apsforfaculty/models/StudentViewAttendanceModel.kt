package com.example.apsforfaculty.models

import com.example.apsforfaculty.R

// Step 1: Humne ek enum banaya hai jo teen possible states ko define karta hai.
enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    NOT_MARKED
}

// Step 2: 'isPresent: Boolean' ko 'status: AttendanceStatus' se badal diya hai.
// Maine 'name' ko 'studentName' bhi kar diya hai taaki code consistent rahe.
data class StudentViewAttendanceModel(
    val studentName: String,
    val studentId: String,
    val rollNumber: String,
    val status: AttendanceStatus
) {
    fun getDisplayName(): String {
        return studentName
    }

    fun getFormattedStudentId(): String {
        return "ID: $studentId"
    }

    // Step 3: Ab ye function 'when' ka istemal karke teeno states ke liye sahi text dega.
    fun getAttendanceStatus(): String {
        return when (status) {
            AttendanceStatus.PRESENT -> "Present"
            AttendanceStatus.ABSENT -> "Absent"
            AttendanceStatus.NOT_MARKED -> "Not Marked"
        }
    }

    // Step 4: Ye function teeno states ke liye alag-alag color dega.
    fun getStatusColor(): Int {
        return when (status) {
            AttendanceStatus.PRESENT -> android.R.color.holo_green_dark
            AttendanceStatus.ABSENT -> android.R.color.holo_red_dark
            AttendanceStatus.NOT_MARKED -> android.R.color.darker_gray // Not Marked ke liye naya color
        }
    }

    // Step 5: Ye function teeno states ke liye alag-alag background dega.
    fun getStatusBackground(): Int {
        return when (status) {
            AttendanceStatus.PRESENT -> R.drawable.status_present
            AttendanceStatus.ABSENT -> R.drawable.status_absent
            AttendanceStatus.NOT_MARKED -> R.drawable.status_notmarked // Not Marked ke liye naya background
        }
    }
}