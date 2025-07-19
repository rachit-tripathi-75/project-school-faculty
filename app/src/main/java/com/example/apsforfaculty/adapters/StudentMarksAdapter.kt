package com.example.apsforfaculty.adapters


import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apsforfaculty.R
import com.example.apsforfaculty.models.UploadMarksStudent
import com.example.apsforfaculty.models.UploadMarksSubject

class StudentMarksAdapter :
    ListAdapter<UploadMarksStudent, StudentMarksAdapter.StudentViewHolder>(StudentDiffCallback()) {

    private val students = mutableListOf<UploadMarksStudent>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_marks, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    fun submitStudentList(list: List<UploadMarksStudent>) {
        super.submitList(list)
        students.clear()
        students.addAll(list)
    }

    fun getStudentsWithMarks(): List<UploadMarksStudent> = students

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSrNo: TextView = itemView.findViewById(R.id.tv_sr_no)
        private val tvScholarNo: TextView = itemView.findViewById(R.id.tv_scholar_no)
        private val tvStudentName: TextView = itemView.findViewById(R.id.tv_student_name)
        private val tvFatherName: TextView = itemView.findViewById(R.id.tv_father_name)
        private val etMarks: EditText = itemView.findViewById(R.id.et_marks)

        fun bind(student: UploadMarksStudent, position: Int) {
            tvSrNo.text = (position + 1).toString()
            tvScholarNo.text = student.sidIncNumber
            tvStudentName.text = student.name
            tvFatherName.text = student.fatherName
            etMarks.setText(student.marks.toString())

            etMarks.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val updatedMarks = s.toString().toIntOrNull() ?: 0
                    students[adapterPosition].marks = updatedMarks
                }
            })
        }
    }

    class StudentDiffCallback : DiffUtil.ItemCallback<UploadMarksStudent>() {
        override fun areItemsTheSame(oldItem: UploadMarksStudent, newItem: UploadMarksStudent): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UploadMarksStudent, newItem: UploadMarksStudent): Boolean {
            return oldItem == newItem
        }
    }
}
