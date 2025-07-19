package com.example.apsforfaculty.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apsforfaculty.activities.UploadMarksActivity
import com.example.apsforfaculty.R
import com.example.apsforfaculty.activities.MarkAttendanceActivity
import com.example.apsforfaculty.adapters.SubjectAdapter
import com.example.apsforfaculty.classes.ApiClient
import com.example.apsforfaculty.classes.ApiServices
import com.example.apsforfaculty.classes.PrefsManager
import com.example.apsforfaculty.databinding.FragmentSubjectListBinding
import com.example.apsforfaculty.models.UploadMarksSubject
import com.example.apsforfaculty.requests.SectionWithSubjectRequest
import com.example.apsforfaculty.responses.Section
import com.example.apsforfaculty.responses.SectionWithSubjectResponse
import com.example.apsforfaculty.responses.ViewAttendanceResponse
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call

class SubjectListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var subjectAdapter: SubjectAdapter
    private lateinit var binding: FragmentSubjectListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSubjectListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        loadSubjects()
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_subjects)
        recyclerView.layoutManager = LinearLayoutManager(context)

        subjectAdapter = SubjectAdapter(
            onUploadClick = { subject ->
                (activity as UploadMarksActivity).navigateToUploadMarks(subject)
            },
            onViewClick = { subject ->
                (activity as UploadMarksActivity).navigateToViewMarks(subject)
            }
        )

        recyclerView.adapter = subjectAdapter
    }

    private fun loadSubjects() {

        val s = SectionWithSubjectRequest("33")

        ApiClient.subjectRequestInstance.getSectionWithSubjects(
            "application/json",
            "ci_session=6f09sgi0v478hmmta4qima7flics5edd",
            s).enqueue(object : retrofit2.Callback<SectionWithSubjectResponse> {
            override fun onResponse(call: Call<SectionWithSubjectResponse>, response: retrofit2.Response<SectionWithSubjectResponse>) {

                if (response.isSuccessful) {
                    Log.d("viewAttendanceTAG", response.body()?.Msg.toString())
                    val s = response.body()
                    if (s?.status == 1) {
                        if (s.data.isEmpty()) {
                            Snackbar.make(binding.root, "No data found", Snackbar.LENGTH_LONG).show()
                            Log.d("sectionWithSubjectsTAG", "inside if-data-is-empty: " + response.body()?.Msg.toString())
                        } else if (s.data.isNotEmpty()) {
                            fillSubjects(s.data)
                            Log.d("sectionWithSubjectsTAG", "inside if-data-is-not-empty: " + response.body()?.Msg.toString())
                        }
                    } else {
                        Snackbar.make(binding.root, s?.Msg!!, Snackbar.LENGTH_LONG).show()
                        Log.d("sectionWithSubjectsTAG", "inside else-status != 1: " + response.body()?.Msg.toString())
                    }

                } else {
                    Log.d("sectionWithSubjectsTAG", "inside else-unsuccessful: " + response.body()?.Msg.toString())
                    Toast.makeText(requireContext(), "Some error occurred.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SectionWithSubjectResponse>, t: Throwable) {
                Log.d("sectionWithSubjectsTAG", "onFailure, Error: ${t.message}")

                Toast.makeText(requireContext(), "An error has occurred. Please try again later",Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun fillSubjects(data: List<Section>) {
        val subjects = mutableListOf<UploadMarksSubject>()

        for (section in data) {
            val sectionId = section.section_id.toIntOrNull() ?: continue
            val sectionName = section.section_name
            val teacherName = PrefsManager.getUserInformation(requireContext()).data.name

            for (subject in section.subjects) {
                subjects.add(
                    UploadMarksSubject(
                        id = subject.sub_id.toIntOrNull() ?: continue,
                        section = sectionName,
                        teacherName = teacherName,
                        subjectName = subject.sub_name
                    )
                )
            }
        }

        subjectAdapter.submitList(subjects)
    }

    companion object {
        fun newInstance(): SubjectListFragment {
            return SubjectListFragment()
        }
    }
}
