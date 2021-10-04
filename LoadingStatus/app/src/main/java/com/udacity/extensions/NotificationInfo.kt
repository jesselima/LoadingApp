package com.udacity.extensions

import com.udacity.R

data class NotificationInfo(
    val title: String = "",
    val description: String = "",
    val source: String = "",
    val fileExtension: String? = null,
    var downloadResult: String = "",
    var isFailure: Boolean = false,
    val actionLabelStrRes: Int = R.string.notification_default_button
)
