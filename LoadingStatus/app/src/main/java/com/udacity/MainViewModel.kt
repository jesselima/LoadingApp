package com.udacity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.data.DataProviderType
import com.udacity.widgets.ButtonState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel: ViewModel() {

    private val dispatchers = Dispatchers.IO

    private val _state = MutableLiveData<MainState>()
    val state: LiveData<MainState> = _state

    init {
        _state.value = MainState()
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