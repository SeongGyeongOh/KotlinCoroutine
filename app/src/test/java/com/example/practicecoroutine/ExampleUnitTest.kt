package com.example.practicecoroutine

import android.provider.Settings
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
    @Test
    fun main() {
        //코루틴은 루틴에 대한 진입 / 탈출 지점이 여러개이다
        //코루틴이 돌아가는 스레드가 종료되면 해당 코루틴도 종료된다
        testSuspend()

        //기본적으로 코루틴 scope안의 코드는 순차적으로 진행된다
        testSequential()

        //동시성을 띄고 싶으면 async를 사용한다
        //코틀린 코루틴의 async -
        testAsync()
    }

    @Test
    fun testSuspend() {
        GlobalScope.launch {
            aa()
            bb()
        }

        println("coroutine 탈출")
        Thread.sleep(1500)
        println("coroutine 2차 탈출")
        Thread.sleep(2500)
        println("coroutine 마지막 탈출")
    }

    @Test
    fun testSequential() {
        runBlocking {
            val time = measureTimeMillis {
                val a = aa()
                val b = bb()
                println("a + b =  ${a + b}")
            }
            println("순차적 소요시간 $time")
        }
    }

    @Test
    fun testAsync() {
        runBlocking {
            val time = measureTimeMillis {
                val a = async { aa() }
                val b = async { bb() }
                println("${a.await() + b.await()}")
            }
            println("동시 소요시간 $time")
        }
    }

    @Test
    fun launchAndAsync() = runBlocking {
        //launch는 Job 객체를 반환한다
        //launch는 값을 리턴할 수 없다
        val job = GlobalScope.launch {
            delay(1000)
            println("coroutine - launch")
        }

        job.cancel()
        println("launch 와 async 사이")

        //async는 Deferred 객체를 반환하다
        //async는 값을 반환할 수 있다
        val deferred = GlobalScope.async {
            delay(1000)
            println("coroutine - async")
            return@async 10          //실행 가능 코드
        }

        deferred.await()
        println("async 리턴값 확인 ${deferred.await()}")
    }

    suspend fun aa(): Int {
        delay(1000)
        println("aa()")
        return 10
    }

    suspend fun bb(): Int {
        delay(1000)
        println("bb()")
        return 5
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

//    @Test
//    fun launchAndAsync() {
//        runBlocking {
//            withContext(Dispatchers.Default) {
//                delay(2000)
//                println("async처럼 동작하게")
//            }
//
//            val value: Int = withContext(Dispatchers.Default) {
//                delay(1500)
//                1 + 2
//            }
//
//            launch {
//                delay(300)
//                println("Launch has NO return value")
//            }
//
//            println("순서대로 실행되는 코루틴 블럭")
//            println("Async has return value: $value")
//        }
//    }

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