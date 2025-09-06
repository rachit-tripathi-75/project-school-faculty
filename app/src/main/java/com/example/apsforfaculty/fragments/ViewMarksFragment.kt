package com.example.apsforfaculty.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apsforfaculty.R
import com.example.apsforfaculty.adapters.ViewMarksAdapter
import com.example.apsforfaculty.classes.ApiClient
import com.example.apsforfaculty.classes.PrefsManager
import com.example.apsforfaculty.databinding.FragmentViewMarksBinding
import com.example.apsforfaculty.models.UploadMarksListModel
import com.example.apsforfaculty.models.UploadMarksStudent
import com.example.apsforfaculty.models.UploadMarksSubject
import com.example.apsforfaculty.responses.AssignedSubjectResponse
import com.example.apsforfaculty.responses.ExamListExam
import com.example.apsforfaculty.responses.ExamListResponse
import com.example.apsforfaculty.responses.StudentViewedMark
import com.example.apsforfaculty.responses.ViewMarksResponse
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewMarksFragment : Fragment() {

    private lateinit var subject: UploadMarksSubject
    private lateinit var viewMarksAdapter: ViewMarksAdapter
    private var _binding: FragmentViewMarksBinding? = null
    private val binding get() = _binding!!
    private lateinit var sstId: String
    private lateinit var sectionId: String

    // ## CHANGE 1: Property to hold the fetched list of exams
    private var examList: List<ExamListExam> = emptyList()

    // ... (Companion object and onCreate remain the same) ...

    companion object {
        private const val ARG_SUBJECT_ID = "subject_id"
        private const val ARG_SUBJECT_NAME = "subject_name"
        private const val ARG_SECTION = "section"
        private const val ARG_TEACHER_NAME = "teacher_name"
        private const val ARG_SECTION_ID = "section_id"

        fun newInstance(subject: UploadMarksListModel): ViewMarksFragment {
            val fragment = ViewMarksFragment()
            val bundle = Bundle().apply {
                putInt(ARG_SUBJECT_ID, subject.subId.toInt())
                putString(ARG_SUBJECT_NAME, subject.subjectName)
                putString(ARG_SECTION, subject.mainSectionName)
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
                subId = bundle.getString(ARG_SUBJECT_ID),
                section = bundle.getString(ARG_SECTION, ""),
                teacherName = bundle.getString(ARG_TEACHER_NAME, ""),
                subjectName = bundle.getString(ARG_SUBJECT_NAME, ""),
                sectionId = bundle.getString(ARG_SECTION_ID, "")
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewMarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the chosen subject name
        binding.tvChosenSubject.text = subject.subjectName

        setupRecyclerView()
        setupClickListeners()

        // ## CHANGE 2: Fetch exams from API to populate the spinner
        fetchExams()
    }

    // ## CHANGE 3: New function to fetch exams
    private fun fetchExams() {
        // You can show a progress bar here
        ApiClient.examListInstance.getExamList("ci_session=88kve9h6dthcqpc5pbat6d9dab63b0rm")
            .enqueue(object : Callback<ExamListResponse> {
                override fun onResponse(call: Call<ExamListResponse>, response: Response<ExamListResponse>) {
                    // Hide progress bar here
                    if (response.isSuccessful && response.body()?.status == 1) {
                        val exams = response.body()?.data
                        if (!exams.isNullOrEmpty()) {
                            this@ViewMarksFragment.examList = exams // Store the full list
                            val examNames = exams.map { it.name } // Get just the names for the spinner
                            setupExamSpinner(examNames)
                        } else {
                            Toast.makeText(context, "No active exams found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Failed to load exams", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ExamListResponse>, t: Throwable) {
                    // Hide progress bar here
                    Log.e("ViewMarksFragment", "onFailure: fetchExams", t)
                    Toast.makeText(context, "Network error while fetching exams", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // ## CHANGE 4: Renamed and updated spinner setup function
    private fun setupExamSpinner(examNames: List<String>) {
        val examAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, examNames)
        examAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerExamView.adapter = examAdapter
    }

    private fun setupRecyclerView() {
        binding.recyclerMarksView.layoutManager = LinearLayoutManager(context)
        viewMarksAdapter = ViewMarksAdapter()
        binding.recyclerMarksView.adapter = viewMarksAdapter
    }

    private fun setupClickListeners() {
        binding.btnSubmitView.setOnClickListener {
            val chosenSubject = binding.tvChosenSubject.text.toString()
            val selectedPosition = binding.spinnerExamView.selectedItemPosition

            // ## CHANGE 5: Get the ID from the stored list based on spinner position
            if (selectedPosition != -1 && examList.isNotEmpty()) {
                val selectedExamId = examList[selectedPosition].id
                fetchSubjectDetailsAndLoadMarks(chosenSubject, selectedExamId)
            } else {
                Toast.makeText(context, "Please select an exam.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ## CHANGE 6: Updated function to accept examId
    private fun fetchSubjectDetailsAndLoadMarks(chosenSubject: String, examId: String) {
        val teacherId = PrefsManager.getTeacherDetailedInformation(requireContext()).data.id
        Log.d("teacheridxxx", teacherId)
        ApiClient.assignedSubjectInstance.getAssignedSubjects(
            "application/x-www-form-urlencoded",
            "ci_session=YOUR_DYNAMIC_SESSION_ID",
            teacherId
        ).enqueue(object : Callback<AssignedSubjectResponse> {
            override fun onResponse(call: Call<AssignedSubjectResponse>, response: Response<AssignedSubjectResponse>) {
                if (response.isSuccessful && response.body()?.status == 1) {
                    val foundSubject = response.body()?.data?.find {
                        it.sub_name.trim().equals(chosenSubject, ignoreCase = true)
                    }

                    if (foundSubject != null) {
                        Log.d("ViewMarksFragment", "Found subject: sst_id=${foundSubject.sst_id}, section_id=${foundSubject.section_id}")
                        // ## CHANGE 7: Pass the examId to loadMarks
                        loadMarks(
                            examId = examId,
                            sectionId = foundSubject.section_id,
                            sstId = foundSubject.sst_id
                        )
                    } else {
                        Snackbar.make(binding.root, "Details for '$chosenSubject' not found.", Snackbar.LENGTH_LONG).show()
                    }
                } else {
                    val errorMsg = response.body()?.Msg ?: "Failed to get subject details."
                    Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<AssignedSubjectResponse>, t: Throwable) {
                Log.e("ViewMarksFragment", "onFailure: getAssignedSubjects", t)
                Toast.makeText(requireContext(), "Network error. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ## CHANGE 8: Updated function signature for loadMarks
    private fun loadMarks(examId: String, sectionId: String, sstId: String) {
        ApiClient.viewMarksInstance.viewMarks(
            "application/x-www-form-urlencoded",
            "ci_session=YOUR_DYNAMIC_SESSION_ID",
            examId = examId, // Use the passed examId directly
            sectionId = sectionId,
            sstId = sstId
        ).enqueue(object : Callback<ViewMarksResponse> {
            override fun onResponse(call: Call<ViewMarksResponse>, response: Response<ViewMarksResponse>) {
                if (response.isSuccessful && response.body()?.status == 1) {
                    val marksData = response.body()?.data
                    if (marksData.isNullOrEmpty()) {
                        Snackbar.make(binding.root, "No marks found for this exam", Snackbar.LENGTH_LONG).show()
                        viewMarksAdapter.submitList(emptyList())
                    } else {
                        fillViewMarksData(marksData)
                    }
                } else {
                    val errorMsg = response.body()?.Msg ?: "Failed to load marks."
                    Snackbar.make(binding.root, errorMsg, Snackbar.LENGTH_LONG).show()
                    viewMarksAdapter.submitList(emptyList())
                }
            }

            override fun onFailure(call: Call<ViewMarksResponse>, t: Throwable) {
                Log.e("ViewMarksFragment", "onFailure: viewMarks", t)
                Toast.makeText(requireContext(), "An error has occurred. Please try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fillViewMarksData(data: List<StudentViewedMark>) {
        val students = data.mapIndexed { index, item ->
            UploadMarksStudent(index + 1, item.student_id, item.name, "", item.mark.toIntOrNull() ?: 0)
        }
        viewMarksAdapter.submitList(students)
    }

    // ## CHANGE 9: The getExamId function is no longer needed and can be deleted.

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}