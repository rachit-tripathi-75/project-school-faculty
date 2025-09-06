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
import com.example.apsforfaculty.R
import com.example.apsforfaculty.activities.UploadMarksActivity
import com.example.apsforfaculty.adapters.SubjectAdapter
import com.example.apsforfaculty.classes.ApiClient
import com.example.apsforfaculty.classes.PrefsManager
import com.example.apsforfaculty.databinding.FragmentSubjectListBinding
import com.example.apsforfaculty.models.UploadMarksListModel
import com.example.apsforfaculty.responses.AssignedSubjectData
import com.example.apsforfaculty.responses.AssignedSubjectResponse
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import retrofit2.Call

class SubjectListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var subjectAdapter: SubjectAdapter
    private lateinit var binding: FragmentSubjectListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                val gson = Gson()
                Log.d("passedsubject", gson.toJson(subject))
                (activity as UploadMarksActivity).navigateToUploadMarks(subject)
            },
            onViewClick = { subject ->
                (activity as UploadMarksActivity).navigateToViewMarks(subject)
                val gson = Gson()
                Log.d("chosensubjectxxxx", gson.toJson(subject))
            }
        )

        recyclerView.adapter = subjectAdapter
    }

    private fun loadSubjects() {

//        val s = SectionWithSubjectRequest(PrefsManager.getTeacherDetailedInformation(requireContext()).data.id)
        Log.d("teacherxxxxxx", PrefsManager.getTeacherDetailedInformation(requireContext()).data.id)
        ApiClient.assignedSubjectInstance.getAssignedSubjects(
            "application/x-www-form-urlencoded",
            "ci_session=6f09sgi0v478hmmta4qima7flics5edd",
            PrefsManager.getTeacherDetailedInformation(requireContext()).data.id
        ).enqueue(object : retrofit2.Callback<AssignedSubjectResponse> {
            override fun onResponse(
                call: Call<AssignedSubjectResponse>,
                response: retrofit2.Response<AssignedSubjectResponse>
            ) {

                if (response.isSuccessful) {
                    Log.d("sectionWithSubjectsTAG", response.body()?.Msg.toString())
                    val s = response.body()
                    if (s?.status == 1) {
                        if (s.data.isEmpty()) {
                            Snackbar.make(binding.root, s.Msg, Snackbar.LENGTH_LONG).show()
                            val gson = Gson()
                            Log.d(
                                "sectionWithSubjectsTAG",
                                "inside if-data-is-empty: " + gson.toJson(response.body()?.data)
                            )
                        } else if (s.data.isNotEmpty()) {
                            fillSubjects(s.data)
                            val gson = Gson()
                            Log.d(
                                "sectionWithSubjectsTAG",
                                "inside if-data-is-not-empty: " + gson.toJson(response.body()?.data)
                            )
                        }
                    } else {
                        Snackbar.make(binding.root, s?.Msg!!, Snackbar.LENGTH_LONG).show()
                        Log.d(
                            "sectionWithSubjectsTAG",
                            "inside else-status != 1: " + response.body()?.Msg.toString()
                        )
                    }

                } else {
                    Log.d(
                        "sectionWithSubjectsTAG",
                        "inside else-unsuccessful: " + response.body()?.Msg.toString()
                    )
                    Toast.makeText(requireContext(), "Some error occurred.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<AssignedSubjectResponse>, t: Throwable) {
                Log.d("sectionWithSubjectsTAG", "onFailure, Error: ${t.message}")

                Toast.makeText(
                    requireContext(),
                    "An error has occurred. Please try again later",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })


    }

    // CORRECT version
    private fun fillSubjects(data: List<AssignedSubjectData>) {
        val subjects = mutableListOf<UploadMarksListModel>()
        val teacherName = PrefsManager.getUserInformation(requireContext()).data.name

        // Use only ONE loop
        for (subjectData in data) {
            subjects.add(
                UploadMarksListModel(
                    subjectData.sst_id,
                    subjectData.section_id,
                    subjectData.sub_id,
                    subjectData.sub_name,
                    subjectData.main_sec_name,
                    teacherName
                )
            )
        }

        subjectAdapter.submitList(subjects)
    }

    companion object {
        fun newInstance(): SubjectListFragment {
            return SubjectListFragment()
        }
    }
}
