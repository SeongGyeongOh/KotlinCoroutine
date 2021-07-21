package com.example.practicecoroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.*
import java.net.Socket
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.*

class MainActivity : AppCompatActivity() {

    val scope = CoroutineScope(Dispatchers.Main)
    var viewStub: ViewStub? = null
    var flowData = mutableListOf(1, 2, 3, 4, 5)
    val _textLiveData = MutableLiveData(flowData)
    val textLiveData : MutableLiveData<MutableList<Int>> get() = _textLiveData

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn.setOnClickListener {
            flowData.add(6)
            Log.d("플로우 데이터", flowToLiveData(flowData).value.toString())
        }

        FlowToLiveData(flowData).flowToLiveData(flowData).observe(this) {
            text.text = it.toString()
        }
//        flowToLiveData(flowData)
        test()
    }

    val flowStream : Flow<List<Int>> = flow {
        while(true) {
            emit(flowData)
            Log.d("플로우 데이터", flowData.toString())
            delay(3000)
        }
    }

    fun flowToLiveData(data: MutableList<Int>) : LiveData<MutableList<Int>> {
        val flow = flow {
            emit(data)
        }

        CoroutineScope(Dispatchers.Default).launch {
            flow.collect {
                Log.d("멍청한 플로우", it.toString())
            }
        }

        return flow.asLiveData()
    }

    private fun test() {
        CoroutineScope(Dispatchers.Main).launch {
            while(true) {
                flowStream.distinctUntilChanged().collect {
                    Log.d("플로우", "바뀜")
                    Toast.makeText(this@MainActivity, it.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun afterTest() {
        Toast.makeText(this@MainActivity, "실행 타이밍 확인하기", Toast.LENGTH_SHORT).show()
    }
}

class FlowToLiveData(data: MutableList<Int>) {

    fun flowToLiveData(data: MutableList<Int>) : LiveData<MutableList<Int>> {
        val flow = flow {
            emit(data)
        }

        CoroutineScope(Dispatchers.Default).launch {
            flow.collect {
                Log.d("멍청한 플로우", it.toString())
            }
        }

        return flow.asLiveData()
    }
}