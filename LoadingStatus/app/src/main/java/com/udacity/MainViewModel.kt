package com.udacity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun onActionButtonClicked(requestProviderType: RequestProviderType) {
        _state.value = state.value?.copy(isLoading = true)
        when (requestProviderType) {
            RequestProviderType.RETROFIT -> getDataWithRetrofit()
            RequestProviderType.DOWNLOAD_MANAGER -> getDataWithDownloadManager()
            RequestProviderType.GLIDE -> getDataWithGlide()
        }
    }

    private fun getDataWithGlide() {
        viewModelScope.launch {
            runCatching {
                withContext(dispatchers) {
                    delay(3000)
                }
            }.onSuccess {
                _state.value = state.value?.copy(isLoading = false)
            }.onFailure {

            }
        }
    }

    private fun getDataWithRetrofit() {
        viewModelScope.launch {
            runCatching {
                withContext(dispatchers) {
                    delay(3000)
                }
            }.onSuccess {
                _state.value = state.value?.copy(isLoading = false)
            }.onFailure {

            }
        }
    }

    private fun getDataWithDownloadManager() {
        viewModelScope.launch {
            runCatching {
                withContext(dispatchers) {
                    delay(3000)
                }
            }.onSuccess {
                _state.value = state.value?.copy(isLoading = false)
            }.onFailure {

            }
        }
    }

}