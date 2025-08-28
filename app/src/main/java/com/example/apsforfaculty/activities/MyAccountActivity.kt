package com.example.apsforfaculty.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide // Import Glide
import com.example.apsforfaculty.R
import com.example.apsforfaculty.classes.ApiClient
import com.example.apsforfaculty.classes.PrefsManager
import com.example.apsforfaculty.databinding.ActivityMyAccountBinding
import com.example.apsforfaculty.responses.TeacherDetails // Import the new models
import com.example.apsforfaculty.responses.TeacherDetailsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MyAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        binding = ActivityMyAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialisers() // This function is empty, can be removed or used later
        listeners()

        // Fetch data from the API when the activity is created
        fetchTeacherDetails()
    }

    private fun fetchTeacherDetails() {
        // Assume you get teacher ID from your PrefsManager
        val teacherId = PrefsManager.getTeacherDetailedInformation(this).data.id
        val contentType = "application/x-www-form-urlencoded"
        val cookie = "ci_session=snunhleb4451tl3ebdp5ao6cfrml3anq"

        ApiClient.teacherDetailsInstance.getDetailedInformation(contentType, cookie, teacherId)
            .enqueue(object : Callback<TeacherDetailsResponse> {
                override fun onResponse(call: Call<TeacherDetailsResponse>, response: Response<TeacherDetailsResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val teacherDetails = response.body()!!.data
                        // Call a separate function to update the UI
                        updateUi(teacherDetails)
                    } else {
                        Toast.makeText(this@MyAccountActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<TeacherDetailsResponse>, t: Throwable) {
                    Log.e("MyAccountActivity", "API call failed", t)
                    Toast.makeText(this@MyAccountActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateUi(teacher: TeacherDetails) {
        binding.apply {
            // Set basic text information
            tvTeacherName.text = teacher.name
            tvEmail.text = teacher.email
            tvPhone.text = teacher.phone
            tvEmpCode.text = teacher.emp_code

            // The API provides "teacher" for role. We can capitalize it for better display.
            tvDesignation.text = teacher.role.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
            }

            // Load the profile image using Glide
            Glide.with(this@MyAccountActivity)
                .load(teacher.image_url)
                .placeholder(R.drawable.ic_person) // Default image while loading
                .error(R.drawable.ic_person)      // Image if loading fails
                .circleCrop()                       // Make the image circular
                .into(ivProfilePicture)
        }
    }

    private fun listeners() {
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this@MyAccountActivity, EditProfileActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this@MyAccountActivity, SettingsActivity::class.java))
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}