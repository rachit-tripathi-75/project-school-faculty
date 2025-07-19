package com.example.apsforfaculty.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TableRow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.apsforfaculty.R
import com.example.apsforfaculty.classes.ApiClient
import com.example.apsforfaculty.classes.PrefsManager
import com.example.apsforfaculty.databinding.FragmentTimeTableBinding
import com.example.apsforfaculty.databinding.PeriodHeaderItemBinding
import com.example.apsforfaculty.networks.NetworkChangeReceiver
import com.example.apsforfaculty.requests.TimeTableRequest
import com.example.apsforfaculty.responses.TimeTableResponse
import com.example.apsforfaculty.viewmodels.TimeTableViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.getValue


class TimeTableFragment : Fragment() {

    private lateinit var binding: FragmentTimeTableBinding
    private lateinit var periodHeaderItemBinding: PeriodHeaderItemBinding
    private val timeTableViewModel: TimeTableViewModel by viewModels()


    private val periods = listOf(
        Period(1, "9:05-9:55"),
        Period(2, "9:55-10:45"),
        Period(3, "10:55-11:45"),
        Period(4, "11:45-12:35"),
        Period(5, "12:35-14:15"),
        Period(6, "14:15-15:05"),
        Period(7, "15:15-16:05"),
        Period(8, "16:05-16:55"),
        Period(9, "17:30-19:30")
    )

    private val classes = listOf(
        ClassInfo("Mathematics", "Room 101", "Teacher 1"),
        ClassInfo("Physics", "Lab 203", "Teacher 2"),
        ClassInfo("Computer Science", "Lab 105", "Teacher 3"),
        ClassInfo("Literature", "Room 302", "Teacher 4"),
        ClassInfo("Chemistry", "Lab 201", "Teacher 5"),
        ClassInfo("History", "Room 204", "Teacher 6"),
        ClassInfo("Physical Education", "Gymnasium", "Teacher 7"),
        ClassInfo("Art", "Studio 101", "Teacher 9"),
        ClassInfo("Free Period", "Study Hall", "")
    )



    var networkChangeReceiver: NetworkChangeReceiver = NetworkChangeReceiver(object : NetworkChangeReceiver.NetworkStatusListener {
        override fun onNetworkConnected() {
            binding.llNoInternetFound.visibility = View.GONE
            fetchTimeTable()
            Log.d("networkInterceptorTAG", "inside onNetworkConnected()")

        }

        override fun onNetworkDisconnected() {
            binding.horizontalScrollView.visibility = View.GONE
            binding.llNoInternetFound.visibility = View.VISIBLE
            binding.llSunday.visibility = View.GONE
            Log.d("networkInterceptorTAG", "inside onNetworkDisconnected()")
            Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_LONG).show()
        }
    })



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimeTableBinding.inflate(inflater, container, false)
        periodHeaderItemBinding = PeriodHeaderItemBinding.inflate(inflater, container, false)

        initialisers()

        return binding.root
    }

    private fun initialisers() {
        setupDateDisplay()
        fetchTimeTable()
        setupScheduleTable()
        startAnimations()

        // Start a timer to check current period
//        Timer().scheduleAtFixedRate(object : TimerTask() {
//            override fun run() {
//                runOnUiThread {
//                    updateCurrentPeriod()
//                }
//            }
//        }, 0, 60000) // Check every minute
    }

    private fun setupDateDisplay() {
        val sdf = SimpleDateFormat("EEEE [ yyyy-MM-dd ]", Locale.getDefault())
//        binding.tvDate.text = sdf.format(Date())
    }

    private fun setupScheduleTable() {

        timeTableViewModel.timeTableData.observe(viewLifecycleOwner) { timeTableData ->
            if (timeTableData.data.isEmpty()) {
                binding.llNoTimeTableDataFound.visibility = View.VISIBLE
                binding.horizontalScrollView.visibility = View.GONE
                return@observe
            }
            for (i in 0 until timeTableData.data.size) {
                when (timeTableData.data[i].tpId.toInt()) {
                    1 -> {
                        binding.Period1.tvClassSubjectName.text = timeTableData.data[i].subName
                        binding.Period1.tvRoomNumber.text = "Room Number: ${timeTableData.data[i].rm}"
                        binding.Period1.chipTeacherName.text = timeTableData.data[i].name
                        binding.Period1.tvRoomNumber.visibility = View.VISIBLE
                        binding.Period1.chipTeacherName.visibility = View.VISIBLE
                        binding.Period1.llFreeTimeClock.visibility = View.GONE
//                        binding.Period1.currentChip.visibility = if (isTimeBetween(9, 5, 9, 55)) View.VISIBLE else View.GONE
                    }

                    2 -> {
                        binding.Period2.tvClassSubjectName.text = timeTableData.data[i].subName
                        binding.Period2.tvRoomNumber.text = "Room Number: ${timeTableData.data[i].rm}"
                        binding.Period2.chipTeacherName.text = timeTableData.data[i].name
                        binding.Period2.tvRoomNumber.visibility = View.VISIBLE
                        binding.Period2.chipTeacherName.visibility = View.VISIBLE
                        binding.Period2.llFreeTimeClock.visibility = View.GONE
//                        binding.Period2.currentChip.visibility = if (isTimeBetween(10, 5, 10, 55)) View.VISIBLE else View.GONE
                    }

                    3 -> {
                        binding.Period3.tvClassSubjectName.text = timeTableData.data[i].subName
                        binding.Period3.tvRoomNumber.text = "Room Number: ${timeTableData.data[i].rm}"
                        binding.Period3.chipTeacherName.text = timeTableData.data[i].name
                        binding.Period3.tvRoomNumber.visibility = View.VISIBLE
                        binding.Period3.chipTeacherName.visibility = View.VISIBLE
                        binding.Period3.llFreeTimeClock.visibility = View.GONE
//                        binding.Period3.currentChip.visibility = if (isTimeBetween(11, 5, 11, 55)) View.VISIBLE else View.GONE
                    }

                    4 -> {
                        binding.Period4.tvClassSubjectName.text = timeTableData.data[i].subName
                        binding.Period4.tvRoomNumber.text = "Room Number: ${timeTableData.data[i].rm}"
                        binding.Period4.chipTeacherName.text = timeTableData.data[i].name
                        binding.Period4.tvRoomNumber.visibility = View.VISIBLE
                        binding.Period4.chipTeacherName.visibility = View.VISIBLE
                        binding.Period4.llFreeTimeClock.visibility = View.GONE
//                        binding.Period4.currentChip.visibility = if (isTimeBetween(12, 5, 12, 55)) View.VISIBLE else View.VISIBLE
                    }

                    5 -> {
                        binding.Period5.tvClassSubjectName.text = timeTableData.data[i].subName
                        binding.Period5.tvRoomNumber.text = "Room Number: ${timeTableData.data[i].rm}"
                        binding.Period5.chipTeacherName.text = timeTableData.data[i].name
                        binding.Period5.tvRoomNumber.visibility = View.VISIBLE
                        binding.Period5.chipTeacherName.visibility = View.VISIBLE
                        binding.Period5.llFreeTimeClock.visibility = View.GONE
//                        binding.Period5.currentChip.visibility = if (isTimeBetween(13, 5, 13, 55)) View.VISIBLE else View.GONE
                    }

                    6 -> {
                        binding.Period6.tvClassSubjectName.text = timeTableData.data[i].subName
                        binding.Period6.tvRoomNumber.text = "Room Number: ${timeTableData.data[i].rm}"
                        binding.Period6.chipTeacherName.text = timeTableData.data[i].name
                        binding.Period6.tvRoomNumber.visibility = View.VISIBLE
                        binding.Period6.chipTeacherName.visibility = View.VISIBLE
                        binding.Period6.llFreeTimeClock.visibility = View.GONE
//                        binding.Period6.currentChip.visibility = if (isTimeBetween(14, 5, 14, 55)) View.VISIBLE else View.GONE
                    }

                    7 -> {
                        binding.Period7.tvClassSubjectName.text = timeTableData.data[i].subName
                        binding.Period7.tvRoomNumber.text = "Room Number: ${timeTableData.data[i].rm}"
                        binding.Period7.chipTeacherName.text = timeTableData.data[i].name
                        binding.Period7.tvRoomNumber.visibility = View.VISIBLE
                        binding.Period7.chipTeacherName.visibility = View.VISIBLE
                        binding.Period7.llFreeTimeClock.visibility = View.GONE
//                        binding.Period7.currentChip.visibility = if (isTimeBetween(15, 5, 15, 55)) View.VISIBLE else View.GONE
                    }

                    8 -> {
                        binding.Period8.tvClassSubjectName.text = timeTableData.data[i].subName
                        binding.Period8.tvRoomNumber.text = "Room Number: ${timeTableData.data[i].rm}"
                        binding.Period8.chipTeacherName.text = timeTableData.data[i].name
                        binding.Period8.tvRoomNumber.visibility = View.VISIBLE
                        binding.Period8.chipTeacherName.visibility = View.VISIBLE
                        binding.Period8.llFreeTimeClock.visibility = View.GONE
//                        binding.Period8.currentChip.visibility = if (isTimeBetween(16, 5, 16, 55)) View.VISIBLE else View.GONE
                    }

                    9 -> {
                        binding.Period9.tvClassSubjectName.text = timeTableData.data[i].subName
                        binding.Period9.tvRoomNumber.text = "Room Number: ${timeTableData.data[i].rm}"
                        binding.Period9.chipTeacherName.text = timeTableData.data[i].name
                        binding.Period9.tvRoomNumber.visibility = View.VISIBLE
                        binding.Period9.chipTeacherName.visibility = View.VISIBLE
                        binding.Period9.llFreeTimeClock.visibility = View.GONE
//                        binding.Period9.currentChip.visibility = if (isTimeBetween(17, 5, 17, 55)) View.VISIBLE else View.GONE
                    }

                    else -> { // setting the particular period as FREE period, if there's no period found
                        // leaving it as it is........... Since, by default, its showing as free period....
                    }
                }
            }
        }

        setCurrentChip() // for setting up the 'current' chip for the current period happening........


        // Setup period headers
        val headerRow = binding.tlTimeTable.getChildAt(0) as TableRow
        for (i in 0 until periods.size) {
            val periodHeaderView = headerRow.getChildAt(i + 1)

            periodHeaderItemBinding.tvPeriodNumber.text =
                "${periods[i].number}${getSuperscript(periods[i].number)}"
            periodHeaderItemBinding.tvPeriodTime.text = periods[i].time
        }

        // Setup class cells
//        val classRow = binding.tlTimeTable.getChildAt(1) as TableRow
//        for (i in 0 until classes.size) {
//            val classItemView = classRow.getChildAt(i + 1) as MaterialCardView
//            val classNameText = classItemView.findViewById<TextView>(R.id.tvClassSubjectName)
//            val classRoomText = classItemView.findViewById<TextView>(R.id.tvRoomNumber)
//            val teacherChip = classItemView.findViewById<Chip>(R.id.chipTeacherName)
//
//            classNameText.text = classes[i].name
//            classRoomText.text = classes[i].room
//            teacherChip.text = classes[i].teacher
//
//
//            if (classes[i].name == "Free Period") {
//                classNameText.text = "Free Period"
//                classNameText.setTextColor(
//                    ContextCompat.getColor(
//                        requireContext(),
//                        R.color.blue_600
//                    )
//                )
//                classNameText.setTypeface(null, android.graphics.Typeface.ITALIC)
//                classRoomText.visibility = View.GONE
//                teacherChip.visibility = View.GONE
//            }
//
//            // Add animation to each card
//            val animation =
//                AnimationUtils.loadAnimation(requireContext(), R.anim.item_animation_from_bottom)
//            animation.startOffset = (i * 100).toLong()
//            classItemView.startAnimation(animation)
//        }
    }

    private fun setCurrentChip() {
        if (isTimeBetween(9, 5, 9, 55)) {
            binding.Period1.classItemCard.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green_100))
        } else if (isTimeBetween(10, 5, 10, 55)) {
            binding.Period2.classItemCard.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green_100))
        } else if (isTimeBetween(11, 5, 11, 55)) {
            binding.Period3.classItemCard.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green_100))
        } else if (isTimeBetween(12, 5, 12, 55)) {
            binding.Period4.classItemCard.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green_100))
        } else if (isTimeBetween(13, 5, 13, 55)) {
            binding.Period5.classItemCard.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green_100))
        } else if (isTimeBetween(14, 5, 14, 55)) {
            binding.Period6.classItemCard.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green_100))
        } else if (isTimeBetween(15, 5, 15, 55)) {
            binding.Period7.classItemCard.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green_100))
        } else if (isTimeBetween(16, 5, 16, 55)) {
            binding.Period8.classItemCard.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green_100))
        } else if (isTimeBetween(17, 5, 17, 55)) {
            binding.Period9.classItemCard.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green_100))
        }
    }

    private fun isTimeBetween(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int): Boolean {
        require(startHour in 0..23 && endHour in 0..23) { "Hours must be between 0-23" }
        require(startMinute in 0..59 && endMinute in 0..59) { "Minutes must be between 0-59" }

        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val currentTimeInMinutes = currentHour * 60 + currentMinute
        val startTimeInMinutes = startHour * 60 + startMinute
        val endTimeInMinutes = endHour * 60 + endMinute
        Log.d("timeTAG", "here!!")

        return currentTimeInMinutes in startTimeInMinutes..endTimeInMinutes
    }


    private fun updateCurrentPeriod() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentTimeInMinutes = currentHour * 60 + currentMinute

        var currentPeriodIndex = -1

        for (i in periods.indices) {
            val period = periods[i]
            val times = period.time.split("-")
            val startTime = times[0].split(":")
            val endTime = times[1].split(":")

            val startHour = startTime[0].toInt()
            val startMinute = startTime[1].toInt()
            val endHour = endTime[0].toInt()
            val endMinute = endTime[1].toInt()

            val startTimeInMinutes = startHour * 60 + startMinute
            val endTimeInMinutes = endHour * 60 + endMinute

            if (currentTimeInMinutes in startTimeInMinutes..endTimeInMinutes) {
                currentPeriodIndex = i
                break
            }
        }

        // Update UI to highlight current period
        val headerRow = binding.tlTimeTable.getChildAt(0) as TableRow
        val classRow = binding.tlTimeTable.getChildAt(1) as TableRow

        for (i in periods.indices) {
            val periodHeaderView = headerRow.getChildAt(i + 1)
            val classItemView = classRow.getChildAt(i + 1) as MaterialCardView
            val currentChip = classItemView.findViewById<Chip>(R.id.currentChip)

            if (i == currentPeriodIndex) {
                periodHeaderItemBinding.tvPeriodNumber.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue_600
                    )
                )
                classItemView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue_50
                    )
                )
                currentChip.visibility = View.VISIBLE
            } else {
                periodHeaderItemBinding.tvPeriodNumber.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_600
                    )
                )
                classItemView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.white
                    )
                )
                currentChip.visibility = View.GONE
            }
        }
    }

    private fun startAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
//        binding.appBarLayout.startAnimation(fadeIn)
    }

    private fun getSuperscript(number: Int): String {
        return when (number) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }

    }

    private fun fetchTimeTable() {
        binding.horizontalScrollView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        if (getToday().equals("Sunday")) {
            binding.llSunday.visibility = View.VISIBLE
            return
        }

        val s = TimeTableRequest(PrefsManager.getUserInformation(requireContext()).data.id, getToday(), getTodaysDate())
//        val s = TimeTableRequest("33", "Saturday", "2025-03-01")

        lifecycleScope.launch {
            try {
                delay(1500)
                ApiClient.timeTableInstance.getTimeTable(
                    "application/json",
                    "ci_session=qv72uulnvk25ck2t0vrarinop6lpddej",
                    s
                ).enqueue(object : retrofit2.Callback<TimeTableResponse> {


                    override fun onResponse(
                        call: Call<TimeTableResponse>,
                        response: Response<TimeTableResponse>
                    ) {

                        if (response.isSuccessful && response.body() != null) {
                            binding.horizontalScrollView.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.GONE
                            val s = response.body()
                            val gson = Gson()
                            if (s?.status == 1) {
                                Log.d("timeTableTAG", "${gson.toJson(s)}}")
                                timeTableViewModel.setTimeTableData(s)
                            }
                        }
                    }

                    override fun onFailure(call: Call<TimeTableResponse>, t: Throwable) {
                        binding.progressBar.visibility = View.GONE
                        Log.d("timeTableTAG", "${t.message}")
                    }

                })
            } catch (e: Exception) {
                Log.d("timeTableTAG", "catch: " + e.message)
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getToday(): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis() // Set time from system clock
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> "Unknown"
        }
    }

    private fun getTodaysDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun onResume() {
        super.onResume()
        fetchTimeTable()
        NetworkChangeReceiver.registerReceiver(requireContext(), networkChangeReceiver)
    }

    override fun onPause() {
        super.onPause()
//        NetworkChangeReceiver.registerReceiver(requireContext(), networkChangeReceiver)
    }



    data class Period(val number: Int, val time: String)
    data class ClassInfo(val name: String, val room: String, val teacher: String)


}