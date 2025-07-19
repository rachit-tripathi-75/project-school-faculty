package com.example.apsforfaculty.responses

import com.google.gson.annotations.SerializedName


data class StudentListForMarkingAttendanceResponse(
    val status: Int,
    val Msg: String,
    val data: List<StudentData>
)


data class StudentData(
    val id: String,
    @SerializedName("category_id")
    val categoryId: String,
    @SerializedName("ref_id")
    val refId: String?,
    val name: String,
    val email: String,
    @SerializedName("stu_id")
    val stuId: String,
    val enrollment: String,
    val enrollment2: String,
    @SerializedName("roll_no")
    val rollNo: String?,
    val dob: String,
    val mobile: String?,
    val pass: String,
    val gender: String,
    val father: String,
    val mother: String,
    @SerializedName("father_no")
    val fatherNo: String?,
    val emergency: String?,
    val category: String?,
    @SerializedName("sub_caste")
    val subCaste: String?,
    val religion: String?,
    val nationality: String?,
    val city: String?,
    val pincode: String?,
    val state: String?,
    @SerializedName("local_address")
    val localAddress: String?,
    @SerializedName("doc_id")
    val docId: String,
    @SerializedName("self_img")
    val selfImg: String,
    @SerializedName("parent_img")
    val parentImg: String?,
    @SerializedName("self_aadhaar")
    val selfAadhaar: String?,
    val driver: String?,
    @SerializedName("birth_certificate")
    val birthCertificate: String,
    val tc: String,
    @SerializedName("report_card")
    val reportCard: String,
    @SerializedName("parents_aadhaar")
    val parentsAadhaar: String,
    val adhaar: String?,
    @SerializedName("class")
    val studentClass: String,
    val address: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("created_by")
    val createdBy: String,
    val status: String,
    val type: String,
    val enqtype: String,
    val regstatus: String,
    val enqstatus: String,
    @SerializedName("status_changed")
    val statusChanged: String,
    @SerializedName("changed_by")
    val changedBy: String,
    @SerializedName("changed_on")
    val changedOn: String?,
    val remark: String?,
    @SerializedName("Year")
    val year: String,
    @SerializedName("IsNew")
    val isNew: String,
    @SerializedName("IsLeft")
    val isLeft: String,
    val policytype: String,
    @SerializedName("school_id")
    val schoolId: String,
    val info: String?,
    @SerializedName("FCM")
    val fcm: String,
    @SerializedName("sec_id")
    val secId: String
)