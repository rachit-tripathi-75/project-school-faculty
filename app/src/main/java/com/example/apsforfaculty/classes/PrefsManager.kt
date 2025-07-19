package com.example.apsforfaculty.classes

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.apsforfaculty.responses.LoginResponse
import com.example.apsforfaculty.responses.TeacherDetails
import com.example.apsforfaculty.responses.TeacherDetailsResponse
import com.google.gson.Gson

class PrefsManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }


    companion object {
        const val PREF_NAME = "MyPrefs"

        fun setSession(context: Context, flag: Boolean) {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit() {
                putBoolean("isLoggedIn", flag)
            }
        }

        fun getSession(context: Context): Boolean {
            val sharedPreferences =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("isLoggedIn", false)
        }

        fun setUserInformation(context: Context, loginResponse: LoginResponse) {
            val gson = Gson()
            val userJsonInformationString = gson.toJson(loginResponse)
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit() {
                putString("userInformation", userJsonInformationString)
            }
        }

        fun getTeacherDetailedInformation(context: Context) : TeacherDetailsResponse {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val gson = Gson()
            val jsonFromPrefs = sharedPreferences.getString("teacherDetailedInformation", null)
            val userInformationObject = gson.fromJson(jsonFromPrefs, TeacherDetailsResponse::class.java)
            return userInformationObject
        }


        fun setTeacherDetailedInformation(context: Context, teacherDetailsResponse: TeacherDetailsResponse) {
            val gson = Gson()
            val userJsonInformationString = gson.toJson(teacherDetailsResponse)
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit() {
                putString("teacherDetailedInformation", userJsonInformationString)
            }
        }

        fun getUserInformation(context: Context) : LoginResponse {
            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val gson = Gson()
            val jsonFromPrefs = sharedPreferences.getString("userInformation", null)
            val userInformationObject = gson.fromJson(jsonFromPrefs, LoginResponse::class.java)
            return userInformationObject
        }

//        fun setUserDetailedInformation(context: Context, studentDetailResponse: StudentDetailResponse) {
//            val gson = Gson()
//            val userDetailedInformationString = gson.toJson(studentDetailResponse)
//            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit {
//                putString("userDetailedInformation", userDetailedInformationString)
//            }
//        }
//
//        fun getUserDetailedInformation(context: Context): StudentDetailResponse {
//            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//            val gson = Gson()
//            val jsonFromPrefs = sharedPreferences.getString("userDetailedInformation", null)
//            val userDetailedInformationObject = gson.fromJson(jsonFromPrefs, StudentDetailResponse::class.java)
//            return userDetailedInformationObject
//        }

//        fun setSectionId(context: Context, sectionId: String) {
//            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit() {
//                putString("sectionId", sectionId)
//            }
//        }
//
//        fun getSectionId(context: Context): String {
//            val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
//            return sharedPreferences.getString("sectionId", "-1") ?: "-1"
//        }

    }
}