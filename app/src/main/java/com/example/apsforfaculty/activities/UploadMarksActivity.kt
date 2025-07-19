package com.example.apsforfaculty.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apsforfaculty.R
import com.example.apsforfaculty.adapters.UploadMarksForStudentAdapter
import com.example.apsforfaculty.databinding.ActivityUploadMarksBinding
import com.example.apsforfaculty.fragments.MarksEntryFragment
import com.example.apsforfaculty.fragments.SubjectListFragment
import com.example.apsforfaculty.fragments.UploadMarksFragment
import com.example.apsforfaculty.fragments.ViewMarksFragment
import com.example.apsforfaculty.models.StudentMark
import com.example.apsforfaculty.models.UploadMarksSubject

class UploadMarksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadMarksBinding
    private lateinit var studentMarksAdapter: UploadMarksForStudentAdapter
    private val studentMarksList = mutableListOf<StudentMark>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUploadMarksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listeners()
        // Load initial fragment
        if (savedInstanceState == null) {
            loadFragment(SubjectListFragment())
        }

    }

    private fun listeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }


    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun navigateToUploadMarks(subject: UploadMarksSubject) {
        val fragment = UploadMarksFragment.newInstance(subject)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun navigateToMarksEntry(subject: UploadMarksSubject, exam: String) {
        val fragment = MarksEntryFragment.newInstance(subject, exam)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun navigateToViewMarks(subject: UploadMarksSubject) {
        val fragment = ViewMarksFragment.newInstance(subject)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}