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
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
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
private const val MIN_INPUT_URL_LENGTH = 7
private const val MAX_INPUT_URL_LENGTH = 1000

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
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, INVALID_RESULT)

            if (isDownloadCompleted) {
                val downloadCursor = downloadManager?.query(DownloadManager.Query().setFilterById(lastDownloadId))
                downloadCursor?.let { cursor ->
                    if (cursor.moveToFirst()) {
                        when(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                            DownloadManager.STATUS_FAILED -> {
                                showCustomToast(
                                    toastType = ToastType.ERROR,
                                    stringResId = R.string.message_download_error,
                                )
                                viewModel.setStateError()
                                notificationInfo.downloadResult = getString(R.string.text_download_failed)
                                notificationInfo.isFailure = true
                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                showCustomToast(
                                    toastType = ToastType.SUCCESS,
                                    stringResId = R.string.text_success
                                )
                                viewModel.setStateSuccess()
                                notificationInfo.downloadResult = getString(R.string.text_download_success)
                                notificationInfo.isFailure = false
                            }
                        }

                        if (id == lastDownloadId) {
                            context?.showOrUpdateNotification(
                                notificationId = NotificationKeys.NOTIFICATION_ID,
                                title = notificationInfo.title,
                                text = notificationInfo.downloadResult,
                                contentText = notificationInfo.description,
                                shouldTrackProgress = false,
                                shouldIntentNewTask = true,
                                shouldLaunchIntent = true,
                                actionLabelText = getString(notificationInfo.actionLabelStrRes),
                                data = bundleOf(
                                    Pair(NotificationKeys.KEY_RESULT, notificationInfo.downloadResult),
                                    Pair(NotificationKeys.KEY_IS_FAILURE, notificationInfo.isFailure),
                                    Pair(NotificationKeys.KEY_FILE_NAME, notificationInfo.title),
                                    Pair(NotificationKeys.KEY_SOURCE, notificationInfo.source),
                                )
                            )
                        }
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
            val checkedId = binding.radioGroupDownloadOptions.checkedRadioButtonId
            if (connectionChecker.isConnected().not()) {
                showCustomToast(
                    toastType = ToastType.WARNING,
                    stringResId = R.string.message_connection_error
                )
            } else {
                when {
                    checkedId == View.NO_ID -> {
                        showCustomToast(
                            toastType = ToastType.INFO,
                            stringResId = R.string.message_alert_select_download_type
                        )
                    }
                    else -> {
                        if(checkedId == R.id.radioButtonCustomUrl &&
                            binding.textInputLayoutCustomUrl.isErrorEnabled) {
                            showCustomToast(
                                toastType = ToastType.INFO,
                                stringResId = R.string.message_invalid_custom_url
                            )
                        } else {
                            setNotificationInfoAndDownload()
                        }

                    }
                }
            }
        }

        binding.textInputLayoutCustomUrl.editText?.doOnTextChanged { inputTextValue, _, _, _ ->
            with(binding) {
                inputTextValue?.let { url ->
                    textInputLayoutCustomUrl.isErrorEnabled = true
                    if(url.length > MIN_INPUT_URL_LENGTH) {

                        val errorMessage: String? = when {
                            url.length > MAX_INPUT_URL_LENGTH -> {
                                getString(R.string.message_too_long_url)
                            }
                            Patterns.WEB_URL.matcher(url).matches().not() -> {
                                getString(R.string.message_invalid_url)
                            }
                            else -> {
                                textInputLayoutCustomUrl.isErrorEnabled = false
                                loadingButtonView.buttonState = ButtonState.IdleState
                                loadingButtonView.buttonText = getString(
                                    R.string.button_text_download
                                )
                                null
                            }
                        }
                        textInputLayoutCustomUrl.error = errorMessage
                    } else {
                        textInputLayoutCustomUrl.error = getString(R.string.message_too_short_url)
                    }
                }
            }
        }

        binding.radioGroupDownloadOptions.setOnCheckedChangeListener { _, checkedId ->
            with(binding) {
                textInputLayoutCustomUrl.isEnabled = checkedId ==  R.id.radioButtonCustomUrl
                when(checkedId) {
                    R.id.radioButtonGlide, R.id.radioButtonRepository, R.id.radioButtonRetrofit,
                    R.id.radioButtonSampleVideo-> {
                        loadingButtonView.buttonState = ButtonState.IdleState
                        loadingButtonView.buttonText = getString( R.string.button_text_download)
                    }
                    R.id.radioButtonCustomUrl -> {
                        textInputLayoutCustomUrl.isEnabled = true
                        loadingButtonView.buttonState = ButtonState.IdleState
                        loadingButtonView.buttonText =
                            getString(R.string.button_text_paste_your_url)
                    }
                    else -> {
                        loadingButtonView.buttonState = ButtonState.IdleState
                        loadingButtonView.buttonText = getString(R.string.button_text_idle)
                    }
                }
            }

        }
    }

    private fun setupObservers() {
        viewModel.state.observe(this, { state ->
            with(binding) {
                loadingButtonView.buttonState = state.buttonState
                loadingButtonView.buttonText = getString(state.buttonTextResId)

                // Todo
                //  - Use of "when"
                //  - Incorporate circleLoadingIndicator in the ButtonView
                if (state.buttonState == ButtonState.Loading) {
                    circleLoadingIndicator.startAnimation()
                } else {
                    circleLoadingIndicator.stopAnimation()
                }
                if (state.buttonState == ButtonState.ConnectionError) {
                    showCustomToast(
                        toastType = ToastType.WARNING,
                        stringResId = R.string.message_connection_error,
                        durationToast = Toast.LENGTH_LONG
                    )
                }
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
            R.id.radioButtonCustomUrl -> {
                notificationInfo = NotificationInfo(
                    title = getString(R.string.label_custom_url_title),
                    description = getString(R.string.label_custom_url_description),
                    fileExtension = null,
                    source = binding.textInputLayoutCustomUrl.editText?.text.toString(),
                )
                checkPermissionsAndStartDownload()
            }
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
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setRequiresCharging(false)
                }

                val path = if (notificationInfo?.fileExtension != null) {
                    "${notificationInfo.title}${notificationInfo.fileExtension}"
                } else {
                    notificationInfo.title
                }

                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    path
                )
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
