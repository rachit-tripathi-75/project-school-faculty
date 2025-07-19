package com.example.apsforfaculty.adapters


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apsforfaculty.R
import com.example.apsforfaculty.models.TimetableItem
import com.google.android.material.card.MaterialCardView

class TimetableAdapter(
    private val context: Context,
    private val items: List<TimetableItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TimetableItem.Corner -> VIEW_TYPE_CORNER
            is TimetableItem.Header -> VIEW_TYPE_HEADER
            is TimetableItem.Day -> VIEW_TYPE_DAY
            is TimetableItem.Class -> VIEW_TYPE_CLASS
            is TimetableItem.Empty -> VIEW_TYPE_EMPTY
            is TimetableItem.Free -> VIEW_TYPE_FREE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)

        return when (viewType) {
            VIEW_TYPE_CORNER -> CornerViewHolder(
                inflater.inflate(R.layout.item_timetable_corner, parent, false)
            )
            VIEW_TYPE_HEADER -> HeaderViewHolder(
                inflater.inflate(R.layout.item_timetable_header, parent, false)
            )
            VIEW_TYPE_DAY -> DayViewHolder(
                inflater.inflate(R.layout.item_timetable_day, parent, false)
            )
            VIEW_TYPE_CLASS -> ClassViewHolder(
                inflater.inflate(R.layout.item_timetable_class, parent, false)
            )
            VIEW_TYPE_FREE -> FreeViewHolder(
                inflater.inflate(R.layout.item_timetable_free_period, parent, false)
            )
            else -> EmptyViewHolder(
                inflater.inflate(R.layout.item_timetable_empty, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is CornerViewHolder -> holder.bind(item as TimetableItem.Corner)
            is HeaderViewHolder -> holder.bind(item as TimetableItem.Header)
            is DayViewHolder -> holder.bind(item as TimetableItem.Day)
            is ClassViewHolder -> holder.bind(item as TimetableItem.Class)
            is FreeViewHolder -> {} // No binding needed for free periods
            is EmptyViewHolder -> {} // No binding needed for empty cells
        }
    }

    override fun getItemCount(): Int = items.size

    // ViewHolders
    class CornerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView as TextView

        fun bind(item: TimetableItem.Corner) {
            textView.text = item.text
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView as TextView

        fun bind(item: TimetableItem.Header) {
            textView.text = item.text
        }
    }

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView as TextView

        fun bind(item: TimetableItem.Day) {
            textView.text = item.text

            // Highlight Wednesday
            if (item.row == 3) { // Wednesday is row 3
                textView.setBackgroundResource(R.drawable.cell_day_current)
            }
        }
    }

    class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val subjectText: TextView = itemView.findViewById(R.id.subjectText)
        private val roomText: TextView = itemView.findViewById(R.id.roomText)
        private val teacherText: TextView = itemView.findViewById(R.id.teacherText)
        private val classCard: MaterialCardView = itemView.findViewById(R.id.classCard)

        fun bind(item: TimetableItem.Class) {
            subjectText.text = item.text
            roomText.text = item.room
            teacherText.text = item.teacher

            // Set colors
            classCard.setCardBackgroundColor(Color.parseColor(item.backgroundColor))
            subjectText.setTextColor(Color.parseColor(item.textColor))
            roomText.setTextColor(Color.parseColor(item.textColor))
            teacherText.setTextColor(Color.parseColor(item.textColor))
        }
    }

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class FreeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private const val VIEW_TYPE_CORNER = 0
        private const val VIEW_TYPE_HEADER = 1
        private const val VIEW_TYPE_DAY = 2
        private const val VIEW_TYPE_CLASS = 3
        private const val VIEW_TYPE_EMPTY = 4
        private const val VIEW_TYPE_FREE = 5
    }
}