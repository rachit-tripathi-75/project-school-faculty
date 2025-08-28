package com.example.apsforfaculty.activities

import android.animation.ObjectAnimator
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apsforfaculty.R
import com.example.apsforfaculty.activities.ViewAttendanceActivity
import com.example.apsforfaculty.adapters.StudentListForAttendanceAdapter
import com.example.apsforfaculty.classes.ApiClient
import com.example.apsforfaculty.classes.PrefsManager
import com.example.apsforfaculty.databinding.ActivityMarkAttendanceBinding
import com.example.apsforfaculty.models.AttendanceItem
import com.example.apsforfaculty.responses.AttendanceRecord
import com.example.apsforfaculty.models.Student
import com.example.apsforfaculty.responses.MarkStudentAttendanceResponse
import com.example.apsforfaculty.responses.StudentData
import com.example.apsforfaculty.responses.StudentListForMarkingAttendanceResponse
import com.example.apsforfaculty.responses.ViewAttendanceResponse
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MarkAttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarkAttendanceBinding
    private lateinit var studentListAdapter: StudentListForAttendanceAdapter
    private lateinit var listViewContainer: View
    private lateinit var swipeViewContainer: View
    private var students = mutableListOf<Student>()
    private var alreadyMarkedStudents = mutableListOf<Student>()
    private var currentSwipeIndex = 0
    private var isListView = true
    private var hasAlreadyMarked: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMarkAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initialisers()
        listeners()
        getStudentsList()
        setupRecyclerView()
        setupClickListeners()
        updateHeaderStats()
        showListView()

    }

    private fun initialisers() {

        listViewContainer = findViewById(R.id.listViewContainer)
        swipeViewContainer = findViewById(R.id.swipeViewContainer)

    }

    private fun listeners() {

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun checkIfTodaysAttendanceIsMarked() {


        ApiClient.viewAttendanceInstance.getAttendance(
            "application/x-www-form-urlencoded",
            "ci_session=ntsr7u5og5lhi0khp9nji5fru9md06nn",
            PrefsManager.getUserInformation(this).data.id,
            getTodaysDate()).enqueue(object : retrofit2.Callback<ViewAttendanceResponse> {
            override fun onResponse(call: Call<ViewAttendanceResponse>, response: retrofit2.Response<ViewAttendanceResponse>) {

                if (response.isSuccessful) {
                    Log.d("viewAttendanceTAG", response.body()?.Msg.toString())
                    val s = response.body()
                    if (s?.status == 1) {
                        if (s.data.isEmpty()) {
                            Snackbar.make(binding.root, "No data found", Snackbar.LENGTH_LONG).show()
                            Log.d("viewAttendanceTAG", "inside if-data-is-empty: " + response.body()?.Msg.toString())
                        } else if (s.data.isNotEmpty()) {
                            binding.llMainContent.visibility = View.VISIBLE
                            if (s.data.get(0).attendance.equals("Not Marked")) {
                                hasAlreadyMarked = false
                            } else if (s.data.get(0).attendance.equals("Present") || s.data.get(0).attendance.equals("Absent") ){
                                hasAlreadyMarked = true
                            }
                            setupByDefaultStudentData(s.data)
                            Log.d("viewAttendanceTAG", "inside if-data-is-not-empty: " + response.body()?.Msg.toString())
                        }
                    } else {
                        Snackbar.make(binding.root, s?.Msg!!, Snackbar.LENGTH_LONG).show()
                        Log.d("viewAttendanceTAG", "inside else-status != 1: " + response.body()?.Msg.toString())
                    }

                } else {
                    Log.d("viewAttendanceTAG", "inside else-unsuccessful: " + response.body()?.Msg.toString())
                    Toast.makeText(this@MarkAttendanceActivity, "Some error occurred.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ViewAttendanceResponse>, t: Throwable) {
                Log.d("viewAttendanceTAG", "onFailure, Error: ${t.message}")
                binding.llInternalServerError.visibility = View.VISIBLE
                binding.llMainContent.visibility = View.GONE
                Toast.makeText(this@MarkAttendanceActivity, "An error has occurred. Please try again later",Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun getStudentsList() {

        binding.progressBar.visibility = View.VISIBLE

        ApiClient.studentListForMarkingAttendance.getStudentListForMarkingAttendance(
            "application/x-www-form-urlencoded",
            "ci_session=te15gpti3hsf4546ljdr6q32cbuqmjo3",
            PrefsManager.getUserInformation(this).data.id
        ).enqueue(object : retrofit2.Callback<StudentListForMarkingAttendanceResponse> {
            override fun onResponse(
                call: Call<StudentListForMarkingAttendanceResponse>,
                response: retrofit2.Response<StudentListForMarkingAttendanceResponse>
            ) {

                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    Log.d("studentListTAG", response.body()?.Msg.toString())
                    val s = response.body()
                    if (s?.status == 1) {
                        if (s.data.isEmpty()) {
                            binding.llNoDataFound.visibility = View.VISIBLE
                            Log.d(
                                "studentListTAG",
                                "inside if-data-is-empty: " + response.body()?.Msg.toString()
                            )
                        } else if (s.data.isNotEmpty()) {
                            binding.llMainContent.visibility = View.VISIBLE
                            setupStudentData(s.data)
                            Log.d(
                                "studentListTAG",
                                "inside if-data-is-not-empty: " + response.body()?.Msg.toString()
                            )
                        }
                    } else {
                        binding.llInternalServerError.visibility = View.VISIBLE
                        Log.d(
                            "studentListTAG",
                            "inside else-status != 1: " + response.body()?.Msg.toString()
                        )
                    }

                } else {
                    Log.d(
                        "studentListTAG",
                        "inside else-unsuccessful: " + response.body()?.Msg.toString()
                    )
                    Toast.makeText(
                        this@MarkAttendanceActivity,
                        "Some error occurred.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<StudentListForMarkingAttendanceResponse>,
                t: Throwable
            ) {
                binding.progressBar.visibility = View.GONE
                Log.d("studentListTAG", "onFailure, Error: ${t.message}")
                Toast.makeText(
                    this@MarkAttendanceActivity,
                    "An error has occurred. Please try again later",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun setupByDefaultStudentData(data: List<AttendanceRecord>) {
        alreadyMarkedStudents.clear()

        for (i in 0 until data.size) {
            if (data.get(i).attendance.equals("Present")) {
                alreadyMarkedStudents.add(Student(data.get(i).student_id, data.get(i).student_name, data.get(i).enrollment, true))
            } else if (data.get(i).attendance.equals("Absent")) {
                alreadyMarkedStudents.add(Student(data.get(i).student_id, data.get(i).student_name, data.get(i).enrollment, false))
            } else if (data.get(i).attendance.equals("Not Marked")) {
                alreadyMarkedStudents.add(Student(data.get(i).student_id, data.get(i).student_name, data.get(i).enrollment, false))
            }

        }
    }

    private fun setupStudentData(data: List<StudentData>) {
        students.clear()

        for (i in 0 until data.size) {
            students.add(Student(data.get(i).stuId, data.get(i).name, data.get(i).enrollment, false)
            )
        }

    }

    private fun setupRecyclerView() {
        studentListAdapter = StudentListForAttendanceAdapter(students) { student ->
            toggleStudentAttendance(student.id)
        }
        binding.listViewContainer.recyclerViewStudents.layoutManager = LinearLayoutManager(this)
        binding.listViewContainer.recyclerViewStudents.adapter = studentListAdapter
    }

    private fun setupClickListeners() {
        // View mode toggle
        binding.btnListView.setOnClickListener {
            showListView()
        }

        binding.btnSwipeView.setOnClickListener {
            showSwipeView()
            resetSwipeView()
        }

        // Bulk actions
        binding.listViewContainer.btnMarkAllPresent.setOnClickListener {
            showBulkActionDialog(true)
        }

        binding.listViewContainer.btnMarkAllAbsent.setOnClickListener {
            showBulkActionDialog(false)
        }

        // Swipe actions
        binding.swipeViewContainer.btnPresent.setOnClickListener {
            handleSwipeAction(true)
        }

        binding.swipeViewContainer.btnAbsent.setOnClickListener {
            handleSwipeAction(false)
        }

        binding.swipeViewContainer.btnStartOver.setOnClickListener {
            resetSwipeView()
        }

        // Submit attendance
        binding.btnSubmitAttendance.setOnClickListener {
            submitAttendance()
        }
    }

    private fun showListView() {
        isListView = true
        listViewContainer.visibility = View.VISIBLE
        swipeViewContainer.visibility = View.GONE

        // Update button styles
        binding.btnListView.setBackgroundResource(R.drawable.button_selected)
        binding.btnListView.setTextColor(ContextCompat.getColor(this, R.color.surface))
        binding.btnSwipeView.setBackgroundResource(R.drawable.button_unselected)
        binding.btnSwipeView.setTextColor(ContextCompat.getColor(this, R.color.on_surface_variant))
    }

    private fun showSwipeView() {
        isListView = false
        listViewContainer.visibility = View.GONE
        swipeViewContainer.visibility = View.VISIBLE

        // Update button styles
        binding.btnListView.setBackgroundResource(R.drawable.button_unselected)
        binding.btnListView.setTextColor(ContextCompat.getColor(this, R.color.on_surface_variant))
        binding.btnSwipeView.setBackgroundResource(R.drawable.button_selected)
        binding.btnSwipeView.setTextColor(ContextCompat.getColor(this, R.color.surface))

        updateSwipeView()
    }

    private fun toggleStudentAttendance(studentId: String) {
        val student = students.find { it.id == studentId }
        student?.let {
            it.isPresent = !it.isPresent
            studentListAdapter.notifyDataSetChanged()
            updateHeaderStats()

            // Show feedback
            val message =
                if (it.isPresent) "${it.name} marked Present" else "${it.name} marked Absent"
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAllAttendance(isPresent: Boolean) {
        students.forEach { it.isPresent = isPresent }
        studentListAdapter.notifyDataSetChanged()
        updateHeaderStats()

        val message = if (isPresent) "All students marked Present" else "All students marked Absent"
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showBulkActionDialog(isPresent: Boolean) {
        val action = if (isPresent) "present" else "absent"
        val message = "Mark all students as $action?"

        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .setAction("CONFIRM") {
                setAllAttendance(isPresent)
            }
            .setActionTextColor(ContextCompat.getColor(this, R.color.primary))
            .show()
    }

    private fun resetSwipeView() {
        currentSwipeIndex = 0
        updateSwipeView()
    }

    private fun updateSwipeView() {
        if (currentSwipeIndex < students.size) {
            val currentStudent = students[currentSwipeIndex]

            // Update student info
            binding.swipeViewContainer.tvSwipeStudentName.text = currentStudent.name
            binding.swipeViewContainer.tvSwipeRollNumber.text =
                "Roll No: ${currentStudent.rollNumber}"

            // Update progress
            val progressPercentage = ((currentSwipeIndex.toFloat() / students.size) * 100).toInt()
            binding.swipeViewContainer.tvProgress.text =
                "Student ${currentSwipeIndex + 1} of ${students.size}"
            binding.swipeViewContainer.tvProgressPercentage.text = "$progressPercentage%"
            binding.swipeViewContainer.progressBar.progress = progressPercentage

            // Show current views
            binding.swipeViewContainer.studentCard.visibility = View.VISIBLE
            binding.swipeViewContainer.actionButtons.visibility = View.VISIBLE
            binding.swipeViewContainer.completionView.visibility = View.GONE

            // Update status dot
            binding.swipeViewContainer.statusDot.setBackgroundResource(R.drawable.status_dot_pending)

        } else {
            // Show completion view
            showCompletionView()
        }
    }

    private fun showCompletionView() {
        binding.swipeViewContainer.studentCard.visibility = View.GONE
        binding.swipeViewContainer.actionButtons.visibility = View.GONE
        binding.swipeViewContainer.completionView.visibility = View.VISIBLE

        // Update completion stats
        val presentCount = students.count { it.isPresent }
        val absentCount = students.size - presentCount

        binding.swipeViewContainer.tvPresentCount.text = presentCount.toString()
        binding.swipeViewContainer.tvAbsentCount.text = absentCount.toString()
        binding.swipeViewContainer.tvCompletionMessage.text =
            "You've marked attendance for all ${students.size} students."
    }

    private fun handleSwipeAction(isPresent: Boolean) {
        if (currentSwipeIndex < students.size) {
            val currentStudent = students[currentSwipeIndex]

            // Update student status
            currentStudent.isPresent = isPresent

            // Show visual feedback
            if (isPresent) {
                binding.swipeViewContainer.statusDot.setBackgroundResource(R.drawable.status_dot_present)
//                Toast.makeText(this, "${currentStudent.name} marked Present", Toast.LENGTH_SHORT).show()
            } else {
                binding.swipeViewContainer.statusDot.setBackgroundResource(R.drawable.status_dot_absent)
//                Toast.makeText(this, "${currentStudent.name} marked Absent", Toast.LENGTH_SHORT).show()
            }

            // Animate card
            animateSwipeCard()

            // Move to next student after animation
            binding.swipeViewContainer.studentCard.postDelayed({
                currentSwipeIndex++
                updateSwipeView()
                updateHeaderStats()
            }, 200)
        }
    }

    private fun animateSwipeCard() {
        val scaleX =
            ObjectAnimator.ofFloat(binding.swipeViewContainer.studentCard, "scaleX", 1f, 0.95f, 1f)
        val scaleY =
            ObjectAnimator.ofFloat(binding.swipeViewContainer.studentCard, "scaleY", 1f, 0.95f, 1f)

        scaleX.duration = 200
        scaleY.duration = 200

        scaleX.start()
        scaleY.start()
    }

    private fun getTodaysDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun updateHeaderStats() {
        val presentCount = students.count { it.isPresent }
        val totalCount = students.size
        val percentage = if (totalCount > 0) (presentCount * 100) / totalCount else 0

        binding.tvTotalStudents.text = totalCount.toString()
        binding.tvPresentStudents.text = presentCount.toString()
        binding.tvPercentage.text = "$percentage%"

        // Update percentage color
        val percentageColor = when {
            percentage >= 80 -> R.color.success
            percentage >= 60 -> R.color.primary
            else -> R.color.error
        }
        binding.tvPercentage.setTextColor(ContextCompat.getColor(this, percentageColor))
    }

    private fun submitAttendance() {

        binding.btnSubmitAttendance.visibility = View.GONE
        binding.btnProgressBar.visibility = View.VISIBLE

        val presentCount = students.count { it.isPresent }
        val absentCount = students.size - presentCount

//        if (presentCount == 0 && absentCount == students.size) {
//            binding.btnSubmitAttendance.visibility = View.VISIBLE
//            binding.btnProgressBar.visibility = View.GONE
//            Toast.makeText(this, "Please mark at least one student as present", Toast.LENGTH_LONG)
//                .show()
//            return
//        }


        val s = mutableListOf<String>();
        s.clear();
        val attendanceList = mutableListOf<AttendanceItem>()
        for (i in 0 until students.size) {
            if (students.get(i).isPresent) {
                attendanceList.add(AttendanceItem(students.get(i).name, 0)) // 1 ---> Present
            } else {
                attendanceList.add(AttendanceItem(students.get(i).name, 1)) // 0 ---> Absent
            }
        }

        if (s.isEmpty()) {
            Log.d("detailsxxx", "empty")
        }

        for (i in 0 until s.size) {
            Log.d("detailsxxx", "Student name: " + s.get(i))
        }



        ApiClient.markStudentAttendanceApiService.postAttendance(
            "application/x-www-form-urlencoded",
            "ci_session=dd5r3o2vph846ri5k027e84cm19e865j",
            PrefsManager.getUserInformation(this).data.id,
            getTodaysDate(),
            Gson().toJson(attendanceList)
        ).enqueue(object : retrofit2.Callback<MarkStudentAttendanceResponse> {
            override fun onResponse(call: Call<MarkStudentAttendanceResponse>, response: retrofit2.Response<MarkStudentAttendanceResponse>) {

                if (response.isSuccessful) {
                    binding.btnSubmitAttendance.visibility = View.VISIBLE
                    binding.btnProgressBar.visibility = View.GONE
                    Log.d("studentListTAG", response.body()?.Msg.toString())
                    val s = response.body()
                    if (s?.status == 1) {
                        if (s.data.isEmpty()) {
                            // Show success message
                            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                            val message = "Attendance submitted successfully!\n" +
                                    "Date: $currentDate\n" +
                                    "Present: $presentCount, Absent: $absentCount"

                            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                                .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.success_bg))
                                .setTextColor(ContextCompat.getColor(applicationContext, R.color.success_dark))
                                .show()

                            binding.llNoDataFound.visibility = View.VISIBLE
                            Log.d("postAttendanceTAG", "inside if-data-is-empty: " + response.body()?.Msg.toString())
                        } else if (s.data.isNotEmpty()) {
                            binding.llMainContent.visibility = View.VISIBLE

                            Log.d("postAttendanceTAG", "inside if-data-is-not-empty: " + response.body()?.Msg.toString())
                        }
                    } else {
                        binding.llInternalServerError.visibility = View.VISIBLE
                        Log.d("postAttendanceTAG", "inside else-status != 1: " + response.body()?.Msg.toString())
                    }

                } else {
                    Log.d("postAttendanceTAG", "inside else-unsuccessful: " + response.body()?.Msg.toString())
                    Toast.makeText(this@MarkAttendanceActivity, "Some error occurred.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MarkStudentAttendanceResponse>, t: Throwable) {
                binding.btnSubmitAttendance.visibility = View.VISIBLE
                binding.btnProgressBar.visibility = View.GONE
                Log.d("postAttendanceTAG", "onFailure, Error: ${t.message}")
                Toast.makeText(this@MarkAttendanceActivity, "An error has occurred. Please try again later",Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun resetAllAttendance() {
        students.forEach { it.isPresent = false }
        studentListAdapter.notifyDataSetChanged()
        updateHeaderStats()
        resetSwipeView()
    }

    // Handle back button
    override fun onBackPressed() {
        if (!isListView) {
            showListView()
        } else {
            super.onBackPressed()
        }
    }

    // Save state on rotation
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentSwipeIndex", currentSwipeIndex)
        outState.putBoolean("isListView", isListView)

        // Save attendance states
        val attendanceStates = students.map { it.isPresent }.toBooleanArray()
        outState.putBooleanArray("attendanceStates", attendanceStates)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentSwipeIndex = savedInstanceState.getInt("currentSwipeIndex", 0)
        isListView = savedInstanceState.getBoolean("isListView", true)

        // Restore attendance states
        val attendanceStates = savedInstanceState.getBooleanArray("attendanceStates")
        attendanceStates?.let { states ->
            students.forEachIndexed { index, student ->
                if (index < states.size) {
                    student.isPresent = states[index]
                }
            }
        }

        // Update UI
        if (isListView) {
            showListView()
        } else {
            showSwipeView()
        }

        studentListAdapter.notifyDataSetChanged()
        updateHeaderStats()
    }

}