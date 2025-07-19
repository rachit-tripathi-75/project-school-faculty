package com.example.apsforfaculty.responses

import com.google.gson.annotations.SerializedName

data class TimeTableResponse(
    @SerializedName("Msg")
    val msg: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("data")
    val data: List<TeacherTimetableItem>
)

data class TeacherTimetableItem(
    @SerializedName("roomId")
    val roomId: String,
    @SerializedName("drag_id")
    val dragId: String,
    @SerializedName("t_p_id")
    val tpId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("sub_name")
    val subName: String,
    @SerializedName("sub_type")
    val subType: String,
    @SerializedName("rm")
    val rm: String,
    @SerializedName("main_sec_name")
    val mainSecName: String,
    @SerializedName("stime_table_date")
    val stimeTableDate: String,
    @SerializedName("ttime_table_date")
    val ttimeTableDate: String
)