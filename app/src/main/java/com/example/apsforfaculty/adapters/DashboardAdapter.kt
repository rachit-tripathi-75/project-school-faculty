package com.example.apsforfaculty.adapters

import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.motion.widget.ViewTransition
import androidx.recyclerview.widget.RecyclerView
import com.example.apsforfaculty.R
import com.example.apsforfaculty.activities.MarkAttendanceActivity
import com.example.apsforfaculty.activities.NoticeBoardActivity
import com.example.apsforfaculty.activities.UploadMarksActivity
import com.example.apsforfaculty.activities.UploadPracticeSheetActivity
import com.example.apsforfaculty.activities.UploadWorkActivity
import com.example.apsforfaculty.activities.ViewAttendanceActivity
import com.example.apsforfaculty.activities.ViewTimeTableActivity
import com.google.android.material.card.MaterialCardView
import kotlin.jvm.java

data class DashboardItem(val iconResId: Int, val label: String)

class DashboardAdapter(val context: Context, private val items: List<DashboardItem>) :
    RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view as MaterialCardView
        val icon: ImageView = view.findViewById(R.id.icon)
        val label: TextView = view.findViewById(R.id.label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dashboard, parent, false)

        // Apply state list animators for elevation and scale
        val elevationAnimator = AnimatorInflater.loadStateListAnimator(
            parent.context,
            R.animator.card_elevation_animator
        )

        val scaleAnimator = AnimatorInflater.loadStateListAnimator(
            parent.context,
            R.animator.card_scale_animator
        )

        (view as MaterialCardView).stateListAnimator = elevationAnimator

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.icon.setImageResource(item.iconResId)
        holder.label.text = item.label

        // Add click animation
        holder.card.setOnClickListener { view ->
            animateCardClick(view)
            when (position) {
                0 -> {
                    context.startActivity(Intent(context, NoticeBoardActivity::class.java))
                }
                1 -> {
                    context.startActivity(Intent(context, MarkAttendanceActivity::class.java))
                }
                2 -> {
                    context.startActivity(Intent(context, ViewAttendanceActivity::class.java))
                }
                3 -> {
                    context.startActivity(Intent(context, UploadMarksActivity::class.java))
                }
                4 -> {
                    context.startActivity(Intent(context, UploadWorkActivity::class.java))
                }
//                4 -> {
//                    context.startActivity(Intent(context, UploadPracticeSheetActivity::class.java))
//                }
//                5 -> {
//                    context.startActivity(Intent(context, ViewTimeTableActivity::class.java))
//                }
            }
        }

    }

    private fun animateCardClick(view: View) {
        // Scale down
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                // Scale back up with slight bounce
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .setInterpolator(OvershootInterpolator(1.5f))
                    .start()
            }
            .start()
    }

    override fun getItemCount() = items.size
}