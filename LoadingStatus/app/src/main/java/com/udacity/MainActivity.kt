package com.udacity

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.udacity.data.DataProviderType
import com.udacity.databinding.ActivityMainBinding
import com.udacity.extensions.ToastType
import com.udacity.extensions.showCustomToast
import com.udacity.extensions.showOrUpdateNotification
import com.udacity.extensions.startDefaultNotificationChannel
import com.udacity.widgets.ButtonState

private const val NOTIFICATION_ID = 4337584

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel = MainViewModel()

    private var downloadID: Long = 0

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupListeners()
        setupObservers()

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        startDefaultNotificationChannel()
    }

    private fun setupListeners() {

        binding.loadingButtonView.setOnClickListener {
            if (binding.radioGroupDownloadOptions.checkedRadioButtonId == View.NO_ID) {
                applicationContext?.showCustomToast(
                    toastType = ToastType.INFO,
                    R.string.message_alert_select_download_type
                )
            } else {
                resolveDownloadType()
            }
        }

        binding.radioGroupDownloadOptions.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.radioButtonGlide, R.id.radioButtonRepository, R.id.radioButtonRetrofit -> {
                    binding.loadingButtonView.buttonState = ButtonState.IdleState
                    binding.loadingButtonView.buttonText = getString( R.string.button_text_download)
                }
                else -> {
                    binding.loadingButtonView.buttonState = ButtonState.IdleState
                    binding.loadingButtonView.buttonText = getString(R.string.button_text_idle)
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.state.observe(this, { state ->
            if (state.buttonState == ButtonState.Loading) {
                binding.circleLoadingIndicator.startAnimation()
                showOrUpdateNotification(
                    notificationId = NOTIFICATION_ID,
                    title = getString(R.string.notification_title),
                    text = getString(R.string.notification_download_in_progress),
                    shouldTrackProgress = true,
                )
            } else {
                binding.circleLoadingIndicator.stopAnimation()
            }
            binding.loadingButtonView.buttonState = state.buttonState
            binding.loadingButtonView.buttonText = getString(state.buttonTextResId)
            if (state.buttonState == ButtonState.Success) {
                applicationContext?.showCustomToast(stringResId = R.string.text_download_success)
                showOrUpdateNotification(
                    notificationId = NOTIFICATION_ID,
                    title = getString(R.string.notification_title),
                    text = getString(R.string.notification_description)
                )
            }
        })
    }

    private fun resolveDownloadType() {
        binding.loadingButtonView.buttonState = ButtonState.Loading
        val requestType = when(binding.radioGroupDownloadOptions.checkedRadioButtonId) {
            R.id.radioButtonGlide -> DataProviderType.GLIDE
            R.id.radioButtonRepository -> DataProviderType.DOWNLOAD_MANAGER
            R.id.radioButtonRetrofit -> DataProviderType.RETROFIT
            else -> {
                DataProviderType.DOWNLOAD_MANAGER
            }
        }
        viewModel.onActionButtonClicked(dataProviderType = requestType)
    }

    private fun download() {
        val request = DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        // enqueue puts the download request in the queue.
        downloadID = downloadManager.enqueue(request)
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }
}
