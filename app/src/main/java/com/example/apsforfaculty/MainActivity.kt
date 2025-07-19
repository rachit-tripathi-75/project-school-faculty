package com.example.apsforfaculty

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.apsforfaculty.activities.ContactUsActivity
import com.example.apsforfaculty.activities.LoginActivity
import com.example.apsforfaculty.activities.MyAccountActivity
import com.example.apsforfaculty.activities.NotificationsActivity
import com.example.apsforfaculty.adapters.BannerPagerAdapter
import com.example.apsforfaculty.adapters.DashboardAdapter
import com.example.apsforfaculty.adapters.DashboardItem
import com.example.apsforfaculty.classes.PrefsManager
import com.example.apsforfaculty.classes.SmoothDrawerToggle
import com.example.apsforfaculty.databinding.ActivityMainBinding
import com.example.apsforfaculty.databinding.NavHeaderBinding
import com.example.apsforfaculty.fragments.TimeTableFragment
import com.example.apsforfaculty.networks.NetworkChangeReceiver
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationHeaderBinding: NavHeaderBinding

    companion object {
        private const val REQUEST_CODE_NOTIFICATION = 1001
        private const val REQUEST_CODE_STORAGE = 1002
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requiredPermissionsApi33 = arrayOf(
        android.Manifest.permission.POST_NOTIFICATIONS,
        android.Manifest.permission.READ_MEDIA_IMAGES,
        android.Manifest.permission.READ_MEDIA_VIDEO
    )

    private val requiredPermissionsBelowApi33 = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private lateinit var bannerPagerAdapter: BannerPagerAdapter

    var networkChangeReceiver: NetworkChangeReceiver =
        NetworkChangeReceiver(object : NetworkChangeReceiver.NetworkStatusListener {
            override fun onNetworkConnected() {
                Log.d("networkInterceptorTAG", "inside onNetworkConnected()")

            }

            override fun onNetworkDisconnected() {
                Log.d("networkInterceptorTAG", "inside onNetworkDisconnected()")
                Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_LONG).show()
            }
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initialise()
        listeners()
        setupNavigationDrawer()
        requestNotificationPermission()
        setupDashboardRecyclerView()
        showCurrentDayAndDate()
        applyAnimations()
        setUpTimeTableFragment()

    }

    // Function to request storage permission
    fun requestStoragePermission() {
        val permissionsToRequest = ArrayList<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ - use new media permissions
            permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_IMAGES)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10-12 - only need READ_EXTERNAL_STORAGE
            permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            // Android 9 and below - need both read and write
            permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            permissionsToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        ActivityCompat.requestPermissions(
            this,
            permissionsToRequest.toTypedArray(),
            REQUEST_CODE_STORAGE
        )
    }

    // Function to request notification permission (Android 13+ only)
    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_NOTIFICATION
            )
        }
    }


    private fun initialise() {

        val headerView = binding.navigationView.getHeaderView(0)
        navigationHeaderBinding = NavHeaderBinding.bind(headerView)

        if (PrefsManager.getUserInformation(this@MainActivity) != null) {
            navigationHeaderBinding.tvFacultyEmail.text =
                PrefsManager.getUserInformation(this@MainActivity).data.email
            navigationHeaderBinding.tvFacultyName.text =
                PrefsManager.getUserInformation(this@MainActivity).data.name
            binding.tvWelcomeHeader.text =
                "Welcome, ${PrefsManager.getUserInformation(this@MainActivity).data.name}"
        }




    }

    private fun listeners() {
        binding.ivNotifcation.setOnClickListener {
            startActivity(Intent(this@MainActivity, NotificationsActivity::class.java))
        }
    }

    private fun setupNavigationDrawer() {

        val smoothDrawerToggle = SmoothDrawerToggle(binding.drawerLayout)
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item clicks
            when (menuItem.itemId) {
                R.id.nav_home -> {

                }

                R.id.nav_account -> {
                    startActivity(Intent(this@MainActivity, MyAccountActivity::class.java))
                }
                // Handle other menu items
                R.id.nav_contact -> {
                    startActivity(Intent(this@MainActivity, ContactUsActivity::class.java))
                }
            }

            // Close drawer with custom animation
            smoothDrawerToggle.closeDrawer()
            true
        }

        // Set up drawer toggle with custom animation
        binding.ivHamburger.setOnClickListener {
            smoothDrawerToggle.openDrawer()
        }

        navigationHeaderBinding.btnCloseDrawer.setOnClickListener {
            smoothDrawerToggle.closeDrawer()
        }

        binding.btnLogout.setOnClickListener {
            // Handle logout
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finishAffinity()
            getSharedPreferences(PrefsManager.PREF_NAME, Context.MODE_PRIVATE).edit() { clear() }
        }


    }

    private fun showCurrentDayAndDate() {

        binding.tvDate.text = SimpleDateFormat(
            "EEEE, MMMM d, yyyy",
            Locale.getDefault()
        ).format(Calendar.getInstance().time)
//        binding.tvDay.text = SimpleDateFormat("EEEE", Locale.getDefault()).format(Calendar.getInstance().time)
    }

    private fun applyForegroundService() {
        // foreground service for seeing the notification in-app also............ (pending)

    }


    private fun setupDashboardRecyclerView() {
        val layoutManager = GridLayoutManager(this, 3)
        binding.rvDashboard.layoutManager = layoutManager
        val adapter = DashboardAdapter(this, getDashboardItems())
        binding.rvDashboard.adapter = adapter

        // Apply layout animation
        val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)
        binding.rvDashboard.layoutAnimation = animation
    }

    private fun applyAnimations() {

        // Animate profile image with rotation
        navigationHeaderBinding.profileImage.alpha = 0f
        navigationHeaderBinding.profileImage.scaleX = 0.5f
        navigationHeaderBinding.profileImage.scaleY = 0.5f
        navigationHeaderBinding.profileImage.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .setInterpolator(OvershootInterpolator())
            .setStartDelay(300)
            .start()

        // Animate dashboard grid items sequentially
        binding.rvDashboard.scheduleLayoutAnimation()
    }

    private fun getDashboardItems(): List<DashboardItem> {
        return listOf(
            DashboardItem(R.drawable.attendance, "Mark Attendance"),
            DashboardItem(R.drawable.viewattendance, "View Attendance"),
            DashboardItem(R.drawable.uploadmarks, "Upload Marks"),
            DashboardItem(R.drawable.uploadwork, "Upload Work"),
//            DashboardItem(R.drawable.practicesheet, "Upload Practice Sheet"),
//            DashboardItem(R.drawable.ic_timetable, "View TimeTable"),
//            DashboardItem(R.drawable.ic_subject_notes, "Subject Notes"),
//            DashboardItem(R.drawable.ic_calendar, "Academic Plan"),
//            DashboardItem(R.drawable.ic_leave_request, "Leave Request"),
//            DashboardItem(R.drawable.ic_pta, "PTA"),
//            DashboardItem(R.drawable.ic_homework, "Home Work"),
//            DashboardItem(R.drawable.ic_grievance, "Grievance"),
//            DashboardItem(R.drawable.ic_activity, "Extra Activity"),
//            DashboardItem(R.drawable.ic_comment, "Student Comment")
        )
    }

    private fun setUpTimeTableFragment() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.findFragmentById(R.id.fragment_time_table_container) == null) {
            val timeTableFragment = TimeTableFragment()
            val transaction = fragmentManager.beginTransaction()
            transaction.add(
                R.id.fragment_time_table_container,
                timeTableFragment,
                "default_fragment"
            )
            transaction.commit()
        }
    }

    override fun onResume() {
        super.onResume()
        NetworkChangeReceiver.registerReceiver(this, networkChangeReceiver)
    }

    override fun onPause() {
        super.onPause()
        NetworkChangeReceiver.unregisterReceiver(this, networkChangeReceiver)
    }


    // Handle permission results
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE_NOTIFICATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermission()
//                    Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT)
//                        .show()
                } else {
                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            REQUEST_CODE_STORAGE -> {
                var allGranted = true
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false
                        break
                    }
                }

                if (allGranted) {
//                    Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }


}