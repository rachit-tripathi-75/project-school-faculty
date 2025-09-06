package com.example.apsforfaculty.classes

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object ApiClient {
    const val BASE_URL = "https://erp.apschitrakoot.in/"

    val teacherDetailsInstance: ApiServices.GetDetailedInformationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices.GetDetailedInformationApiService::class.java)
    }

    val loginInstance: ApiServices.LoginApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices.LoginApiService::class.java)
    }

    val timeTableInstance: ApiServices.TeacherTimeTableApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices.TeacherTimeTableApiService::class.java)
    }

    val studentListForMarkingAttendance : ApiServices.GetStudentListForMarkingAttendanceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices.GetStudentListForMarkingAttendanceApiService::class.java)
    }

    val markStudentAttendanceApiService: ApiServices.MarkStudentAttendanceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices.MarkStudentAttendanceApiService::class.java)
    }

    val viewAttendanceInstance: ApiServices.ViewStudentAttendanceApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices.ViewStudentAttendanceApiService::class.java)
    }

    val subjectRequestInstance: ApiServices.TeacherSectionsWithSubjectsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices.TeacherSectionsWithSubjectsApiService::class.java)
    }

    val examListInstance: ApiServices.ExamListApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices.ExamListApiService::class.java)
    }

    val sectionWiseStudentListDetailInstance: ApiServices.SectionWiseStudentListDetailApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices.SectionWiseStudentListDetailApiService::class.java)
    }

    val uploadMarksInstance: ApiServices.UploadMarksApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices.UploadMarksApiService::class.java)
    }

    val viewMarksInstance: ApiServices.ViewMarksApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices.ViewMarksApiService::class.java)
    }

    val assignedSubjectInstance: ApiServices.GetAssignedSubjectsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices.GetAssignedSubjectsApiService::class.java)
    }

    val uploadWorkInstance: ApiServices.UploadWorkApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().addInterceptor(logging).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices.UploadWorkApiService::class.java)
    }

    val getNoticeDetailInstance: ApiServices.GetNoticeDetailApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices.GetNoticeDetailApiService::class.java)
    }

    val downloadPdfInstance: ApiServices.DownloadPdfApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices.DownloadPdfApiService::class.java)
    }

    val changePasswordInstance: ApiServices.ChangePasswordApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices.ChangePasswordApiService::class.java)
    }

}