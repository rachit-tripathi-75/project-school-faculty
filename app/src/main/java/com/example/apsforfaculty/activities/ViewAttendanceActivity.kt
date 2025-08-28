package com.example.apsforfaculty.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.apsforfaculty.activities.MarkAttendanceActivity
import com.example.apsforfaculty.adapters.StudentViewAttendanceAdapter
import com.example.apsforfaculty.classes.ApiClient
import com.example.apsforfaculty.classes.PrefsManager
import com.example.apsforfaculty.databinding.ActivityViewAttendanceBinding
import com.example.apsforfaculty.models.StudentViewAttendanceModel
import com.example.apsforfaculty.responses.AttendanceRecord
import com.example.apsforfaculty.responses.ViewAttendanceResponse
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Random

class ViewAttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewAttendanceBinding
    private lateinit var studentViewAttendanceAdapter: StudentViewAttendanceAdapter
    private var selectedDate: Calendar? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    private val handler = Handler(Looper.getMainLooper())

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
            onBackPressed()
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
            this,
            { _, year, month, dayOfMonth ->
                selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }

                // Update UI with selected date
                val formattedDate = dateFormat.format(selectedDate!!.time)
                val displayDate = displayDateFormat.format(selectedDate!!.time)

                binding.btnSelectDate.text = formattedDate
                binding.tvSelectedDate.text = displayDate
                binding.tvSelectedDate.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))

                // Enable search button
                binding.btnSearch.isEnabled = true
                binding.btnSearch.alpha = 1.0f

                // Show feedback
//                Toast.makeText(this, getString(R.string.date_selected_toast, formattedDate), Toast.LENGTH_SHORT).show()

                // Hide results if previously shown
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

        // Customize date picker
        datePickerDialog.setTitle(getString(R.string.select_attendance_date))

        // Set date constraints (e.g., don't allow future dates)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        // Set minimum date (e.g., start of academic year)
        val minCalendar = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.APRIL) // Academic year starts in April
            set(Calendar.DAY_OF_MONTH, 1)
        }
        datePickerDialog.datePicker.minDate = minCalendar.timeInMillis

        datePickerDialog.show()
    }

    private fun setupSearchButton() {
        binding.btnSearch.setOnClickListener {
            if (selectedDate != null) {
                searchAttendance()
            } else {
                Toast.makeText(this, getString(R.string.select_date_first), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchAttendance() {
        showLoadingState()

        val dateText = dateFormat.format(selectedDate!!.time)



        ApiClient.viewAttendanceInstance.getAttendance(
            "application/x-www-form-urlencoded",
            "ci_session=ntsr7u5og5lhi0khp9nji5fru9md06nn",
            PrefsManager.getUserInformation(applicationContext).data.id,
            dateText).enqueue(object : retrofit2.Callback<ViewAttendanceResponse> {
            override fun onResponse(call: Call<ViewAttendanceResponse>, response: retrofit2.Response<ViewAttendanceResponse>) {

                if (response.isSuccessful) {
                    Log.d("viewAttendanceTAG", response.body()?.Msg.toString())
                    val s = response.body()
                    if (s?.status == 1) {
                        if (s.data.isEmpty()) {
                            showInitialState()
                            binding.btnSearch.isEnabled = true
                            binding.btnSearch.alpha = 1.0f
                            Snackbar.make(binding.root, "No data found", Snackbar.LENGTH_LONG).show()
                            Log.d("viewAttendanceTAG", "inside if-data-is-empty: " + response.body()?.Msg.toString())
                        } else if (s.data.isNotEmpty()) {
                            binding.llMainContent.visibility = View.VISIBLE
                            val attendanceList = getStudentAttendanceList(s.data)
                            showResults(attendanceList)
                            Log.d("viewAttendanceTAG", "inside if-data-is-not-empty: " + response.body()?.Msg.toString())
                        }
                    } else {
                        showInitialState()
                        binding.btnSearch.isEnabled = true
                        binding.btnSearch.alpha = 1.0f
                        Snackbar.make(binding.root, s?.Msg!!, Snackbar.LENGTH_LONG).show()
                        Log.d("viewAttendanceTAG", "inside else-status != 1: " + response.body()?.Msg.toString())
                    }

                } else {
                    Log.d("viewAttendanceTAG", "inside else-unsuccessful: " + response.body()?.Msg.toString())
                    Toast.makeText(this@ViewAttendanceActivity, "Some error occurred.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ViewAttendanceResponse>, t: Throwable) {
                Log.d("viewAttendanceTAG", "onFailure, Error: ${t.message}")
                binding.llInternalServerError.visibility = View.VISIBLE
                binding.llMainContent.visibility = View.GONE
                Toast.makeText(this@ViewAttendanceActivity, "An error has occurred. Please try again later",Toast.LENGTH_SHORT).show()
            }
        })


//        // Simulate API call with delay
//        handler.postDelayed({
//            val attendanceList = getStudentAttendanceList()
//            showResults(attendanceList)
//        }, 1500) // 1.5 second delay to simulate loading
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

        val dateText = dateFormat.format(selectedDate!!.time)
//        Toast.makeText(this, getString(R.string.attendance_loaded_toast, dateText), Toast.LENGTH_SHORT).show()
    }

    private fun updateAttendanceSummary(attendanceList: List<StudentViewAttendanceModel>) {
        val presentCount = attendanceList.count { it.isPresent }
        val absentCount = attendanceList.size - presentCount
        val attendanceRate = if (attendanceList.isNotEmpty()) {
            (presentCount * 100) / attendanceList.size
        } else 0

        binding.tvPresentCount.text = presentCount.toString()
        binding.tvAbsentCount.text = absentCount.toString()
        binding.tvAttendanceRate.text = getString(R.string.attendance_rate_format, attendanceRate)
        binding.tvTotalStudents.text = getString(R.string.total_students, attendanceList.size)
    }

    private fun getStudentAttendanceList(data: List<AttendanceRecord>): List<StudentViewAttendanceModel> {
        // Sample data with some logic based on selected date
        val random = Random(selectedDate!!.timeInMillis) // Use date as seed for consistent results


        val list = mutableListOf<StudentViewAttendanceModel>()
        list.clear()

        for (i in 0 until data.size) {
            if (data.get(i).attendance.equals("Absent", ignoreCase = true)) {
                list.add(StudentViewAttendanceModel(data.get(i).student_name, data.get(i).student_id, data.get(i).enrollment, true))
            } else if (data.get(i).attendance.equals("Absent", ignoreCase = true)){
                list.add(StudentViewAttendanceModel(data.get(i).student_name, data.get(i).student_id, data.get(i).enrollment, false))
            }

        }

        return list
    }

}