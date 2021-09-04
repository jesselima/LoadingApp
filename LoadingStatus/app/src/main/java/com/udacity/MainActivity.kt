package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        setupListeners()
    }

    private fun setupListeners() {
        binding.downloadButton.setOnClickListener {
            resolveDownloadType()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun resolveDownloadType() {
        when(binding.radioGroupDownloadOptions.checkedRadioButtonId) {
            R.id.radioButtonGlide -> {
                Toast.makeText(this, "radioButtonGlide is selected!!", Toast.LENGTH_SHORT).show()
            }
            R.id.radioButtonRepository -> {
                Toast.makeText(this, "radioButtonRepository is selected!!", Toast.LENGTH_SHORT).show()
            }
            R.id.radioButtonRetrofit -> {
                Toast.makeText(this, "radioButtonRetrofit is selected!!", Toast.LENGTH_SHORT).show()
            }
            View.NO_ID -> {
                Toast.makeText(this, "Please, select a file to download ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun download() {
        val request = DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
