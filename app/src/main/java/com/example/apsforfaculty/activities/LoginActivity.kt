package com.example.apsforfaculty.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apsforfaculty.MainActivity
import com.example.apsforfaculty.R
import com.example.apsforfaculty.classes.ApiClient
import com.example.apsforfaculty.classes.PrefsManager
import com.example.apsforfaculty.databinding.ActivityLoginBinding
import com.example.apsforfaculty.responses.LoginResponse
import com.example.apsforfaculty.responses.TeacherDetails
import com.example.apsforfaculty.responses.TeacherDetailsResponse
import retrofit2.Call
import java.util.regex.Pattern
import kotlin.toString

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        listeners()
    }

    private fun listeners() {
        binding.btnSignIn.setOnClickListener {
            if (isValidDetails()) {
                signIn()
            }
        }
        binding.togglePasswordButton.setOnClickListener { view ->
            if (isPasswordVisible) {
                // Hide password
                binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.togglePasswordButton.setImageResource(R.drawable.ic_visibility_off)
            } else {
                // Show password
                binding.etPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.togglePasswordButton.setImageResource(R.drawable.ic_visibility)
            }
            isPasswordVisible = !isPasswordVisible
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

    }

    private fun isValidDetails(): Boolean {
        if (binding.etEmail.text.toString().isEmpty()) {
            binding.etEmail.setError("Enter enrollment number")
            return false
        } else if (isValidEmailAddress(binding.etEmail.text.toString())) {

        } else if (binding.etPassword.text.toString().isEmpty()) {
            binding.etPassword.setError("Enter password")
            return false
        }
        return true
    }

    private fun signIn() {
        binding.btnSignIn.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        ApiClient.loginInstance.login(binding.etEmail.text.toString(), binding.etPassword.text.toString()).enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
                binding.btnSignIn.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    Log.d("loginResponseTAG", response.body()?.Msg.toString())
                    val loginData = response.body()
                    if (loginData?.status == 1) {
                        PrefsManager.setUserInformation(this@LoginActivity, loginData)
                        PrefsManager.setSession(this@LoginActivity, true)
                        getDetailedInformation()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }

                } else {
                    Log.d("loginResponseTAG", response.body()?.Msg.toString())
                    Toast.makeText(this@LoginActivity, "Incorrect Credentials", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.btnSignIn.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                Log.d("loginResponseTAG", "Error: ${t.message}")
                Toast.makeText(this@LoginActivity, "An error has occurred. Please try again later", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun getDetailedInformation() {

        ApiClient.teacherDetailsInstance.getDetailedInformation(
            "application/x-www-form-urlencoded",
            "ci_session=snunhleb4451tl3ebdp5ao6cfrml3anq",
            PrefsManager.getUserInformation(this).data.id
        ).enqueue(object : retrofit2.Callback<TeacherDetailsResponse> {
            override fun onResponse(call: Call<TeacherDetailsResponse>, response: retrofit2.Response<TeacherDetailsResponse>) {

                if (response.isSuccessful) {
                    Log.d("teacherDetailsInformationTAG", response.body()?.Msg.toString())
                    val loginData = response.body()
                    if (loginData?.status == 0) {
                        PrefsManager.setTeacherDetailedInformation(this@LoginActivity, loginData)
                    }

                } else {
                    Log.d("teacherDetailsInformationTAG", response.body()?.Msg.toString())
                    Toast.makeText(this@LoginActivity, "Some error occurred. Please try again later.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TeacherDetailsResponse>, t: Throwable) {
                binding.btnSignIn.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                Log.d("teacherDetailsInformationTAG", "Error: ${t.message}")
                Toast.makeText(this@LoginActivity, "An error has occurred. Please try again later", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun isValidEmailAddress(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


}