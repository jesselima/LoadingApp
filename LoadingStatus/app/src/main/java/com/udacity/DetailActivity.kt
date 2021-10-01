package com.udacity

import android.app.DownloadManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.core.NotificationKeys
import com.udacity.databinding.ActivityDetailBinding

private const val EMPTY = ""

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
        setupViews()
    }

    private fun setupViews() {
        binding.content.textStatus.text =
            intent?.extras?.getString(NotificationKeys.KEY_RESULT, EMPTY)
        binding.content.textFileName.text =
            intent?.extras?.getString(NotificationKeys.KEY_FILE_NAME, EMPTY)
        binding.content.textSource.text =
            intent?.extras?.getString(NotificationKeys.KEY_SOURCE, EMPTY)
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
