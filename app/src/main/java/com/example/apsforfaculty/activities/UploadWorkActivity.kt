package com.example.apsforfaculty.activities

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apsforfaculty.R
import com.example.apsforfaculty.adapters.AttachmentAdapter
import com.example.apsforfaculty.classes.ApiClient
import com.example.apsforfaculty.databinding.ActivityUploadMarksBinding
import com.example.apsforfaculty.databinding.ActivityUploadWorkBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class UploadWorkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadWorkBinding
    private lateinit var attachmentAdapter: AttachmentAdapter
    private val attachmentsList = mutableListOf<Uri>()

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        attachmentsList.addAll(uris)
        attachmentAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUploadWorkBinding.inflate(layoutInflater)
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
    }

    private fun setupRecyclerView() {
        attachmentAdapter = AttachmentAdapter(attachmentsList) { position ->
            removeAttachment(position)
        }
        binding.recyclerViewAttachments.apply {
            layoutManager = LinearLayoutManager(this@UploadWorkActivity)
            adapter = attachmentAdapter
        }
    }

    private fun setupClickListeners() {
        binding.cardUploadArea.setOnClickListener {
            filePickerLauncher.launch("*/*")
        }

        binding.etDueDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnUploadWork.setOnClickListener {
            if (attachmentsList.isEmpty()) {
                Toast.makeText(this, "Please select at least one file", Toast.LENGTH_SHORT).show()
            } else {
                attachmentsList.forEach { uri ->
                    uploadWork(uri)
                }
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"
                binding.etDueDate.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun removeAttachment(position: Int) {
        attachmentsList.removeAt(position)
        attachmentAdapter.notifyItemRemoved(position)
    }

    private fun uploadWork(uri: Uri) {
        binding.btnUploadWork.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
//        val subject = binding.spinnerSubject.text.toString()
//        val className = binding.spinnerClass.text.toString()
//        val title = binding.etTitle.text.toString()
//        val description = binding.etDescription.text.toString()
//        val dueDate = binding.etDueDate.text.toString()
//
//        val workType = when {
//            binding.radioHomework.isChecked -> "Homework"
//            binding.radioClasswork.isChecked -> "Classwork"
//            else -> ""
//        }
//
//        if (subject.isEmpty() || className.isEmpty() || title.isEmpty() || workType.isEmpty()) {
//            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // TODO: Implement actual upload logic
//        Toast.makeText(this, "Assignment uploaded successfully!", Toast.LENGTH_SHORT).show()


        if (!checkStoragePermission()) {
            Toast.makeText(this, "Please enable storage access", Toast.LENGTH_LONG).show()
            requestStoragePermission()
            return
        }

        val mimeType = getMimeType(uri)
        val file = File(getRealPathFromURI(uri))
        val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val sstId = "89".toRequestBody("text/plain".toMediaTypeOrNull())
        val teacherId = "33".toRequestBody("text/plain".toMediaTypeOrNull())

        val cookieHeader = "ci_session=c01int1mg0h9l6or858i8fe9hqbao3o0" // You can also store it in SharedPreferences

        lifecycleScope.launch {
            try {
                val response = ApiClient.uploadWorkInstance.uploadWork(cookieHeader, sstId, teacherId, body)
                if (response.isSuccessful) {
                    binding.btnUploadWork.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@UploadWorkActivity, "${file.name} uploaded!", Toast.LENGTH_SHORT).show()
                } else {
                    binding.btnUploadWork.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@UploadWorkActivity, "Failed: ${file.name}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.btnUploadWork.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@UploadWorkActivity, "Error uploading ${file.name}: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getMimeType(uri: Uri): String? {
        return contentResolver.getType(uri) ?: "*/*"
    }

    private fun getRealPathFromURI(uri: Uri): String {
        var result = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val fileName = cursor.getString(idx)

            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()
            result = file.absolutePath
            cursor.close()
        }
        return result
    }

    private fun checkStoragePermission(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

        ActivityCompat.requestPermissions(this, arrayOf(permission), 1001)
    }


}