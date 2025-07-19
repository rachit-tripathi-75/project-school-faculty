package com.example.apsforfaculty.classes

import androidx.annotation.HalfFloat
import com.example.apsforfaculty.requests.SectionWithSubjectRequest
import com.example.apsforfaculty.requests.TimeTableRequest
import com.example.apsforfaculty.requests.UploadMarksRequest
import com.example.apsforfaculty.responses.AssignedSubjectResponse
import com.example.apsforfaculty.responses.ExamListResponse
import com.example.apsforfaculty.responses.LoginResponse
import com.example.apsforfaculty.responses.MarkStudentAttendanceResponse
import com.example.apsforfaculty.responses.SectionWiseStudentListResponse
import com.example.apsforfaculty.responses.SectionWithSubjectResponse
import com.example.apsforfaculty.responses.StudentListForMarkingAttendanceResponse
import com.example.apsforfaculty.responses.TeacherDetailsResponse
import com.example.apsforfaculty.responses.TimeTableResponse
import com.example.apsforfaculty.responses.UploadMarksResponse
import com.example.apsforfaculty.responses.UploadWorkResponse
import com.example.apsforfaculty.responses.ViewAttendanceResponse
import com.example.apsforfaculty.responses.ViewMarksResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Streaming

class ApiServices {

    interface GetDetailedInformationApiService {
        @FormUrlEncoded
        @POST("API/getLoggedInTeacherDetails")
        fun getDetailedInformation(
            @Header("Content-Type") contentType: String,
            @Header("Cookie") cookie: String,
            @Field("teacher_id") teacherId: String
        ): Call<TeacherDetailsResponse>
    }

    interface LoginApiService {
        @FormUrlEncoded
        @POST("API/teacherLogin")
        fun login(
            @Field("email") email: String,
            @Field("password") password: String
        ): Call<LoginResponse>
    }

    interface TeacherTimeTableApiService {
        @POST("API/getTeachertimetable")
        fun getTimeTable(
            @Header("Content-Type") contentType: String,
            @Header("Cookie") cookie: String,
            @Body timeTableRequest: TimeTableRequest
        ): Call<TimeTableResponse>
    }

    interface GetStudentListForMarkingAttendanceApiService {
        @FormUrlEncoded
        @POST("API/getStudentListForAttendance")
        fun getStudentListForMarkingAttendance(
            @Header("Content-Type") contentType: String,
            @Header("Cookie") cookie: String,
            @Field("teacher_id") teacherId: String
        ): Call<StudentListForMarkingAttendanceResponse>
    }

    interface MarkStudentAttendanceApiService {
        @FormUrlEncoded
        @POST("API/markStudentAttendance")
        fun postAttendance(
            @Header("Content-Type") contentType: String,
            @Header("Cookie") cookie: String,
            @Field("teacher_id") teacherId: String,
            @Field("date") date: String,
            @Field("attendance") attendance: String
        ): Call<MarkStudentAttendanceResponse>
    }

    interface ViewStudentAttendanceApiService {
        @FormUrlEncoded
        @POST("API/getStudentAttendance")
        fun getAttendance(
            @Header("Content-Type") contentType: String,
            @Header("Cookie") cookie: String,
            @Field("teacher_id") sectionId: String,
            @Field("date") date: String
        ): Call<ViewAttendanceResponse>
    }

    interface TeacherSectionsWithSubjectsApiService {
        @POST("API/getTeacherSectionsWithSubjects")
        fun getSectionWithSubjects(
            @Header("Content-Type") contentType: String,
            @Header("Cookie") cookie: String,
            @Body sectionWithSubjectsRequest: SectionWithSubjectRequest
        ): Call<SectionWithSubjectResponse>
    }

    interface ExamListApiService {
        @POST("API/getExamList")
        fun getExamList(
            @Header("Cookie") cookie: String,
        ): Call<ExamListResponse>
    }

    interface SectionWiseStudentListDetailApiService {
        @FormUrlEncoded
        @POST("API/getSectionStudentDetail")
        fun getSectionWiseStudents(
            @Header("Content-Type") contentType: String,
            @Header("Cookie") cookie: String,
            @Field("sec_id") sectionId: String
        ): Call<SectionWiseStudentListResponse>
    }

    interface UploadMarksApiService {
        @POST("API/uploadMarks")
        fun uploadMarks(
            @Header("Content-Type") contentType: String,
            @Header("Cookie") cookie: String,
            @Body uploadMarksRequest: UploadMarksRequest
        ): Call<UploadMarksResponse>
    }

    interface ViewMarksApiService {
        @FormUrlEncoded
        @POST("API/viewMarks")
        fun viewMarks(
            @Header("Content-Type") contentType: String,
            @Header("Cookie") cookie: String,
            @Field("exam_id") examId: String,
            @Field("section_id") sectionId: String,
            @Field("sst_id") sstId: String
        ): Call<ViewMarksResponse>
    }

    interface GetAssignedSubjectsApiService {
        @FormUrlEncoded
        @POST("API/getAssignedSubjects")
        fun getAssignedSubjects(
            @Header("Content-Type") contentType: String,
            @Header("Cookie") cookie: String,
            @Field("teacher_id") teacherId: String
        ): Call<AssignedSubjectResponse>
    }

    interface UploadWorkApiService {
        @Multipart
        @POST("API/uploadNotes")
        suspend fun uploadWork(
            @Header("Cookie") cookie: String,
            @Part("sst_id") sstId: RequestBody,
            @Part("teacher_id") teacherId: RequestBody,
            @Part file: MultipartBody.Part
        ): retrofit2.Response<UploadWorkResponse>
    }


}