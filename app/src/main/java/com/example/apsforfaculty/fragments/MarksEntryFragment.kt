package com.example.apsforfaculty.fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apsforfaculty.R
import com.example.apsforfaculty.adapters.StudentMarksAdapter
import com.example.apsforfaculty.classes.ApiClient
import com.example.apsforfaculty.classes.PrefsManager
import com.example.apsforfaculty.databinding.FragmentMarksEntryBinding
import com.example.apsforfaculty.models.UploadMarksStudent
import com.example.apsforfaculty.models.UploadMarksSubject
import com.example.apsforfaculty.requests.UploadMarksData
import com.example.apsforfaculty.requests.UploadMarksRequest
import com.example.apsforfaculty.responses.AssignedSubjectResponse
import com.example.apsforfaculty.responses.SectionWiseStudent
import com.example.apsforfaculty.responses.SectionWiseStudentListResponse
import com.example.apsforfaculty.responses.UploadMarksResponse
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call

class MarksEntryFragment : Fragment() {

    // only upload marks (here) and view marks fragments are left to done.......!!!!
    // sari constants value (jaha-jaha API me pas ho rahi hai, unki place me shared preferences ke through values ko put karna waha us jagah pe...)


    private lateinit var subject: UploadMarksSubject
    private lateinit var examName: String
    private lateinit var tvSubjectName: TextView
    private lateinit var etExam: EditText
    private lateinit var etSection: EditText
    private lateinit var btnSubmitMarks: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var studentMarksAdapter: StudentMarksAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var sstId: String = "-1"
    private lateinit var binding: FragmentMarksEntryBinding
    private var sstIdFlag: Boolean = false

    companion object {
        private const val ARG_SUBJECT_ID = "subject_id"
        private const val ARG_SUBJECT_NAME = "subject_name"
        private const val ARG_SECTION = "section"
        private const val ARG_TEACHER_NAME = "teacher_name"
        private const val ARG_EXAM_NAME = "exam_name"

        fun newInstance(subject: UploadMarksSubject, examName: String): MarksEntryFragment {
            val fragment = MarksEntryFragment()
            val bundle = Bundle().apply {
                putInt(ARG_SUBJECT_ID, subject.id)
                putString(ARG_SUBJECT_NAME, subject.subjectName)
                putString(ARG_SECTION, subject.section)
                putString(ARG_TEACHER_NAME, subject.teacherName)
                putString(ARG_EXAM_NAME, examName)
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
            examName = bundle.getString(ARG_EXAM_NAME, "")
        }
        sharedPreferences =
            requireContext().getSharedPreferences("student_marks", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMarksEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupRecyclerView()
        loadSstId()
        loadStudents()
        setupClickListeners()
    }

    private fun setupViews(view: View) {
        tvSubjectName = view.findViewById(R.id.tv_subject_name)
        etExam = view.findViewById(R.id.et_exam)
        etSection = view.findViewById(R.id.et_section)
        btnSubmitMarks = view.findViewById(R.id.btn_submit_marks)
        recyclerView = view.findViewById(R.id.recycler_students)

        tvSubjectName.text = "Subject Name : ${subject.subjectName}"
        etExam.setText(examName)
        etSection.setText(subject.section)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        studentMarksAdapter = StudentMarksAdapter()
        recyclerView.adapter = studentMarksAdapter
    }

    private fun loadSstId() {

        ApiClient.assignedSubjectInstance.getAssignedSubjects(
            "application/x-www-form-urlencoded",
            "ci_session=jb231bl4eo9i74ueppu4rvhca8nvkdhs",
            "33"
        ).enqueue(object : retrofit2.Callback<AssignedSubjectResponse> {
            override fun onResponse(
                call: Call<AssignedSubjectResponse>,
                response: retrofit2.Response<AssignedSubjectResponse>
            ) {

                if (response.isSuccessful) {
                    Log.d("assignedSubjectsTAG", response.body()?.Msg.toString())
                    val s = response.body()
                    if (s?.status == 1) {
                        if (s.data.isEmpty()) {
                            Log.d(
                                "assignedSubjectsTAG",
                                "inside if-data-is-empty: " + response.body()?.Msg.toString()
                            )
                        } else if (s.data.isNotEmpty()) {
                            sstId = s.data.get(0).sst_id
                            Log.d(
                                "assignedSubjectsTAG",
                                "inside if-data-is-not-empty: " + response.body()?.Msg.toString()
                            )
                        }
                    } else {
                        Snackbar.make(binding.root, s?.Msg!!, Snackbar.LENGTH_LONG).show()
                        Log.d(
                            "assignedSubjectsTAG",
                            "inside else-status != 1: " + response.body()?.Msg.toString()
                        )
                    }

                } else {
                    Log.d(
                        "assignedSubjectsTAG",
                        "inside else-unsuccessful: " + response.body()?.Msg.toString()
                    )
                    Toast.makeText(requireContext(), "Some error occurred.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<AssignedSubjectResponse>, t: Throwable) {
                Log.d("studentListDetailTAG", "onFailure, Error: ${t.message}")

                Toast.makeText(
                    requireContext(),
                    "An error has occurred. Please try again later",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })


    }

    private fun loadStudents() {

//        if (sstId == "-1") {
//            Snackbar.make(binding.root, "Subject is not assigned to this teacher. Please contact admin", Snackbar.LENGTH_LONG).show()
//            return
//        }

        ApiClient.sectionWiseStudentListDetailInstance.getSectionWiseStudents(
            "application/x-www-form-urlencoded",
            "ci_session=cof7n2dje9kjg780mnvl95au1rt7usof",
            "1"
        ).enqueue(object : retrofit2.Callback<SectionWiseStudentListResponse> { // 1 means sec_id
            override fun onResponse(
                call: Call<SectionWiseStudentListResponse>,
                response: retrofit2.Response<SectionWiseStudentListResponse>
            ) {

                if (response.isSuccessful) {
                    Log.d("studentListDetailTAG", response.body()?.Msg.toString())
                    val s = response.body()
                    if (s?.status == 1) {
                        if (s.data.isEmpty()) {
                            Snackbar.make(binding.root, "No data found", Snackbar.LENGTH_LONG)
                                .show()
                            Log.d(
                                "sectionWithSubjectsTAG",
                                "inside if-data-is-empty: " + response.body()?.Msg.toString()
                            )
                        } else if (s.data.isNotEmpty()) {
                            fillStudentsList(s.data)
                            Log.d(
                                "studentListDetailTAG",
                                "inside if-data-is-not-empty: " + response.body()?.Msg.toString()
                            )
                        }
                    } else {
                        Snackbar.make(binding.root, s?.Msg!!, Snackbar.LENGTH_LONG).show()
                        Log.d(
                            "studentListDetailTAG",
                            "inside else-status != 1: " + response.body()?.Msg.toString()
                        )
                    }

                } else {
                    Log.d(
                        "studentListDetailTAG",
                        "inside else-unsuccessful: " + response.body()?.Msg.toString()
                    )
                    Toast.makeText(requireContext(), "Some error occurred.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<SectionWiseStudentListResponse>, t: Throwable) {
                Log.d("studentListDetailTAG", "onFailure, Error: ${t.message}")

                Toast.makeText(
                    requireContext(),
                    "An error has occurred. Please try again later",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    private fun fillStudentsList(data: List<SectionWiseStudent>) {


        val students = mutableListOf<UploadMarksStudent>()
        for (i in 0 until data.size) {
            students.add(
                UploadMarksStudent(
                    i + 1,
                    data.get(i).sidinc,
                    data.get(i).name,
                    data.get(i).father
                )
            )
        }

//        val students = listOf(
//            UploadMarksStudent(1, "3737", "Adriti Sonker", "Devendra Sonker"),
//            UploadMarksStudent(2, "3495", "Afifa Azeem", "Fazle Azeem Khan"),
//            UploadMarksStudent(3, "4097", "Ali Ahmad", "Rizwan Ahmad"),
//            UploadMarksStudent(4, "3384", "Aliya", "Mohd Shahid"),
//            UploadMarksStudent(5, "3389", "Aliza Ansari", "Mo Aqeel Ansari"),
//            UploadMarksStudent(6, "3553", "Anshara Haseen", "Haseen Ahmad"),
//            UploadMarksStudent(7, "3499", "Areeqa Fatima", "Mohd Wasif Sheikh"),
//            UploadMarksStudent(8, "3496", "Ariket Singh", "Ravindra Kumar"),
//            UploadMarksStudent(9, "3561", "Asna khan", "Mr. Ishaq")
//        )

        studentMarksAdapter.submitStudentList(students)

    }

    private fun setupClickListeners() {
        btnSubmitMarks.setOnClickListener {
            val studentsWithMarks = studentMarksAdapter.getStudentsWithMarks()
            saveMarks(studentsWithMarks)
//            Toast.makeText(context, "Marks submitted successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveMarks(students: List<UploadMarksStudent>) {

        val uploadMarksData = mutableListOf<UploadMarksData>()
        for (i in 0 until students.size) {
            uploadMarksData.add(
                UploadMarksData(
                    students.get(i).sidIncNumber,
                    students.get(i).marks.toString()
                )
            )
            Log.d(
                "eeeexxxx",
                "Enrollment: " + students.get(i).sidIncNumber + ", Name: " + students.get(i).name + " Marks: " + students.get(
                    i
                ).marks.toString()
            )
        }

        val s = UploadMarksRequest(exam_id = "1", section_id = "1", "89", uploadMarksData)



        ApiClient.uploadMarksInstance.uploadMarks(
            "application/json",
            "ci_session=rkplba802cmta17s620r8f9vcmksh4m9",
            s
        ).enqueue(object : retrofit2.Callback<UploadMarksResponse> {
            override fun onResponse(
                call: Call<UploadMarksResponse>,
                response: retrofit2.Response<UploadMarksResponse>
            ) {

                if (response.isSuccessful) {
                    Log.d("studentListDetailTAG", response.body()?.Msg.toString())
                    val s = response.body()
                    if (s?.type.equals("success")) {
                        Toast.makeText(
                            requireContext(),
                            "Marks uploaded successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(
                            "uploadMarksTAG",
                            "inside if-data-is-not-empty: " + response.body()?.Msg.toString()
                        )
                        parentFragmentManager.popBackStack()

                    } else {
                        Snackbar.make(binding.root, s?.Msg!!, Snackbar.LENGTH_LONG).show()
                        Log.d(
                            "uploadMarksTAG",
                            "inside else-status != 1: " + response.body()?.Msg.toString()
                        )
                    }

                } else {
                    Log.d(
                        "uploadMarksTAG",
                        "inside else-unsuccessful: " + response.body()?.Msg.toString()
                    )
                    Toast.makeText(requireContext(), "Some error occurred.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<UploadMarksResponse>, t: Throwable) {
                Log.d("uploadMarksTAG", "onFailure, Error: ${t.message}")

                Toast.makeText(
                    requireContext(),
                    "An error has occurred. Please try again later",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })


//        val editor = sharedPreferences.edit()
//        val key = "marks_${subject.id}_${getExamId(examName)}"
//
//        // Save marks as comma-separated string
//        val marksString = students.joinToString(",") { "${it.id}:${it.marks}" }
//        editor.putString(key, marksString)
//        editor.apply()
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
