package com.udacity

import androidx.annotation.StringRes

data class MainState(
    val isLoading: Boolean = false,
    @StringRes val buttonText: Int = R.string.button_name
)
