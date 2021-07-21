package com.example.practicecoroutine

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    val scope = CoroutineScope(Dispatchers.Default)
    val _text = MutableLiveData<String>()
    val text: LiveData<String> get() = _text

    init {
        testSuspend()
    }

    private fun testSuspend() {
        scope.launch {
            val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
                aa()
                _text.value = "aa bb 사이"
                bb()
            }
//            _text.postValue("시끄럽게")
            delay(1000)
            job.start()
        }

        scope.launch(Dispatchers.IO) {
//            bb()
        }
    }

    suspend fun aa() {
        _text.value = "오리는"
        delay(3000)
        _text.value = "꽥꽥"
        delay(1000)
    }

    suspend fun bb() {
        _text.postValue("고양이는")
        delay(1000)
        _text.postValue("야옹")
    }
}