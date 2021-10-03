package com.udacity

import android.app.DownloadManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
        with(binding.content) {
            textStatus.text = intent?.extras?.getString(NotificationKeys.KEY_RESULT, EMPTY)
            textFileName.text = intent?.extras?.getString(NotificationKeys.KEY_FILE_NAME, EMPTY)
            textSource.text = intent?.extras?.getString(NotificationKeys.KEY_SOURCE, EMPTY)

            val isFailure = intent?.extras?.getBoolean(
                NotificationKeys.KEY_IS_FAILURE, false
            ) ?: false
            if (isFailure) {
                textStatus.setTextColor(ContextCompat.getColor(
                    this@DetailActivity,
                    R.color.errorDefault)
                )
            } else {
                textStatus.setTextColor(ContextCompat.getColor(
                    this@DetailActivity,
                    R.color.colorPrimary)
                )
            }
        }
    }

    private fun setupListeners() {
        with(binding.content) {
            buttonOpenDownloadsFolder.setOnClickListener {
                startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
            }
            buttonGoBack.setOnClickListener {
                finish()
            }
        }
    }
}
