package com.example.practicecoroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.net.Socket
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val vm: MainViewModel by viewModels()

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("currentThread", "${Thread.currentThread().name}")

        btn.setOnClickListener {
//            lifecycleScope.launch {
//                val job1 = launch(start = CoroutineStart.LAZY) {
//                    Log.d("coroutineThread1", "${Thread.currentThread().name}")
//                    text.text = "메인 스레드에서 실행중..."
//                    delay(3000)
//                }
//
//                val job2 = launch(Dispatchers.Default, start = CoroutineStart.LAZY) {
//                    Log.d("coroutineThread2", "${Thread.currentThread().name}")
//                    delay(3000)
//                }
//
//                job2.join()
//                job1.join()
//
//                text.text = "lifecycleScope 종료..."
//            }


        }
    }
}