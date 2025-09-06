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

        listeners()
        fetchTeacherDetails()
    }

    private fun fetchTeacherDetails() {

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
            tvName.text = teacher.name
            tvEmailValue.text = teacher.email
            tvPhoneValue.text = teacher.phone
            tvEmpCode.text = teacher.emp_code
            tvUsernameValue.text = teacher.username
            tvFatherNameValue.text = teacher.father_name
            tvMotherNameValue.text = teacher.mother_name
            tvGenderValue.text = teacher.gender
            tvDOBValue.text = teacher.dob
            tvBloodGroupValue.text = teacher.blood_group
            tvPhoneValue.text = teacher.phone
            tvHomeContactValue.text = teacher.homecontact
            tvAddressValue.text = teacher.address
            tvPermanentAddressValue.text = teacher.per_address
            tvAadhaarValue.text = teacher.adhaar
            tvPanValue.text = teacher.pan
            tvBankValue.text = teacher.bank
            tvAccountHolderValue.text = teacher.accountholdername
            tvAccountNumberValue.text = teacher.bankaccountnumber
            tvIfscValue.text = teacher.ifsc
            tvJoinDateValue.text = teacher.joindate
            chipRole.text = teacher.role
            chipAcademicYear.text = teacher.academic_year

            // Load the profile image using Glide
            Glide.with(this@MyAccountActivity)
                .load(teacher.image_url)
                .placeholder(R.drawable.ic_person_placeholder) // Default image while loading
                .error(R.drawable.ic_person_placeholder)      // Image if loading fails
                .circleCrop()                       // Make the image circular
                .into(binding.civProfileImage)
        }
    }

    private fun listeners() {

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}