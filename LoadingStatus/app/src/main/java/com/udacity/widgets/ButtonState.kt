package com.udacity.widgets


sealed class ButtonState {
    object IdleState : ButtonState()
    object Loading : ButtonState()
    object Error : ButtonState()
    object Success : ButtonState()
}