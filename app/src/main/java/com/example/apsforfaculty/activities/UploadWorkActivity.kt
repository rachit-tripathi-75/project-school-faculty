package com.example.apsforfaculty.activities

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apsforfaculty.R
import com.example.apsforfaculty.adapters.AttachmentAdapter
import com.example.apsforfaculty.classes.ApiClient
import com.example.apsforfaculty.classes.PrefsManager
import com.example.apsforfaculty.databinding.ActivityUploadWorkBinding
import com.example.apsforfaculty.responses.AssignedSubjectData
import com.example.apsforfaculty.responses.AssignedSubjectResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class UploadWorkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadWorkBinding
    private lateinit var attachmentAdapter: AttachmentAdapter
    private val attachmentsList = mutableListOf<Uri>()

    // ## CHANGE 1: Add variables to manage subject list and selection
    private var subjectsList: List<AssignedSubjectData> = emptyList()
    private var selectedSstId: String? = null

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        attachmentsList.addAll(uris)
        attachmentAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialisers()
        setupRecyclerView()
        setupClickListeners()
        fetchAndDisplaySubjects()
    }

    private fun initialisers() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun fetchAndDisplaySubjects() {
        val teacherId = PrefsManager.getTeacherDetailedInformation(this).data.id
        val cookie = "ci_session=YOUR_VALID_SESSION_ID" // ⚠️ Use a real session ID
        val contentType = "application/x-www-form-urlencoded"

        ApiClient.assignedSubjectInstance.getAssignedSubjects(contentType, cookie, teacherId)
            .enqueue(object : retrofit2.Callback<AssignedSubjectResponse> {
                override fun onResponse(call: Call<AssignedSubjectResponse>, response: Response<AssignedSubjectResponse>) {
                    if (response.isSuccessful && response.body()?.status == 1) {
                        // Store the full subject list
                        subjectsList = response.body()?.data ?: emptyList()
                        binding.radioGroupSubjects.removeAllViews()

                        subjectsList.forEach { subject ->
                            val radioButton = RadioButton(this@UploadWorkActivity).apply {
                                text = subject.sub_name // Using the field from our data class
                                id = View.generateViewId()
                            }
                            binding.radioGroupSubjects.addView(radioButton)
                        }
                    } else {
                        Toast.makeText(this@UploadWorkActivity, "Failed to load subjects", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<AssignedSubjectResponse>, t: Throwable) {
                    Log.e("UploadWorkActivity", "Error fetching subjects", t)
                    Toast.makeText(this@UploadWorkActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
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

        // ## CHANGE 2: Add a listener to the RadioGroup to capture the selected sst_id
        binding.radioGroupSubjects.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById<RadioButton>(checkedId)
            // The index of the button in the group matches the index in our subjectsList
            val selectedIndex = group.indexOfChild(checkedRadioButton)

            if (selectedIndex != -1) {
                // Get the subject from our list and store its ID
                selectedSstId = subjectsList[selectedIndex].sst_id
                Log.d("UploadWorkActivity", "Selected Subject sst_id: $selectedSstId")
            }
        }

        binding.btnUploadWork.setOnClickListener {
            // Updated validation to check our new variable
            if (selectedSstId == null) {
                Toast.makeText(this, "Please select subject", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (attachmentsList.isEmpty()) {
                Toast.makeText(this, "Please select at least one file", Toast.LENGTH_SHORT).show()
            } else {
                // ## CHANGE 3: Pass the dynamic sst_id to the upload function
                attachmentsList.forEach { uri ->
                    uploadWork(uri, selectedSstId!!)
                }
            }
        }
    }

    private fun removeAttachment(position: Int) {
        attachmentsList.removeAt(position)
        attachmentAdapter.notifyItemRemoved(position)
    }

    // ## CHANGE 4: Update function signature to accept the sstId
    private fun uploadWork(uri: Uri, sstId: String) {
        binding.btnUploadWork.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        if (!checkStoragePermission()) {
            Toast.makeText(this, "Please enable storage access", Toast.LENGTH_LONG).show()
            requestStoragePermission()
            binding.btnUploadWork.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            return
        }

        val file = File(getRealPathFromURI(uri))
        val requestFile = file.asRequestBody(contentResolver.getType(uri)?.toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        // ## CHANGE 5: Use the dynamic sstId instead of the hardcoded "89"
        val sstIdBody = sstId.toRequestBody("text/plain".toMediaTypeOrNull())
        val teacherId = PrefsManager.getTeacherDetailedInformation(this).data.id.toRequestBody("text/plain".toMediaTypeOrNull())
        val cookieHeader = "ci_session=c01int1mg0h9l6or858i8fe9hqbao3o0"

        lifecycleScope.launch {
            try {
                // Pass the dynamic sstIdBody to the API call
                val response = ApiClient.uploadWorkInstance.uploadWork(cookieHeader, sstIdBody, teacherId, body)
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

    // ... (getRealPathFromURI, checkStoragePermission, requestStoragePermission functions remain the same) ...
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