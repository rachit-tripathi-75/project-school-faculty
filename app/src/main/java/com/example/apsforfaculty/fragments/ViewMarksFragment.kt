package com.example.apsforfaculty.fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apsforfaculty.R
import com.example.apsforfaculty.adapters.ViewMarksAdapter
import com.example.apsforfaculty.classes.ApiClient
import com.example.apsforfaculty.databinding.FragmentViewMarksBinding
import com.example.apsforfaculty.models.UploadMarksSubject
import com.example.apsforfaculty.models.UploadMarksStudent
import com.example.apsforfaculty.responses.ExamListResponse
import com.example.apsforfaculty.responses.StudentViewedMark
import com.example.apsforfaculty.responses.ViewMarksResponse
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import kotlin.collections.mutableListOf

class ViewMarksFragment : Fragment() {

    private lateinit var subject: UploadMarksSubject
    private lateinit var spinnerExam: Spinner
    private lateinit var spinnerSubject: Spinner
    private lateinit var btnSubmit: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewMarksAdapter: ViewMarksAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: FragmentViewMarksBinding

    companion object {
        private const val ARG_SUBJECT_ID = "subject_id"
        private const val ARG_SUBJECT_NAME = "subject_name"
        private const val ARG_SECTION = "section"
        private const val ARG_TEACHER_NAME = "teacher_name"

        fun newInstance(subject: UploadMarksSubject): ViewMarksFragment {
            val fragment = ViewMarksFragment()
            val bundle = Bundle().apply {
                putInt(ARG_SUBJECT_ID, subject.id)
                putString(ARG_SUBJECT_NAME, subject.subjectName)
                putString(ARG_SECTION, subject.section)
                putString(ARG_TEACHER_NAME, subject.teacherName)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            subject = UploadMarksSubject(
                id = bundle.getInt(ARG_SUBJECT_ID),
                section = bundle.getString(ARG_SECTION, ""),
                teacherName = bundle.getString(ARG_TEACHER_NAME, ""),
                subjectName = bundle.getString(ARG_SUBJECT_NAME, "")
            )
        }
        sharedPreferences = requireContext().getSharedPreferences("student_marks", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewMarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupSpinners()
        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupViews(view: View) {
        spinnerExam = view.findViewById(R.id.spinner_exam_view)
        spinnerSubject = view.findViewById(R.id.spinner_subject_view)
        btnSubmit = view.findViewById(R.id.btn_submit_view)
        recyclerView = view.findViewById(R.id.recycler_marks_view)
    }

    private fun setupSpinners() {
        // Setup exam spinner
        val exams = arrayOf("Unit test 1", "Unit test 2", "Mid Term", "Final Exam")
        val examAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, exams)
        examAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerExam.adapter = examAdapter

        // Setup subject spinner
        val subjects = arrayOf("Hindi Literature", "General Knowledge", "Moral Science", "Hindi Language")
        val subjectAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subjects)
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSubject.adapter = subjectAdapter
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        viewMarksAdapter = ViewMarksAdapter()
        recyclerView.adapter = viewMarksAdapter
    }

    private fun setupClickListeners() {
        btnSubmit.setOnClickListener {
            val selectedExam = spinnerExam.selectedItem.toString()
            loadMarks(selectedExam)
        }
    }

    private fun loadMarks(examName: String) {
//        val key = "marks_${subject.id}_${getExamId(examName)}"
//        val marksString = sharedPreferences.getString(key, "")
//
//        if (marksString.isNullOrEmpty()) {
//            Toast.makeText(context, "No marks found for selected exam", Toast.LENGTH_SHORT).show()
//            return
//        }


        ApiClient.viewMarksInstance.viewMarks(
            "application/x-www-form-urlencoded",
            "ci_session=e000a4cj27gv598f5dn2fpc7joc71ejr",
            examId = "1",
            sectionId = "1",
            sstId = "89").enqueue(object : retrofit2.Callback<ViewMarksResponse> {
            override fun onResponse(call: Call<ViewMarksResponse>, response: retrofit2.Response<ViewMarksResponse>) {

                if (response.isSuccessful) {
                    Log.d("viewMarksTAG", response.body()?.Msg.toString())
                    val s = response.body()
                    if (s?.status == 1) {
                        if (s.data.isEmpty()) {
                            Snackbar.make(binding.root, "No data found", Snackbar.LENGTH_LONG).show()
                            Log.d("viewMarksTAG", "inside if-data-is-empty: " + response.body()?.Msg.toString())
                        } else if (s.data.isNotEmpty()) {
                            fillViewMarksData(s.data)
                            Log.d("viewMarksTAG", "inside if-data-is-not-empty: " + response.body()?.Msg.toString())
                        }
                    } else {
                        Snackbar.make(binding.root, s?.Msg!!, Snackbar.LENGTH_LONG).show()
                        Log.d("viewMarksTAG", "inside else-status != 1: " + response.body()?.Msg.toString())
                    }

                } else {
                    Log.d("viewMarksTAG", "inside else-unsuccessful: " + response.body()?.Msg.toString())
                    Toast.makeText(requireContext(), "Some error occurred.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ViewMarksResponse>, t: Throwable) {
                Log.d("viewMarksTAG", "onFailure, Error: ${t.message}")
                Toast.makeText(requireContext(), "An error has occurred. Please try again later",Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun fillViewMarksData(data: List<StudentViewedMark>) {

        val students = mutableListOf<UploadMarksStudent>()

        for (i in 0 until data.size) {
            val item = data[i]
            students.add(UploadMarksStudent(i + 1, item.student_id, item.name, "", item.mark.toInt()))
        }



//        // Update students with their marks
//        students.forEach { student ->
//            student.marks = marksMap[student.id] ?: 0
//        }

        viewMarksAdapter.submitList(students)

    }

    private fun parseMarksString(marksString: String): Map<Int, Int> {
        val marksMap = mutableMapOf<Int, Int>()
        marksString.split(",").forEach { entry ->
            val parts = entry.split(":")
            if (parts.size == 2) {
                val studentId = parts[0].toIntOrNull()
                val marks = parts[1].toIntOrNull()
                if (studentId != null && marks != null) {
                    marksMap[studentId] = marks
                }
            }
        }
        return marksMap
    }

    private fun getStudentsList(): MutableList<UploadMarksStudent> {
        return mutableListOf(
            UploadMarksStudent(1, "3737", "Adriti Sonker", "Devendra Sonker"),
            UploadMarksStudent(2, "3495", "Afifa Azeem", "Fazle Azeem Khan"),
            UploadMarksStudent(3, "4097", "Ali Ahmad", "Rizwan Ahmad"),
            UploadMarksStudent(4, "3384", "Aliya", "Mohd Shahid"),
            UploadMarksStudent(5, "3389", "Aliza Ansari", "Mo Aqeel Ansari"),
            UploadMarksStudent(6, "3553", "Anshara Haseen", "Haseen Ahmad"),
            UploadMarksStudent(7, "3499", "Areeqa Fatima", "Mohd Wasif Sheikh"),
            UploadMarksStudent(8, "3496", "Ariket Singh", "Ravindra Kumar"),
            UploadMarksStudent(9, "3561", "Asna khan", "Mr. Ishaq")
        )
    }

    private fun getExamId(examName: String): Int {
        return when (examName) {
            "Unit test 1" -> 1
            "Unit test 2" -> 2
            "Mid Term" -> 3
            "Final Exam" -> 4
            else -> 0
        }
    }
}
