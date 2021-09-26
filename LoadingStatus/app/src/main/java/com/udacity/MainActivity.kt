package com.udacity

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.udacity.core.FileTypeValue
import com.udacity.core.NotificationKeys
import com.udacity.core.connectionchecker.ConnectionChecker
import com.udacity.databinding.ActivityMainBinding
import com.udacity.extensions.ToastType
import com.udacity.extensions.showCustomToast
import com.udacity.extensions.showOrUpdateNotification
import com.udacity.widgets.ButtonState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()
    private val connectionChecker: ConnectionChecker by inject()

    private var downloadManager: DownloadManager? = null
    private var notificationInfo = NotificationInfo()
    private var lastDownload: Long = -1

    private val onDownloadCompleteReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val isDownloadCompleted = intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE
            val resultTextResId = if (isDownloadCompleted) {
                getString(R.string.text_download_success)
            } else {
                getString(R.string.text_download_failed)
            }
            if (isDownloadCompleted) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                id?.let { receivedId ->
                    val isDownloadSuccess = receivedId == lastDownload
                    viewModel.setDownloadResultState(isDownloadSuccess)
                    context?.showOrUpdateNotification(
                        notificationId = NotificationKeys.NOTIFICATION_ID,
                        title = notificationInfo.title,
                        text = resultTextResId,
                        contentText = notificationInfo.description,
                        shouldTrackProgress = false,
                        shouldIntentNewTask = true,
                        actionLabelText = getString(notificationInfo.actionLabelStrRes)
                    )
                } ?: viewModel.setStateError()
            } else {
                viewModel.setStateError()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupListeners()
        setupObservers()
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        registerReceiver(onDownloadCompleteReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun setupListeners() {
        binding.loadingButtonView.setOnClickListener {
            if (connectionChecker.isConnected().not()) {
                showCustomToast(
                    toastType = ToastType.WARNING,
                    R.string.message_connection_error
                )
            } else {
                if (binding.radioGroupDownloadOptions.checkedRadioButtonId == View.NO_ID) {
                    showCustomToast(
                        toastType = ToastType.INFO,
                        R.string.message_alert_select_download_type
                    )
                } else {
                    setNotificationInfoAndDownload()
                }
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
            binding.loadingButtonView.buttonState = state.buttonState
            binding.loadingButtonView.buttonText = getString(state.buttonTextResId)
            if (state.buttonState == ButtonState.Loading) {
                binding.circleLoadingIndicator.startAnimation()
            } else {
                binding.circleLoadingIndicator.stopAnimation()
            }
        })
    }

    private fun setNotificationInfoAndDownload() {
        when(binding.radioGroupDownloadOptions.checkedRadioButtonId) {
            R.id.radioButtonGlide -> {
                notificationInfo = NotificationInfo(
                    title = getString(R.string.label_glide_title),
                    description = getString(R.string.label_glide_description),
                    fileExtension = FileTypeValue.ZIP.value,
                    source = getString(R.string.url_glide_repository)
                )
                download()
            }
            R.id.radioButtonRepository -> {
                notificationInfo = NotificationInfo(
                    title = getString(R.string.label_load_app_title),
                    description = getString(R.string.label_load_app_description),
                    fileExtension = FileTypeValue.ZIP.value,
                    source = getString(R.string.url_load_app_repository)
                )
                download()
            }
            R.id.radioButtonRetrofit -> {
                notificationInfo = NotificationInfo(
                    title = getString(R.string.label_retrofit_title),
                    description = getString(R.string.label_retrofit_description),
                    fileExtension = FileTypeValue.ZIP.value,
                    source = getString(R.string.url_retrofit_repository)
                )
                download()
            }
            R.id.radioButtonSampleVideo -> {
                notificationInfo = NotificationInfo(
                    title = getString(R.string.label_video_title),
                    description = getString(R.string.label_video_description),
                    fileExtension = FileTypeValue.MP4.value,
                    source = getString(R.string.url_sample_video),
                )
                download()
            }
            // Todo check inf the input has a valid URL
            //  if not, show toast. if yes try to download.
        }
    }

    private fun download() {
        viewModel.setStateLoading()
        val downloadRequest = DownloadManager.Request(Uri.parse(notificationInfo.source))
            .setTitle(getString(R.string.app_name))
            .setDescription(
                getString(R.string.notification_downloading)
                    .plus(" ${notificationInfo.title} - ${notificationInfo.description}")
            )
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "${notificationInfo.title}${notificationInfo.fileExtension}"
            )

        downloadManager?.let {
            // enqueue puts the download request in the queue.
            lastDownload = it.enqueue(downloadRequest)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onDownloadCompleteReceiver)
    }
}

data class NotificationInfo(
    val title: String = "",
    val description: String = "",
    val source: String = "",
    val fileExtension: String = "",
    val actionLabelStrRes: Int = R.string.notification_default_button
)
