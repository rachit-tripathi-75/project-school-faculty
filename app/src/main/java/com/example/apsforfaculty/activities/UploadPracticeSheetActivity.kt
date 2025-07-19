package com.example.apsforfaculty.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apsforfaculty.R
import com.example.apsforfaculty.adapters.AttachmentAdapter
import com.example.apsforfaculty.databinding.ActivityUploadPracticeSheetBinding
import com.google.android.material.chip.Chip

class UploadPracticeSheetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadPracticeSheetBinding
    private lateinit var attachmentAdapter: AttachmentAdapter
    private val attachmentsList = mutableListOf<Uri>()
    private val tagsList = mutableListOf<String>()

    private val topicsBySubject = mapOf(
        "Mathematics" to arrayOf("Algebra", "Geometry", "Trigonometry", "Calculus", "Statistics", "Custom Topic"),
        "Science" to arrayOf("Physics", "Chemistry", "Biology", "Environmental Science", "Custom Topic"),
        "English" to arrayOf("Grammar", "Literature", "Writing", "Reading Comprehension", "Custom Topic"),
        "History" to arrayOf("Ancient History", "Medieval History", "Modern History", "World Wars", "Custom Topic"),
        "Geography" to arrayOf("Physical Geography", "Human Geography", "Climate", "Natural Resources", "Custom Topic")
    )

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        attachmentsList.addAll(uris)
        attachmentAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUploadPracticeSheetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupSpinners()
        setupRecyclerView()
        setupClickListeners()

    }


    private fun setupSpinners() {
        val subjects = arrayOf("Mathematics", "Science", "English", "History", "Geography")
        val subjectAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subjects)
        binding.spinnerSubject.setAdapter(subjectAdapter)

        val classes = arrayOf("Class 6", "Class 7", "Class 8", "Class 9", "Class 10")
        val classAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, classes)
        binding.spinnerClass.setAdapter(classAdapter)

        val difficulties = arrayOf("Easy", "Medium", "Hard")
        val difficultyAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, difficulties)
        binding.spinnerDifficulty.setAdapter(difficultyAdapter)

        binding.spinnerSubject.setOnItemClickListener { _, _, _, _ ->
            updateTopicSpinner()
        }

        binding.spinnerTopic.setOnItemClickListener { _, _, _, _ ->
            val selectedTopic = binding.spinnerTopic.text.toString()
            binding.layoutCustomTopic.visibility = if (selectedTopic == "Custom Topic") {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun updateTopicSpinner() {
        val selectedSubject = binding.spinnerSubject.text.toString()
        val topics = topicsBySubject[selectedSubject] ?: arrayOf()
        val topicAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, topics)
        binding.spinnerTopic.setAdapter(topicAdapter)
        binding.spinnerTopic.text.clear()
        binding.layoutCustomTopic.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        attachmentAdapter = AttachmentAdapter(attachmentsList) { position ->
            removeAttachment(position)
        }
        binding.recyclerViewAttachments.apply {
            layoutManager = LinearLayoutManager(this@UploadPracticeSheetActivity)
            adapter = attachmentAdapter
        }
    }

    private fun setupClickListeners() {
        binding.cardUploadArea.setOnClickListener {
            filePickerLauncher.launch("*/*")
        }

        binding.btnAddTag.setOnClickListener {
            addTag()
        }

        binding.etNewTag.setOnEditorActionListener { _, _, _ ->
            addTag()
            true
        }

        binding.btnUploadPracticeSheet.setOnClickListener {
            uploadPracticeSheet()
        }
    }

    private fun addTag() {
        val newTag = binding.etNewTag.text.toString().trim()
        if (newTag.isNotEmpty() && !tagsList.contains(newTag)) {
            tagsList.add(newTag)
            addChipToGroup(newTag)
            binding.etNewTag.text?.clear()
        }
    }

    private fun addChipToGroup(tag: String) {
        val chip = Chip(this)
        chip.text = tag
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            binding.chipGroupTags.removeView(chip)
            tagsList.remove(tag)
        }
        binding.chipGroupTags.addView(chip)
    }

    private fun removeAttachment(position: Int) {
        attachmentsList.removeAt(position)
        attachmentAdapter.notifyItemRemoved(position)
    }

    private fun uploadPracticeSheet() {
        val subject = binding.spinnerSubject.text.toString()
        val className = binding.spinnerClass.text.toString()
        val topic = if (binding.spinnerTopic.text.toString() == "Custom Topic") {
            binding.etCustomTopic.text.toString()
        } else {
            binding.spinnerTopic.text.toString()
        }
        val title = binding.etTitle.text.toString()
        val difficulty = binding.spinnerDifficulty.text.toString()
        val description = binding.etDescription.text.toString()

        if (subject.isEmpty() || className.isEmpty() || topic.isEmpty() ||
            title.isEmpty() || difficulty.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Implement actual upload logic
        Toast.makeText(this, "Practice sheet uploaded successfully!", Toast.LENGTH_SHORT).show()
    }

}