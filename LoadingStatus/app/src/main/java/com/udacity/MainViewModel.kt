package com.udacity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.core.connectionchecker.ConnectionChecker
import com.udacity.widgets.ButtonState


class MainViewModel(
    private val connectionChecker: ConnectionChecker
): ViewModel() {

    private val _state = MutableLiveData<MainState>()
    val state: LiveData<MainState> = _state

    init {
        _state.value = MainState(
            buttonTextResId = R.string.button_text_idle,
            buttonState = ButtonState.IdleState
        )
    }

    fun setStateIdle() {
        _state.value = MainState(
            buttonTextResId = R.string.button_text_idle,
            buttonState = ButtonState.IdleState
        )
    }

    fun setStateLoading() {
        _state.value = state.value?.copy(
            buttonTextResId = R.string.button_loading,
            buttonState = ButtonState.Loading
        )
    }

    fun setStateError() {
        _state.value = state.value?.copy(
            buttonTextResId = R.string.button_text_error,
            buttonState = ButtonState.Error
        )
    }

    fun checkConnectionState() {
        if (connectionChecker.isConnected().not()) {
            _state.value = state.value?.copy(
                buttonTextResId = R.string.message_connection_error,
                buttonState = ButtonState.IdleState
            )
        }
    }

    fun setDownloadResultState(downloadSuccess: Boolean) {
        if (downloadSuccess) {
            _state.value = state.value?.copy(
                buttonTextResId = R.string.button_text_completed,
                buttonState = ButtonState.Success
            )
        } else {
            _state.value = state.value?.copy(
                buttonTextResId = R.string.button_text_error,
                buttonState = ButtonState.Error
            )
        }
    }
}