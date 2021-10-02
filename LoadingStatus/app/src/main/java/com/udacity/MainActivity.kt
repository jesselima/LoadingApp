package com.udacity

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.udacity.core.ConnectivityReceiver
import com.udacity.core.FileTypeValue
import com.udacity.core.NotificationKeys
import com.udacity.core.connectionchecker.ConnectionChecker
import com.udacity.databinding.ActivityMainBinding
import com.udacity.extensions.*
import com.udacity.widgets.ButtonState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val REQUEST_CODE = 101
private const val INVALID_RESULT = -1L

class MainActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener  {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()
    private val connectionChecker: ConnectionChecker by inject()

    private var downloadManager: DownloadManager? = null
    private var notificationInfo = NotificationInfo()
    private var lastDownloadId: Long = INVALID_RESULT
    private val connectivityReceiver = ConnectivityReceiver()

    private val onDownloadCompleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val isDownloadCompleted = intent?.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE

            val resultTextResId = if (isDownloadCompleted) {
                viewModel.setStateSuccess()
                getString(R.string.text_download_success)
            } else {
                viewModel.setStateError()
                getString(R.string.text_download_failed)
            }

            if (isDownloadCompleted) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, INVALID_RESULT)
                id?.let { receivedId ->
                    if (receivedId == lastDownloadId) {
                        viewModel.setStateSuccess()
                        showCustomToast(
                            toastType = ToastType.SUCCESS,
                            stringResId = R.string.text_success
                        )
                        context?.showOrUpdateNotification(
                            notificationId = NotificationKeys.NOTIFICATION_ID,
                            title = notificationInfo.title,
                            text = resultTextResId,
                            contentText = notificationInfo.description,
                            shouldTrackProgress = false,
                            shouldIntentNewTask = true,
                            shouldLaunchIntent = true,
                            actionLabelText = getString(notificationInfo.actionLabelStrRes),
                            data = bundleOf(
                                Pair(NotificationKeys.KEY_RESULT, getString(R.string.text_success)),
                                Pair(NotificationKeys.KEY_FILE_NAME, notificationInfo.title),
                                Pair(NotificationKeys.KEY_SOURCE, notificationInfo.source),
                            )
                        )
                    } else {
                        viewModel.setStateError()
                        showCustomToast(
                            toastType = ToastType.WARNING,
                            stringResId = R.string.text_download_failed
                        )
                    }
                }
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
        registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    private fun setupListeners() {
        binding.loadingButtonView.setOnClickListener {
            if (connectionChecker.isConnected().not()) {
                showCustomToast(
                    toastType = ToastType.WARNING,
                    stringResId = R.string.message_connection_error
                )
            } else {
                if (binding.radioGroupDownloadOptions.checkedRadioButtonId == View.NO_ID) {
                    showCustomToast(
                        toastType = ToastType.INFO,
                        stringResId = R.string.message_alert_select_download_type
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

            // Todo
            //  - Use of "when"
            //  - Incorporate circleLoadingIndicator in the ButtonView
            if (state.buttonState == ButtonState.Loading) {
                binding.circleLoadingIndicator.startAnimation()
            } else {
                binding.circleLoadingIndicator.stopAnimation()
            }
            if (state.buttonState == ButtonState.ConnectionError) {
                showCustomToast(
                    toastType = ToastType.WARNING,
                    stringResId = R.string.message_connection_error,
                    durationToast = Toast.LENGTH_LONG
                )
            }
        })
    }

    private fun setNotificationInfoAndDownload() {
        applicationContext?.removeAllNotifications()
        when(binding.radioGroupDownloadOptions.checkedRadioButtonId) {
            R.id.radioButtonGlide -> {
                notificationInfo = NotificationInfo(
                    title = getString(R.string.label_glide_title),
                    description = getString(R.string.label_glide_description),
                    fileExtension = FileTypeValue.ZIP.value,
                    source = getString(R.string.url_glide_repository)
                )
                checkPermissionsAndStartDownload()
            }
            R.id.radioButtonRepository -> {
                notificationInfo = NotificationInfo(
                    title = getString(R.string.label_load_app_title),
                    description = getString(R.string.label_load_app_description),
                    fileExtension = FileTypeValue.ZIP.value,
                    source = getString(R.string.url_load_app_repository)
                )
                checkPermissionsAndStartDownload()
            }
            R.id.radioButtonRetrofit -> {
                notificationInfo = NotificationInfo(
                    title = getString(R.string.label_retrofit_title),
                    description = getString(R.string.label_retrofit_description),
                    fileExtension = FileTypeValue.ZIP.value,
                    source = getString(R.string.url_retrofit_repository)
                )
                checkPermissionsAndStartDownload()
            }
            R.id.radioButtonSampleVideo -> {
                notificationInfo = NotificationInfo(
                    title = getString(R.string.label_video_title),
                    description = getString(R.string.label_video_description),
                    fileExtension = FileTypeValue.MP4.value,
                    source = getString(R.string.url_sample_video),
                )
                checkPermissionsAndStartDownload()
            }
            // Todo check inf the input has a valid URL
            //  if not, show toast. if yes try to download.
        }
    }

    private fun startDownload() {
        viewModel.setStateLoading()
        val downloadRequest = DownloadManager.Request(Uri.parse(notificationInfo.source))
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "${notificationInfo.title}${notificationInfo.fileExtension}"
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setRequiresCharging(false)
                }
            }

        downloadManager?.let {
            // enqueue puts the download request in the queue.
            lastDownloadId = it.enqueue(downloadRequest)
        }
    }

    private fun checkPermissionsAndStartDownload() {
        if (isPermissionNotGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestWriteExternalStoragePermission(REQUEST_CODE)
        } else {
            startDownload()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults.first() == INVALID_RESULT.toInt())  {
            showCustomToast(
                stringResId = R.string.message_external_storage_permission_error,
                toastType = ToastType.WARNING,
                durationToast = Toast.LENGTH_LONG
            )
            viewModel.setStateIdle()
        } else {
            startDownload()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onDownloadCompleteReceiver)
        unregisterReceiver(connectivityReceiver)
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
        viewModel.checkConnectionState(applicationContext.isConnected())
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        viewModel.checkConnectionState(isConnected)
    }
}
