package com.example.practicecoroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _stateFlow = MutableStateFlow("가나다라마바사")
    val stateFlow: StateFlow<String> = _stateFlow

    private val _sharedFlow = MutableSharedFlow<String>()
    val sharedFlow: SharedFlow<String> = _sharedFlow

    init {
        changeStateFlow()
        changeSharedFlow()
    }

    fun setStateFlow(value: String) {
        _stateFlow.value = value
    }

    fun setSharedFlow(value: String) = viewModelScope.launch {
        _sharedFlow.emit(value)
    }

    private fun changeStateFlow() = viewModelScope.launch {
        delay(1000)
        _stateFlow.value = "0"
        delay(1000)
        _stateFlow.value = "1"
    }

    private fun changeSharedFlow() = viewModelScope.launch {
        delay(1000)
        _sharedFlow.emit("0")
        delay(1000)
        _sharedFlow.emit("1")
    }
}