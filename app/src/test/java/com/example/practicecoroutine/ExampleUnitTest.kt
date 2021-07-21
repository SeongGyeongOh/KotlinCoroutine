package com.example.practicecoroutine

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
class ExampleUnitTest {

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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *  코루틴
     */

    //코루틴은 루틴에 대한 진입 / 탈출 지점이 여러개이다
    @Test
    fun main() {
//        testSuspend()
        println("coroutine 탈출")
    }

    @Test
    fun coroutineInThread2() = runBlocking {
        val join = GlobalScope.launch {
            aa()
            bb()
        }

        join.join()

        delay(1000)
    }

    @Test
    fun testSuspend() = runBlocking { // 코루틴 스코프
        GlobalScope.launch {
            val job = launch { bb() }

            launch { aa() }

            job.join()
        }

        delay(4000)
    }

    suspend fun aa() {
        delay(1000)
        println("오리는")
    }

    suspend fun bb() {
        println("시끄러운")
        delay(3000)
        println("꽥꽥")
    }


    //스레드는 선점형 멀티태스킹을 한다
    @Test
    fun testThread() {
        thread {
            print("Thread: ${Thread.currentThread().name}")
            repeat(500) {
                print("1")
            }
        }

        thread {
            print("Thread: ${Thread.currentThread().name}")
            repeat(500) {
                print("2")
            }
        }
    }

    //코루틴은 비선점형 멀티태스킹이다
    @Test
    fun testCoroutine(): Unit = runBlocking {
        launch {
            println("Thread: ${Thread.currentThread().name}")
            repeat(500) {
                print("1")
            }
            println()
        }

        launch {
            println("Thread: ${Thread.currentThread().name}")
            repeat(500) {
                print("2")
            }
            println()
        }
    }

    @Test
    fun launchAndAsync() {
        runBlocking {
            withContext(Dispatchers.Default) {
                delay(2000)
                println("async처럼 동작하게")
            }

            val value: Int = withContext(Dispatchers.Default) {
                delay(1500)
                1 + 2
            }

            launch {
                delay(300)
                println("Launch has NO return value")
            }

            println("순서대로 실행되는 코루틴 블럭")
            println("Async has return value: $value")
        }
    }

    @Test
    fun coroutineInThread() = runBlocking {
        GlobalScope.launch {
            repeat(10) {
                print(it)
                delay(200)
            }
        }

        delay(1000)
    }
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