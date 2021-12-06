package com.example.practicecoroutine

import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class LiveDataFlowTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun collection() {
        fun foo(): List<Int> = listOf(1, 2, 3)

        foo().forEach { value -> println(value) }
    }

    private fun flowTest(): Flow<Int> = flow {
        for (i in 1..3) {
            delay(1000)
            emit(i)
        }
    }

    private suspend fun stateFlowTest(): StateFlow<Int> = flow {
        for (i in 1..3) {
            delay(1000)
            emit(i)
        }
    }.stateIn(CoroutineScope(Dispatchers.Default))

    @FlowPreview
    private fun asFlowTest() = listOf(1, 2, 3).asFlow()

    @FlowPreview
    @Test
    fun test() = runBlocking {
//        launch(Dispatchers.Default) {
//            for (k in 1..3) {
//                println("코루틴 스코프 $k")
//            }
//        }

//        launch(Dispatchers.IO) {
//            flowTest().collect {
//                value -> println("플로우 컬랙$value")
//            }
//        }

        flowTest().take(1).collect {
            value -> println("플로우 컬랙$value")
        }

//        asFlowTest().collect { value ->
//            delay(1000)
//            println("리스트를 플로우로 $value")
//        }
    }

    @Test
    fun testLiveData() {
        val vm = TestViewModel()

        vm.setValue("가나다라마바사")

        println(vm.aa.value)

        vm.setValue("아아아아아")

        println(vm.aa.value)
    }
}

class MainActivity : AppCompatActivity() {
    val _liveData = MutableLiveData<String>()
    val lv: LiveData<String> get() = _liveData
}


class TestViewModel : ViewModel() {
    val _aa = MutableLiveData<String>()
    val aa: LiveData<String> = _aa

    fun setValue(value: String) {
        _aa.value = value
    }


}

fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T

}