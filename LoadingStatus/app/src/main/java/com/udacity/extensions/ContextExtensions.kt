package com.udacity.extensions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.udacity.R
import com.udacity.databinding.CustomToastBinding

private const val TOAST_Y_OFF_SET = 250
private const val TOAST_X_OFF_SET = 0

fun Context.showCustomToast(
    toastType: ToastType = ToastType.SUCCESS,
    stringResId: Int = R.string.text_success,
    durationToast: Int = Toast.LENGTH_SHORT
) {

    val binding = CustomToastBinding.inflate(LayoutInflater.from(this))

    binding.toastText.text = getString(stringResId)

    val imageResId = when (toastType) {
        ToastType.ERROR -> R.drawable.ic_error
        ToastType.INFO -> R.drawable.ic_info
        ToastType.WARNING -> R.drawable.ic_warning
        else -> {
            R.drawable.ic_check_circle
        }
    }
    binding.toastImage.setImageResource(imageResId)

    with (Toast(applicationContext)) {
        setGravity(Gravity.BOTTOM, TOAST_X_OFF_SET, TOAST_Y_OFF_SET)
        duration = durationToast
        view = binding.root
        show()
    }
}

enum class ToastType {
    SUCCESS,
    ERROR,
    INFO,
    WARNING
}

@Suppress("DEPRECATION")
fun Context.isConnected(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = manager.activeNetwork ?: return false
    val networkCapabilities = manager.getNetworkCapabilities(network) ?: return false
    val isConnectingOrConnected = manager.activeNetworkInfo?.isConnectedOrConnecting ?: return false

    return when {
        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        isConnectingOrConnected -> true
        else -> false
    }
}

fun Context.isPermissionNotGranted(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(
        this,
        permission
    ) != PackageManager.PERMISSION_GRANTED
}

fun Activity.requestWriteExternalStoragePermission(requestCode: Int) {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ),
        requestCode
    )
}