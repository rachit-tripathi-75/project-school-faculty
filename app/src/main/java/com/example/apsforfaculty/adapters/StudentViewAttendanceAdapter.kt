package com.example.apsforfaculty.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.apsforfaculty.R
import com.example.apsforfaculty.models.AttendanceStatus // ✅ Naya model import karna zaroori hai
import com.example.apsforfaculty.models.StudentViewAttendanceModel

class StudentViewAttendanceAdapter(
    private var studentList: List<StudentViewAttendanceModel>
) : RecyclerView.Adapter<StudentViewAttendanceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvStudentName: TextView = view.findViewById(R.id.tvStudentName)
        val tvStudentId: TextView = view.findViewById(R.id.tvStudentId)
        val tvRollNumber: TextView = view.findViewById(R.id.tvRollNumber)
        val tvAttendanceStatus: TextView = view.findViewById(R.id.tvAttendanceStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_attendance, parent, false)
        return ViewHolder(view)
    }

    // Yeh function bilkul sahi hai aur ismein koi change nahi chahiye.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = studentList[position]

        holder.tvStudentName.text = student.getDisplayName()
        holder.tvStudentId.text = student.getFormattedStudentId()
        holder.tvRollNumber.text = student.rollNumber
        holder.tvAttendanceStatus.text = student.getAttendanceStatus()

        holder.tvAttendanceStatus.setTextColor(
            ContextCompat.getColor(holder.itemView.context, student.getStatusColor())
        )
        holder.tvAttendanceStatus.setBackgroundResource(student.getStatusBackground())

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(student, position)
        }
    }

    override fun getItemCount() = studentList.size

    fun updateData(newStudentList: List<StudentViewAttendanceModel>) {
        studentList = newStudentList
        notifyDataSetChanged()
    }

    fun getStudentAt(position: Int): StudentViewAttendanceModel? {
        return if (position in 0 until studentList.size) {
            studentList[position]
        } else null
    }

    // ✅ YAHAN CHANGES KIYE GAYE HAIN
    fun getPresentStudents(): List<StudentViewAttendanceModel> {
        // Ab hum 'status' enum se check karenge
        return studentList.filter { it.status == AttendanceStatus.PRESENT }
    }

    // ✅ YAHAN BHI CHANGES KIYE GAYE HAIN
    fun getAbsentStudents(): List<StudentViewAttendanceModel> {
        // Ab hum 'status' enum se check karenge
        return studentList.filter { it.status == AttendanceStatus.ABSENT }
    }

    private var onItemClickListener: ((StudentViewAttendanceModel, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (StudentViewAttendanceModel, Int) -> Unit) {
        onItemClickListener = listener
    }
}