package com.example.apsforfaculty.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.apsforfaculty.R
import com.example.apsforfaculty.models.Student


class StudentListForAttendanceAdapter(private val students: List<Student>, private val onAttendanceToggle: (Student) -> Unit) : RecyclerView.Adapter<StudentListForAttendanceAdapter.StudentViewHolder>() {

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvRollNumber: TextView = itemView.findViewById(R.id.tvRollNumber)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val switchAttendance: SwitchCompat = itemView.findViewById(R.id.switchAttendance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_list, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        val context = holder.itemView.context

        holder.tvStudentName.text = student.name
        holder.tvRollNumber.text = "Enrollment No: ${student.rollNumber}"

        // Update status badge with improved colors
        if (student.isPresent) {
            holder.tvStatus.text = "Present"
            holder.tvStatus.setBackgroundResource(R.drawable.badge_present)
            holder.tvStatus.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.status_present_text
                )
            )
        } else {
            holder.tvStatus.text = "Absent"
            holder.tvStatus.setBackgroundResource(R.drawable.badge_absent)
            holder.tvStatus.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.status_absent_text
                )
            )
        }

        // Set switch state without triggering listener
        holder.switchAttendance.setOnCheckedChangeListener(null)
        holder.switchAttendance.isChecked = student.isPresent

        // Update switch colors
        val thumbColorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(
                ContextCompat.getColor(context, R.color.success),
                ContextCompat.getColor(context, R.color.outline)
            )
        )

        val trackColorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(
                ContextCompat.getColor(context, R.color.success_light),
                ContextCompat.getColor(context, R.color.surface_variant)
            )
        )

        holder.switchAttendance.thumbTintList = thumbColorStateList
        holder.switchAttendance.trackTintList = trackColorStateList

        // Set switch listener
        holder.switchAttendance.setOnCheckedChangeListener { _, _ ->
            onAttendanceToggle(student)
        }

        // Add subtle animation on item change
        holder.itemView.animate()
            .scaleX(0.98f)
            .scaleY(0.98f)
            .setDuration(50)
            .withEndAction {
                holder.itemView.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(50)
                    .start()
            }
            .start()
    }

    override fun getItemCount(): Int = students.size
}