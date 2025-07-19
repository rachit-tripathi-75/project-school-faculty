package com.example.apsforfaculty.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apsforfaculty.R
import com.example.apsforfaculty.models.UploadMarksStudent

class ViewMarksAdapter : ListAdapter<UploadMarksStudent, ViewMarksAdapter.ViewMarksViewHolder>(StudentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewMarksViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_marks, parent, false)
        return ViewMarksViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewMarksViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class ViewMarksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSrNo: TextView = itemView.findViewById(R.id.tv_sr_no)
        private val tvScholarNo: TextView = itemView.findViewById(R.id.tv_scholar_no)
        private val tvStudentName: TextView = itemView.findViewById(R.id.tv_student_name)
        private val tvMarks: TextView = itemView.findViewById(R.id.tv_marks)

        fun bind(student: UploadMarksStudent, position: Int) {
            tvSrNo.text = (position + 1).toString()
            tvScholarNo.text = student.sidIncNumber
            tvStudentName.text = student.name
            tvMarks.text = student.marks.toString()
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
