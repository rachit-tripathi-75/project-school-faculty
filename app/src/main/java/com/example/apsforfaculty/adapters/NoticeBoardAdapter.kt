package com.example.apsforfaculty.adapters

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.apsforfaculty.R
import com.example.apsforfaculty.activities.NoticeBoardActivity.NoticeBoardRecord
import com.example.apsforfaculty.classes.ApiClient
import com.example.apsforfaculty.classes.Constants.Companion.APS_DOWNLOAD_NOTIFICATION_CHANNEL_ID
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import okhttp3.ResponseBody

class NoticeBoardAdapter(val context: Context) :
    RecyclerView.Adapter<NoticeBoardAdapter.ViewHolder>() {
    private var allRecords = listOf<NoticeBoardRecord>() // contains all data
    private var filteredRecords = listOf<NoticeBoardRecord>() // contains only filtered data based on chip selected

    fun setNoticeBoard(records: List<NoticeBoardRecord>) {
        this.allRecords = records.toList()
        this.filteredRecords = records
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notice, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = allRecords[position]
        holder.tvNoticeTitle.text = record.noticeTitle
        holder.tvNoticeDate.text = record.noticeDate
        holder.tvNoticeDescription.text = record.noticeDescription
        holder.chipCategory.text = record.type


        holder.btnDownloadPdf.setOnClickListener {
            Log.d("btnDownloadTAG", "Clicked at: ${position}")

//            val pdfUrl = ApiClient.BASE_URL + record.path + record.pdfName
//            val fileName = "notice_${System.currentTimeMillis()}.pdf"
            fetchFile(context, record.path, record.pdfName)

        }
    }

    fun filter(type: String) {
        filteredRecords = if (type.equals("All", ignoreCase = true)) {
            allRecords
        } else {
            allRecords.filter { it.type.contains(type, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }


    private fun fetchFile(context: Context, path: String, fileName: String) {
        // Create Notification Channel (only once, not every time)
        createNotificationChannel(context)

        // Start the progress notification...
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        var progressNotification = createDownloadProgressNotification(context, 0)
        notificationManager.notify(1, progressNotification)

        Thread {
            try {
                val call = ApiClient.downloadPdfInstance.downloadPdf(fileName)
                val response = call.execute()

                if (response.isSuccessful && response.body() != null) {
                    val totalFileSize = response.body()!!.contentLength()
                    val uri = savePdfScopedStorage(context, response.body()!!, fileName)

                    // Read and write file in chunks to update progress...
                    response.body()!!.byteStream().use { inputStream ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var totalBytesRead: Long = 0

                        val outputStream = context.contentResolver.openOutputStream(uri!!)!!

                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead

                            // Calculate progress
                            val progress = ((totalBytesRead * 100) / totalFileSize).toInt()

                            // Update progress notification on the main thread
                            Handler(Looper.getMainLooper()).post {
                                progressNotification = createDownloadProgressNotification(context, progress)
                                notificationManager.notify(1, progressNotification)
                            }
                        }

                        outputStream.flush()
                        outputStream.close()

                        // Once download is complete, show the finished notification
                        Handler(Looper.getMainLooper()).post {
                            val finishedNotification = createDownloadFinishedNotification(context)
                            notificationManager.notify(1, finishedNotification)
                        }
                    }
                } else {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    Handler(Looper.getMainLooper()).post {
                        val finishedNotification = createDownloadFinishedNotification(context)
                        notificationManager.notify(1, finishedNotification)
                    }
                }
            }
        }.start()
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                APS_DOWNLOAD_NOTIFICATION_CHANNEL_ID,
                "Downloads",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for file download progress"
            }

            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createDownloadProgressNotification(context: Context, progress: Int): Notification {
        return NotificationCompat.Builder(context, APS_DOWNLOAD_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Downloading PDF...")
            .setContentText("Progress: $progress%")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .build()
    }

    fun createDownloadFinishedNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, APS_DOWNLOAD_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Download Finished")
            .setContentText("Your PDF has been downloaded.")
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setAutoCancel(true)
            .build()
    }

    fun savePdfScopedStorage(context: Context, responseBody: ResponseBody, fileName: String): Uri? {
        return try {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    responseBody.byteStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
            uri
        } catch (e: Exception) {
            Log.e("PDF_SAVE", "Error saving PDF: ${e.message}")
            null
        }
    }



    override fun getItemCount() = filteredRecords.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNoticeTitle: TextView = itemView.findViewById(R.id.tvNoticeTitle)
        val tvNoticeDate: TextView = itemView.findViewById(R.id.tvNoticeDate)
        val tvNoticeDescription: TextView = itemView.findViewById(R.id.tvNoticeDescription)
        val chipCategory: Chip = itemView.findViewById(R.id.chipCategory)
        val btnDownloadPdf: MaterialButton = itemView.findViewById(R.id.btnDownloadPdf)
    }


}
