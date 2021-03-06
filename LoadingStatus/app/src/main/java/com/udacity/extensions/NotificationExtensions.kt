package com.udacity.extensions

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import com.udacity.DetailActivity
import com.udacity.R

private const val MAX_PROGRESS = 100
private const val REQUEST_CODE = 0

/**
 * The implementation below is base is this repository where I am the owner:
 * {@link https://github.com/jesselima/Android-Certification-Playground/blob/05-android-core-notifications/app/src/main/java/com/example/playground/extensions/AppCompatActivityExtensions.kt}.
 *
 * Because you must create the notification channel before posting any notifications on
 * Android 8.0 and higher, you should execute this code as soon as your app starts.
 */
fun Context.startDefaultNotificationChannel(
    channels: List<Triple<String, String, String>> = listOf(
        Triple("MAIN", "The main app channel", "Used as default")
    ),
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationManager: NotificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        channels.forEach {
            val channel = NotificationChannel(
                it.first,
                it.second,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = it.third
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@SuppressLint("UnspecifiedImmutableFlag")
fun Context.showOrUpdateNotification(
    notificationId: Int,
    title: String,
    text: String,
    channelId: String = "MAIN",
    shouldLaunchIntent: Boolean = true,
    shouldIntentNewTask: Boolean = false,
    shouldTrackProgress: Boolean = false,
    shouldAutoCancel: Boolean = true,
    actionLabelText: String? = null,
    contentText: String? = null,
    @DrawableRes actionDrawableResId: Int = R.drawable.ic_assistant,
    @DrawableRes resIdSmallIcon: Int = R.drawable.ic_assistant,
    progress: Int = 0,
    data: Bundle? = null
) {

    with(NotificationManagerCompat.from(this)) {

        val notificationIntent = Intent(applicationContext, DetailActivity::class.java).apply {
            if(shouldIntentNewTask) {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        }

        data?.let {
            notificationIntent.putExtras(it)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            REQUEST_CODE,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = buildNotificationManager(
            channelId = channelId,
            resIdSmallIcon = resIdSmallIcon,
            shouldAutoCancel = shouldAutoCancel
        ).apply {
            setContentTitle(title)
            setStyle(NotificationCompat.BigTextStyle().bigText(text))
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            if(shouldLaunchIntent) {
                setContentIntent(pendingIntent)
                addAction(
                    actionDrawableResId,
                    actionLabelText ?: getString(R.string.notification_default_button),
                    pendingIntent
                )
            }
            if (shouldTrackProgress) {
                setProgress(MAX_PROGRESS, progress, true)
            }
            contentText?.let {
                setContentText(it)
            }
        }.build()

        notify(notificationId, notification)
    }
}

fun Context.buildNotificationManager(
    channelId: String,
    shouldAutoCancel: Boolean = true,
    shouldAlertOnlyOnce: Boolean = true,
    @DrawableRes resIdSmallIcon: Int,
    notificationPriority: Int = NotificationCompat.PRIORITY_DEFAULT,
) : NotificationCompat.Builder {

    /**
     * Channel ID is is required for compatibility with Android 8.0 (API level 26) and higher,
     * but is ignored by older versions.
     */
    return NotificationCompat.Builder(this, channelId).apply {
        setSmallIcon(resIdSmallIcon)
        priority = notificationPriority
        setAutoCancel(shouldAutoCancel)
        setOnlyAlertOnce(shouldAlertOnlyOnce)
    }
}

fun Context.removeNotification(notificationId: Int) {
    with(NotificationManagerCompat.from(this)) {
        /** Cancel a specific notification by its ID. Also deletes ongoing notifications. */
        cancel(notificationId)
    }
}

fun Context.removeAllNotifications() {
    with(NotificationManagerCompat.from(this)) {
        /** Removes all of the notifications you previously issued. */
        cancelAll()
    }
}