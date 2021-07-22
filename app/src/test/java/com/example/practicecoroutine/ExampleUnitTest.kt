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
    @Test
    fun main() {
        basicCoroutine()

        //코루틴은 루틴에 대한 진입 / 탈출 지점이 여러개이다
        testSuspend()

        //non-blocking
        //코루틴이 돌아가는 스레드가 종료되면 해당 코루틴도 종료된다
        coroutineInThread()

        //blocking
        //runBlocking을 사용하여 스레드를 정지시킬 수 있음
        testRunBlocking()

        //스레드는 선점형 멀티태스킹이다
        testThread()

        //코루틴은 비선점형 멀티태스킹이다
        testCoroutine()

        //기본적으로 코루틴 scope안의 코드는 순차적으로 진행된다
        testSequential()

        //동시성을 띄고 싶으면 async를 사용한다
        testAsync()

        //launch와 async의 차이
        launchAndAsync()

        //코루틴 지연 - start = CoroutineStart.LAZY를 사용하여 scope 내의 코드 진행을 원하는 때에 실행할 수 있다
        testNonLazy()
        testLazy()

        //코루틴 취소
        testCancel()
    }

    @Test
    fun basicCoroutine(): Unit = runBlocking {
        val mainScope = CoroutineScope(Dispatchers.IO) // CoroutincContext를 지정하여 scope 생성
        mainScope.launch { // 코루틴 빌더
            //코루틴 코드
            println("비동기적 작업을 실행하는 코드입니다")
            println("${Thread.currentThread().name}")
        }

        launch(Dispatchers.Default) {
            println("비동기적 작업을 실행하는 코드입니다2")
            println("${Thread.currentThread().name}")
        }
    }

    //코루틴은 루틴에 대한 진입 / 탈출 지점이 여러개이다
    @Test
    fun testSuspend() {
        CoroutineScope(Dispatchers.Default).launch {
            aa()
            bb()
        }

        println("coroutine 탈출")
        Thread.sleep(1500)
        println("coroutine 2차 탈출")
        Thread.sleep(2500)
        println("coroutine 마지막 탈출")
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

    //non-blocking
    //코루틴이 돌아가는 스레드가 종료되면 해당 코루틴도 종료된다
    @Test
    fun coroutineInThread() {
        CoroutineScope(Dispatchers.Default).launch {
            repeat(10) {
                println(it)
                delay(200)
            }
        }

        Thread.sleep(1000)
        println("thread 종료")
    }


    //blocking
    //runBlocking을 사용하여 스레드를 정지시킬 수 있음
    @Test
    fun testRunBlocking() = runBlocking {
        repeat(10) {
            println(it)
            delay(200)
        }

        println("thread 종료")
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

    //기본적으로 코루틴 scope 안의 코드는 순차적으로 진행된다
    @Test
    fun testSequential() {
        runBlocking {
            val time = measureTimeMillis {
                val a = aa()
                val b = bb()
                println("a + b =  ${a + b}")
            }
            println("소요시간 $time")
        }
    }

    //동시성을 띄고 싶으면 async를 사용한다
    @Test
    fun testAsync() {
        runBlocking {
            val time = measureTimeMillis {
                val a = async { aa() }
                val b = async { bb() }
                println("${a.await() + b.await()}")
            }
            println("소요시간 $time")
        }
    }

    //launch와 async의 차이
    @Test
    fun launchAndAsync() = runBlocking {
        //launch는 Job 객체를 반환한다
        //launch는 결과값을 리턴할 수 없다
        val job: Job = CoroutineScope(Dispatchers.Default).launch {
            delay(1000)
            println("coroutine - launch")
        }
        job.join()

        //async는 Deferred 객체를 반환하다
        //async는 결과값을 반환할 수 있다
        val deferred: Deferred<Int> = CoroutineScope(Dispatchers.Default).async {
            delay(1000)
            println("coroutine - async")
            10
        }
        deferred.await()
        println("async 리턴값 확인 ${deferred.await()}")
    }

    //코루틴 지연 - start = CoroutineStart.LAZY를 사용하여 scope 내의 코드 진행을 원하는 때에 실행할 수 있다
    @Test
    fun testNonLazy(): Unit = runBlocking {
        launch {
            delay(3000)
            println("launch1 실행됨")
        }

        launch {
            delay(1000)
            println("launch2 실행됨")
        }

        async {
            delay(2000)
            println("async1 실행")
        }

        val a = async {
            delay(500)
            println("async2 실행")
            "asdf"
        }
    }

    @Test
    fun testLazy() = runBlocking {
        val job1 = launch(start = CoroutineStart.LAZY) {
            delay(3000)
            println("job1 실행됨")
        }

        val job2 = launch(start = CoroutineStart.LAZY) {
            delay(1000)
            println("job2 실행됨")
        }

        job1.join()
        job2.join()

        println()

        val deferred1 = async(start = CoroutineStart.LAZY) {
            delay(2000)
            println("deferred1 실행됨")
            "동기를"
        }

        val deferred2 = async(start = CoroutineStart.LAZY) {
            delay(500)
            println("deferred2 실행됨")
            " 맞춥시다"
        }

        println(deferred1.await() + deferred2.await())
    }

    @Test
    fun testCancel() = runBlocking {
        val job = launch {
            repeat(100) {
                println(it)
                delay(500)
            }
        }

        delay(5000)
        job.cancel()
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