package com.example.apsforfaculty.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apsforfaculty.R
import com.example.apsforfaculty.models.UploadMarksListModel
import com.example.apsforfaculty.models.UploadMarksSubject

// this is used in Uploading marks of student.........
class SubjectAdapter(private val onUploadClick: (UploadMarksListModel) -> Unit, private val onViewClick: (UploadMarksListModel) -> Unit) : ListAdapter<UploadMarksListModel, SubjectAdapter.SubjectViewHolder>(SubjectDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSrNo: TextView = itemView.findViewById(R.id.tv_sr_no)
        private val tvSection: TextView = itemView.findViewById(R.id.tv_section)
        private val tvTeacher: TextView = itemView.findViewById(R.id.tv_teacher)
        private val tvSubject: TextView = itemView.findViewById(R.id.tv_subject)
        private val btnUpload: ImageButton = itemView.findViewById(R.id.btn_upload)
        private val btnView: ImageButton = itemView.findViewById(R.id.btn_view)

        fun bind(subject: UploadMarksListModel) {
            tvSrNo.text = (adapterPosition + 1).toString()
            tvSection.text = subject.mainSectionName
            tvTeacher.text = subject.teacherName
            tvSubject.text = subject.subjectName

            btnUpload.setOnClickListener {
                onUploadClick(subject)
            }
            btnView.setOnClickListener { onViewClick(subject) }
        }
    }

    class SubjectDiffCallback : DiffUtil.ItemCallback<UploadMarksListModel>() {
        override fun areItemsTheSame(oldItem: UploadMarksListModel, newItem: UploadMarksListModel): Boolean {
            return oldItem.sstId == newItem.sstId
        }

        override fun areContentsTheSame(oldItem: UploadMarksListModel, newItem: UploadMarksListModel): Boolean {
            return oldItem == newItem
        }
    }
}
