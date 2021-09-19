package com.udacity

import androidx.annotation.StringRes
import com.udacity.widgets.ButtonState

data class MainState(
    @StringRes val buttonTextResId: Int = R.string.button_text_idle,
    val buttonState: ButtonState = ButtonState.IdleState
)
