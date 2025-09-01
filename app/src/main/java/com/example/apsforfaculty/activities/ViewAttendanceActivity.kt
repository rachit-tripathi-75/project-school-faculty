package com.example.apsforfaculty.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apsforfaculty.R
import com.example.apsforfaculty.adapters.StudentViewAttendanceAdapter
import com.example.apsforfaculty.classes.ApiClient
import com.example.apsforfaculty.classes.PrefsManager
import com.example.apsforfaculty.databinding.ActivityViewAttendanceBinding
import com.example.apsforfaculty.models.AttendanceStatus
import com.example.apsforfaculty.models.StudentViewAttendanceModel
import com.example.apsforfaculty.responses.AttendanceRecord
import com.example.apsforfaculty.responses.ViewAttendanceResponse
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ViewAttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewAttendanceBinding
    private lateinit var studentViewAttendanceAdapter: StudentViewAttendanceAdapter
    private var selectedDate: Calendar? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listeners()
        setupRecyclerView()
        setupDatePicker()
        setupSearchButton()
        showInitialState()
    }

    private fun listeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        studentViewAttendanceAdapter = StudentViewAttendanceAdapter(emptyList())
        binding.recyclerViewStudents.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewStudents.adapter = studentViewAttendanceAdapter
    }

    private fun setupDatePicker() {
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = selectedDate ?: Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this, { _, year, month, dayOfMonth ->
                selectedDate = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
                val formattedDate = dateFormat.format(selectedDate!!.time)
                val displayDate = displayDateFormat.format(selectedDate!!.time)
                binding.btnSelectDate.text = formattedDate
                binding.tvSelectedDate.text = displayDate
                binding.tvSelectedDate.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
                binding.btnSearch.isEnabled = true
                binding.btnSearch.alpha = 1.0f
                if (binding.layoutResults.visibility == View.VISIBLE) {
                    showInitialState()
                    binding.tvSelectedDate.text = displayDate
                    binding.tvSelectedDate.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun setupSearchButton() {
        binding.btnSearch.setOnClickListener {
            if (selectedDate != null) {
                searchAttendance()
            } else {
                Toast.makeText(this, "Please select a date first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchAttendance() {
        showLoadingState()
        val dateText = dateFormat.format(selectedDate!!.time)
        ApiClient.viewAttendanceInstance.getAttendance(
            "application/x-www-form-urlencoded",
            "ci_session=ntsr7u5og5lhi0khp9nji5fru9md06nn",
            PrefsManager.getTeacherDetailedInformation(this).data.id,
            dateText
        )
            .enqueue(object : retrofit2.Callback<ViewAttendanceResponse> {
                override fun onResponse(call: Call<ViewAttendanceResponse>, response: retrofit2.Response<ViewAttendanceResponse>) {
                    if (response.isSuccessful) {
                        val s = response.body()
                        if (s?.status == 1) {
                            if (s.data.isEmpty()) {
                                showInitialState()
                                binding.btnSearch.isEnabled = true
                                binding.btnSearch.alpha = 1.0f
                                Snackbar.make(binding.root, "No data found for this date.", Snackbar.LENGTH_LONG).show()
                            } else {
                                val attendanceList = getStudentAttendanceList(s.data)
                                showResults(attendanceList)
                            }
                        } else {
                            showInitialState()
                            binding.btnSearch.isEnabled = true
                            binding.btnSearch.alpha = 1.0f
                            Snackbar.make(binding.root, s?.Msg ?: "Failed to get data", Snackbar.LENGTH_LONG).show()
                        }
                    } else {
                        showInitialState()
                        Toast.makeText(this@ViewAttendanceActivity, "An error occurred.", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ViewAttendanceResponse>, t: Throwable) {
                    Log.e("ViewAttendanceTAG", "onFailure, Error: ${t.message}")
                    binding.llInternalServerError.visibility = View.VISIBLE
                    binding.llMainContent.visibility = View.GONE
                    Toast.makeText(this@ViewAttendanceActivity, "Network error. Please try again.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showInitialState() {
        binding.layoutLoading.visibility = View.GONE
        binding.layoutNoData.visibility = View.VISIBLE
        binding.layoutResults.visibility = View.GONE
    }

    private fun showLoadingState() {
        binding.layoutLoading.visibility = View.VISIBLE
        binding.layoutNoData.visibility = View.GONE
        binding.layoutResults.visibility = View.GONE
        binding.btnSearch.isEnabled = false
        binding.btnSearch.alpha = 0.5f
    }

    private fun showResults(attendanceList: List<StudentViewAttendanceModel>) {
        binding.layoutLoading.visibility = View.GONE
        binding.layoutNoData.visibility = View.GONE
        binding.layoutResults.visibility = View.VISIBLE
        binding.btnSearch.isEnabled = true
        binding.btnSearch.alpha = 1.0f
        studentViewAttendanceAdapter.updateData(attendanceList)
        updateAttendanceSummary(attendanceList)
    }

    // ✅ YAHAN CHANGES KIYE GAYE HAIN
    private fun updateAttendanceSummary(attendanceList: List<StudentViewAttendanceModel>) {
        val presentCount = attendanceList.count { it.status == AttendanceStatus.PRESENT }
        val absentCount = attendanceList.count { it.status == AttendanceStatus.ABSENT }
        val totalMarked = presentCount + absentCount
        val attendanceRate = if (totalMarked > 0) {
            (presentCount * 100) / totalMarked
        } else 0
        binding.tvPresentCount.text = presentCount.toString()
        binding.tvAbsentCount.text = absentCount.toString()
        binding.tvAttendanceRate.text = getString(R.string.attendance_rate_format, attendanceRate)
        binding.tvTotalStudents.text = getString(R.string.total_students, attendanceList.size)
    }

    // ✅ YAHAN BHI CHANGES KIYE GAYE HAIN
    private fun getStudentAttendanceList(data: List<AttendanceRecord>): List<StudentViewAttendanceModel> {
        return data.map { record ->
            // API se mile hue string ko hum apne naye enum me convert kar rahe hain
            val currentStatus = when (record.attendance.lowercase()) {
                "present" -> AttendanceStatus.PRESENT
                "absent" -> AttendanceStatus.ABSENT
                else -> AttendanceStatus.NOT_MARKED
            }
            StudentViewAttendanceModel(
                record.student_name,
                record.student_id,
                record.enrollment,
                currentStatus
            )
        }
    }
}