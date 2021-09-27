package com.udacity.widgets


sealed class ButtonState {
    object IdleState : ButtonState()
    object Loading : ButtonState()
    object Error : ButtonState()
    object ConnectionError : ButtonState()
    object ConnectionOnline : ButtonState()
    object Success : ButtonState()
}