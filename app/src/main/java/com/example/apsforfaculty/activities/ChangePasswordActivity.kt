package com.example.apsforfaculty.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.apsforfaculty.R
import com.example.apsforfaculty.activities.MyAccountActivity
import com.example.apsforfaculty.classes.ApiClient
import com.example.apsforfaculty.classes.ApiServices
import com.example.apsforfaculty.classes.PrefsManager
import com.example.apsforfaculty.databinding.ActivityChangePasswordBinding
import com.example.apsforfaculty.models.PasswordRequirement
import com.example.apsforfaculty.responses.ChangePasswordResponse
import com.example.apsforfaculty.responses.TeacherDetailsResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val passwordRequirements = listOf(
        PasswordRequirement("At least 6 characters", Pattern.compile(".{6,}")),
        PasswordRequirement("One uppercase letter", Pattern.compile("[A-Z]")),
        PasswordRequirement("One lowercase letter", Pattern.compile("[a-z]")),
        PasswordRequirement("One number", Pattern.compile("\\d")),
        PasswordRequirement("One special character", Pattern.compile("[!@#$%^&*(),.?\":{}|<>]"))
    )
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
        setupPasswordRequirements()

    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener { onBackPressed() }

        binding.etNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                updatePasswordStrength(password)
                updatePasswordRequirements(password)
                updatePasswordMatch()
                updateSubmitButton()
            }
        })

        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updatePasswordMatch()
                updateSubmitButton()
            }
        })

        binding.btnUpdatePassword.setOnClickListener {
            if (!isLoading && canSubmit()) {
                updatePassword()
            }
        }
    }

    private fun setupPasswordRequirements() {
        passwordRequirements.forEach { requirement ->
            val requirementView = layoutInflater.inflate(
                R.layout.item_password_requirement,
                binding.llRequirements,
                false
            )
            val ivCheck = requirementView.findViewById<ImageView>(R.id.iv_requirement_check)
            val tvRequirement = requirementView.findViewById<TextView>(R.id.tv_requirement)

            tvRequirement.text = requirement.text
            requirementView.tag = requirement

            binding.llRequirements.addView(requirementView)
        }
    }

    private fun updatePasswordStrength(password: String) {
        if (password.isEmpty()) {
            binding.llPasswordStrength.visibility = View.GONE
            return
        }

        binding.llPasswordStrength.visibility = View.VISIBLE

        val satisfiedCount = passwordRequirements.count { it.pattern.matcher(password).find() }

        when {
            satisfiedCount <= 2 -> {
                binding.tvPasswordStrength.text = "Weak"
                binding.tvPasswordStrength.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.destructive
                    )
                )
                binding.pbPasswordStrength.progress = 33
                binding.pbPasswordStrength.progressTintList =
                    ContextCompat.getColorStateList(this, R.color.destructive)
            }

            satisfiedCount <= 4 -> {
                binding.tvPasswordStrength.text = "Medium"
                binding.tvPasswordStrength.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.secondary
                    )
                )
                binding.pbPasswordStrength.progress = 66
                binding.pbPasswordStrength.progressTintList =
                    ContextCompat.getColorStateList(this, R.color.secondary)
            }

            else -> {
                binding.tvPasswordStrength.text = "Strong"
                binding.tvPasswordStrength.setTextColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.holo_green_dark
                    )
                )
                binding.pbPasswordStrength.progress = 100
                binding.pbPasswordStrength.progressTintList =
                    ContextCompat.getColorStateList(this, android.R.color.holo_green_dark)
            }
        }
    }

    private fun updatePasswordRequirements(password: String) {
        for (i in 0 until binding.llRequirements.childCount) {
            val requirementView = binding.llRequirements.getChildAt(i)
            val requirement = requirementView.tag as PasswordRequirement
            val ivCheck = requirementView.findViewById<ImageView>(R.id.iv_requirement_check)
            val tvRequirement = requirementView.findViewById<TextView>(R.id.tv_requirement)

            val isSatisfied = requirement.pattern.matcher(password).find()

            if (isSatisfied) {
                ivCheck.setImageResource(R.drawable.ic_check)
                ivCheck.setColorFilter(ContextCompat.getColor(this, R.color.accent))
                tvRequirement.setTextColor(ContextCompat.getColor(this, R.color.foreground))
            } else {
                ivCheck.setImageResource(R.drawable.ic_circle_outline)
                ivCheck.setColorFilter(ContextCompat.getColor(this, R.color.muted_foreground))
                tvRequirement.setTextColor(ContextCompat.getColor(this, R.color.muted_foreground))
            }
        }
    }

    private fun updatePasswordMatch() {
        val newPassword = binding.etNewPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if (confirmPassword.isEmpty()) {
            binding.llPasswordMatch.visibility = View.GONE
            return
        }

        binding.llPasswordMatch.visibility = View.VISIBLE

        if (newPassword == confirmPassword) {
            binding.ivPasswordMatch.setImageResource(R.drawable.ic_check)
            binding.ivPasswordMatch.setColorFilter(
                ContextCompat.getColor(
                    this,
                    android.R.color.holo_green_dark
                )
            )
            binding.tvPasswordMatch.text = "Passwords match"
            binding.tvPasswordMatch.setTextColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.holo_green_dark
                )
            )
        } else {
            binding.ivPasswordMatch.setImageResource(R.drawable.ic_close)
            binding.ivPasswordMatch.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.destructive
                )
            )
            binding.tvPasswordMatch.text = "Passwords don't match"
            binding.tvPasswordMatch.setTextColor(ContextCompat.getColor(this, R.color.destructive))
        }
    }

    private fun canSubmit(): Boolean {
        val newPassword = binding.etNewPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        val allRequirementsMet = passwordRequirements.all { it.pattern.matcher(newPassword).find() }
        val passwordsMatch = newPassword == confirmPassword && confirmPassword.isNotEmpty()

        return allRequirementsMet && passwordsMatch && !isLoading
    }

    private fun updateSubmitButton() {
        binding.btnUpdatePassword.isEnabled = canSubmit()
    }

    private fun updatePassword() {
        isLoading = true
        binding.btnUpdatePassword.text = "Updating..."
        binding.btnUpdatePassword.isEnabled = false

        lifecycleScope.launch {
            try {
                // Simulate API call
                delay(2000)
                ApiClient.changePasswordInstance.updatePassword(
                    "application/x-www-form-urlencoded",
                    "ci_session=jku7tesctvpf0d2h8k9okaobui5lgabj",
                    PrefsManager.getTeacherDetailedInformation(applicationContext).data.id,
                    binding.etNewPassword.text.toString()
                )
                    .enqueue(object : Callback<ChangePasswordResponse> {
                        override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                            if (response.isSuccessful && response.body() != null) {
                                Toast.makeText(this@ChangePasswordActivity, "Password updated successfully!",Toast.LENGTH_LONG).show()
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                            Log.e("MyAccountActivity", "API call failed", t)
                        }
                    })



            } catch (e: Exception) {
                Toast.makeText(
                    this@ChangePasswordActivity,
                    "Failed to update password. Please try again.",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                isLoading = false
                binding.btnUpdatePassword.text = "Update Password"
                updateSubmitButton()
            }
        }

    }
}