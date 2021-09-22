package com.udacity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.core.connectionchecker.ConnectionChecker
import com.udacity.data.DataProviderType
import com.udacity.widgets.ButtonState
import kotlinx.coroutines.*


class MainViewModel(
    private val dispatchers: CoroutineDispatcher,
    connectionChecker: ConnectionChecker
): ViewModel() {

    private val _state = MutableLiveData<MainState>()
    val state: LiveData<MainState> = _state

    init {
        _state.value = MainState()
        if(connectionChecker.isConnected().not()) {
            _state.value = state.value?.copy(
                buttonTextResId = R.string.message_connection_error,
                buttonState = ButtonState.Error
            )
        }
    }

    fun onActionButtonClicked(dataProviderType: DataProviderType) {
        _state.value = state.value?.copy(
            buttonTextResId = R.string.button_text_loading,
            buttonState = ButtonState.Loading
        )
        when (dataProviderType) {
            DataProviderType.RETROFIT -> getDataWithRetrofit()
            DataProviderType.DOWNLOAD_MANAGER -> getDataWithDownloadManager()
            DataProviderType.GLIDE -> getDataWithGlide()
        }
    }

    private fun getDataWithGlide() {
        viewModelScope.launch {
            runCatching {
                withContext(dispatchers) {
                    delay(5000)
                }
            }.onSuccess {
                _state.value = state.value?.copy(
                    buttonTextResId = R.string.button_text_completed,
                    buttonState = ButtonState.Success
                )
            }.onFailure {
                _state.value = state.value?.copy(
                    buttonTextResId = R.string.button_text_error,
                    buttonState = ButtonState.Error
                )
            }
        }
    }

    private fun getDataWithRetrofit() {
        viewModelScope.launch {
            runCatching {
                withContext(dispatchers) {
                    delay(5000)
                }
            }.onSuccess {
                _state.value = state.value?.copy(
                    buttonTextResId = R.string.button_text_completed,
                    buttonState = ButtonState.Success
                )
            }.onFailure {
                _state.value = state.value?.copy(
                    buttonTextResId = R.string.button_text_error,
                    buttonState = ButtonState.Error
                )
            }
        }
    }

    private fun getDataWithDownloadManager() {
        viewModelScope.launch {
            runCatching {
                withContext(dispatchers) {
                    delay(5000)
                }
            }.onSuccess {
                _state.value = state.value?.copy(
                    buttonTextResId = R.string.button_text_completed,
                    buttonState = ButtonState.Success
                )
            }.onFailure {
                _state.value = state.value?.copy(
                    buttonTextResId = R.string.button_text_error,
                    buttonState = ButtonState.Error
                )
            }
        }
    }

}