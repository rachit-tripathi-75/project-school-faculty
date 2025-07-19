package com.example.apsforfaculty.activities

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apsforfaculty.R
import com.example.apsforfaculty.adapters.TimetableAdapter
import com.example.apsforfaculty.databinding.ActivityViewTimeTableBinding
import com.example.apsforfaculty.models.TimetableItem
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ViewTimeTableActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewTimeTableBinding
    private lateinit var timetableAdapter: TimetableAdapter
    private var currentDate: Calendar = Calendar.getInstance()
    private lateinit var weekFormat: SimpleDateFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding = ActivityViewTimeTableBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initialisers()
        listeners()

    }

    private fun initialisers() {

        // Initialize date
        weekFormat = SimpleDateFormat("'Week of' MMMM d, yyyy", Locale.getDefault())
        updateDateDisplay()

        // Set up RecyclerView
        setupRecyclerView()

        // Set up click listeners

    }

    private fun listeners() {
        // Set up click listeners
        binding.prevWeekButton.setOnClickListener {
            currentDate.add(Calendar.WEEK_OF_YEAR, -1)
            updateDateDisplay()
            timetableAdapter.notifyDataSetChanged()
        }

        binding.nextWeekButton.setOnClickListener {
            currentDate.add(Calendar.WEEK_OF_YEAR, 1)
            updateDateDisplay()
            timetableAdapter.notifyDataSetChanged()
        }



        binding.calendarButton.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(currentDate.timeInMillis)
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                currentDate.timeInMillis = selection
                updateDateDisplay()
                timetableAdapter.notifyDataSetChanged()
            }

            datePicker.show(supportFragmentManager, "DATE_PICKER")
        }

        binding.addClassFab.setOnClickListener {
            Snackbar.make(it, "Add new class", Snackbar.LENGTH_SHORT).show()
            // TODO: Implement add class functionality
        }

    }


    private fun updateDateDisplay() {
        binding.selectedDateText.text = weekFormat.format(currentDate.time)
    }

    private fun setupRecyclerView() {
        // Create sample data
        val items = createSampleData()

        // Set up adapter
        timetableAdapter = TimetableAdapter(this, items)

        // Set up layout manager - 9 columns (1 for days + 8 periods)
        val layoutManager = ExpandedGridLayoutManager(this, 9)

        // Configure the layout manager to handle the fixed width cells
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // All cells have the same span size for a grid layout
                return 1
            }
        }

        binding.timetableRecyclerView.isNestedScrollingEnabled = false

        binding.timetableRecyclerView.layoutManager = layoutManager
        binding.timetableRecyclerView.adapter = timetableAdapter

        // Set fixed size to true for better performance
        binding.timetableRecyclerView.setHasFixedSize(false)
    }

    private fun createSampleData(): List<TimetableItem> {
        val items = mutableListOf<TimetableItem>()
        val days = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
        val periods = arrayOf(
            "1st\n9:05-9:55", "2nd\n9:55-10:45", "3rd\n10:55-11:45", "4th\n11:45-12:35",
            "5th\n13:25-14:15", "6th\n14:15-15:05", "7th\n15:15-16:05", "8th\n16:05-16:55"
        )

        // Add corner cell
        items.add(TimetableItem.Corner("Week View", 0, 0))

        // Add period headers
        for (i in periods.indices) {
            items.add(TimetableItem.Header(periods[i], 0, i + 1))
        }

        // Add day rows with classes
        for (day in days.indices) {
            // Add day cell
            items.add(TimetableItem.Day(days[day], day + 1, 0))

            // Add class cells for each period
            for (period in periods.indices) {
                // Special case for Wednesday
                if (day == 2) {
                    when (period) {
                        0 -> items.add(
                            TimetableItem.Class(
                                "Mathematics", day + 1, period + 1,
                                "Room 101", "Teacher 1", "#E3F2FD", "#1565C0"
                            )
                        )
                        1 -> items.add(
                            TimetableItem.Class(
                                "Physics", day + 1, period + 1,
                                "Lab 203", "Teacher 2", "#E1F5FE", "#0277BD"
                            )
                        )
                        2, 4, 7 -> items.add(TimetableItem.Free("Free Period", day + 1, period + 1))
                        3 -> items.add(
                            TimetableItem.Class(
                                "English", day + 1, period + 1,
                                "Room 302", "Teacher 3", "#E8F5E9", "#2E7D32"
                            )
                        )
                        5 -> items.add(
                            TimetableItem.Class(
                                "Computer Science", day + 1, period + 1,
                                "Lab 105", "Teacher 4", "#E0F7FA", "#00838F"
                            )
                        )
                        6 -> items.add(
                            TimetableItem.Class(
                                "History", day + 1, period + 1,
                                "Room 204", "Teacher 5", "#E1F5FE", "#0288D1"
                            )
                        )
                        else -> items.add(TimetableItem.Empty("N/A", day + 1, period + 1))
                    }
                } else {
                    // Empty cells for other days
                    items.add(TimetableItem.Empty("N/A", day + 1, period + 1))
                }
            }
        }

        return items
    }

    inner class ExpandedGridLayoutManager(
        context: Context,
        spanCount: Int
    ) : GridLayoutManager(context, spanCount) {

        override fun onMeasure(
            recycler: RecyclerView.Recycler,
            state: RecyclerView.State,
            widthSpec: Int,
            heightSpec: Int
        ) {
            val height = View.MeasureSpec.getSize(heightSpec)
            val expandedSpec = View.MeasureSpec.makeMeasureSpec(height * 10, View.MeasureSpec.AT_MOST)
            super.onMeasure(recycler, state, widthSpec, expandedSpec)
        }
    }


}