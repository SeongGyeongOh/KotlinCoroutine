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

        lifecycleScope.launch {

            vm.text.observe(this@MainActivity) {
                text.text = it
            }

            text.text = "계속 작업이 수행돼고 음"
        }
    }
}