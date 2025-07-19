package com.example.apsforfaculty.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apsforfaculty.models.StudentMark
import com.example.apsforfaculty.databinding.ItemStudentMarkBinding


class UploadMarksForStudentAdapter(
    private val studentMarksList: MutableList<StudentMark>,
    private val onRemoveClick: (Int) -> Unit
) : RecyclerView.Adapter<UploadMarksForStudentAdapter.StudentMarkViewHolder>() {

    inner class StudentMarkViewHolder(private val binding: ItemStudentMarkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(studentMark: StudentMark, position: Int) {
            binding.apply {
                // Set current values
                etStudentName.setText(studentMark.name)
                etStudentMarks.setText(studentMark.marks)

                // Add text change listeners to update the model
                etStudentName.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        studentMark.name = etStudentName.text.toString()
                    }
                }

                etStudentMarks.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        studentMark.marks = etStudentMarks.text.toString()
                    }
                }

                // Show/hide remove button based on list size
                btnRemoveStudent.visibility = if (studentMarksList.size > 1) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }

                // Set remove button click listener
                btnRemoveStudent.setOnClickListener {
                    onRemoveClick(position)
                }

                // Set hint for student number
                tilStudentName.hint = "Student ${position + 1} Name"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentMarkViewHolder {
        val binding = ItemStudentMarkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudentMarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentMarkViewHolder, position: Int) {
        holder.bind(studentMarksList[position], position)
    }

    override fun getItemCount(): Int = studentMarksList.size

    fun updateStudentMark(position: Int, name: String, marks: String) {
        if (position < studentMarksList.size) {
            studentMarksList[position].name = name
            studentMarksList[position].marks = marks
        }
    }

    fun getValidStudentMarks(): List<StudentMark> {
        return studentMarksList.filter {
            it.name.isNotBlank() && it.marks.isNotBlank()
        }
    }
}