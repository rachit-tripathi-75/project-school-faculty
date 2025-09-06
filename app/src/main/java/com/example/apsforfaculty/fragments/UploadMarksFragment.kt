package com.example.apsforfaculty.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.apsforfaculty.activities.UploadMarksActivity
import com.example.apsforfaculty.R
import com.example.apsforfaculty.classes.ApiClient
import com.example.apsforfaculty.databinding.FragmentUploadMarksBinding
import com.example.apsforfaculty.models.UploadMarksListModel
import com.example.apsforfaculty.models.UploadMarksSubject
import com.example.apsforfaculty.responses.ExamListExam
import com.example.apsforfaculty.responses.ExamListResponse
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call

class UploadMarksFragment : Fragment() {

    private lateinit var subject: UploadMarksSubject
    private lateinit var tvSubjectName: TextView
    private lateinit var spinnerExam: Spinner
    private lateinit var etSection: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnBack: Button
    private var examList: List<ExamListExam> = emptyList()
    private lateinit var binding: FragmentUploadMarksBinding

    companion object {
        private const val ARG_SUBJECT_ID = "subject_id"
        private const val ARG_SUBJECT_NAME = "subject_name"
        private const val ARG_SECTION = "section"
        private const val ARG_TEACHER_NAME = "teacher_name"
        private const val ARG_SECTION_ID = "section_id"

        fun newInstance(subject: UploadMarksListModel): UploadMarksFragment {
            val fragment = UploadMarksFragment()
            val bundle = Bundle().apply {
                putString(ARG_SUBJECT_ID, subject.subId)
                putString(ARG_SUBJECT_NAME, subject.subjectName)
                putString(ARG_SECTION, subject.mainSectionName)
                putString(ARG_TEACHER_NAME, subject.teacherName)
                putString(ARG_SECTION_ID, subject.sectionId)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            subject = UploadMarksSubject(
                subId = bundle.getString(ARG_SUBJECT_ID),
                section = bundle.getString(ARG_SECTION, ""),
                teacherName = bundle.getString(ARG_TEACHER_NAME, ""),
                subjectName = bundle.getString(ARG_SUBJECT_NAME, ""),
                sectionId = bundle.getString(ARG_SECTION_ID, "")
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUploadMarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupExamSpinner()
        setupClickListeners()
    }

    private fun setupViews(view: View) {
        tvSubjectName = view.findViewById(R.id.tv_subject_name)
        spinnerExam = view.findViewById(R.id.spinner_exam)
        etSection = view.findViewById(R.id.et_section)
        btnSubmit = view.findViewById(R.id.btn_submit)
        btnBack = view.findViewById(R.id.btn_back)

        tvSubjectName.text = "Subject Name : ${subject.subjectName}"
        etSection.setText(subject.section)
    }

    private fun setupExamSpinner() {


        ApiClient.examListInstance.getExamList(
            "ci_session=88kve9h6dthcqpc5pbat6d9dab63b0rm").enqueue(object : retrofit2.Callback<ExamListResponse> {
            override fun onResponse(call: Call<ExamListResponse>, response: retrofit2.Response<ExamListResponse>) {

                if (response.isSuccessful) {
                    Log.d("examListTAG", response.body()?.msg.toString())
                    val s = response.body()
                    if (s?.status == 1) {
                        if (s.data.isEmpty()) {
                            Snackbar.make(binding.root, "No data found", Snackbar.LENGTH_LONG).show()
                            Log.d("viewAttendanceTAG", "inside if-data-is-empty: " + response.body()?.msg.toString())
                        } else if (s.data.isNotEmpty()) {
                            fillSpinner(s.data)
                            Log.d("examListTAG", "inside if-data-is-not-empty: " + response.body()?.msg.toString())
                        }
                    } else {
                        Snackbar.make(binding.root, s?.msg!!, Snackbar.LENGTH_LONG).show()
                        Log.d("examListTAG", "inside else-status != 1: " + response.body()?.msg.toString())
                    }

                } else {
                    Log.d("examListTAG", "inside else-unsuccessful: " + response.body()?.msg.toString())
                    Toast.makeText(requireContext(), "Some error occurred.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ExamListResponse>, t: Throwable) {
                Log.d("examListTAG", "onFailure, Error: ${t.message}")
                Toast.makeText(requireContext(), "An error has occurred. Please try again later",Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun fillSpinner(data: List<ExamListExam>) {
        examList = data
        val examNames = mutableListOf("--Select Exam--").apply {
            addAll(data.map {
                it.name
            })
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, examNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerExam.adapter = adapter
    }


    private fun setupClickListeners() {
        btnSubmit.setOnClickListener {
            val selectedPosition = spinnerExam.selectedItemPosition
            val selectedExam = spinnerExam.selectedItem.toString()
            if (selectedPosition > 0) {
                val selectedExamId = examList[selectedPosition - 1].id
                (activity as UploadMarksActivity).navigateToMarksEntry(subject, selectedExamId, selectedExam)
            } else {
                Toast.makeText(context, "Please select an exam", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}
