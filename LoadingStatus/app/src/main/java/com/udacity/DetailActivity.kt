package com.udacity

import android.app.DownloadManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.udacity.core.NotificationKeys
import com.udacity.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val result: Boolean? = intent?.extras?.getBoolean(NotificationKeys.KEY_RESULT)
        val source: String? = intent?.extras?.getString(NotificationKeys.KEY_SOURCE)

        Log.d("===>>>", result.toString())
        Log.d("===>>>", source.toString())

        setupListeners()
    }

    private fun setupListeners() {
        binding.content.buttonOpenDownloadsFolder.setOnClickListener {
            startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
        }
        binding.content.buttonGoBack.setOnClickListener {
            finish()
        }
    }
}
