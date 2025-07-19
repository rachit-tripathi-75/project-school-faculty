package com.example.apsforfaculty.models

// this is used in uploading marks of student.........

data class UploadMarksSubject(
    val id: Int,
    val section: String,
    val teacherName: String,
    val subjectName: String
)

data class UploadMarksStudent(
    val id: Int,
    val sidIncNumber: String,
    val name: String,
    val fatherName: String,
    var marks: Int = 0
)

data class UploadMarksExam(
    val id: Int,
    val name: String
)

data class UploadMarksStudentMark(
    val studentId: Int,
    val subjectId: Int,
    val examId: Int,
    val marks: Int,
    val createdAt: String = System.currentTimeMillis().toString()
)
