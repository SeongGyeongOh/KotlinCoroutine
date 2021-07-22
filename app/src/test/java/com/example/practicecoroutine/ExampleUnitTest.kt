package com.example.practicecoroutine

import kotlinx.coroutines.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.lang.ArithmeticException
import java.lang.IllegalArgumentException
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
        val job = Job()
        val handler = CoroutineExceptionHandler { _, exception ->
            println("코루틴 에러 발생 ${exception}")
        }

        val scope = CoroutineScope(
                Dispatchers.IO +    // CoroutineContext를 지정하여 scope 생성
                        job +
                        handler +
                        CoroutineName("FirstCoroutine")
        )

        scope.launch { // 코루틴 빌더
            //코루틴 코드
            println("비동기적 작업을 실행하는 코드입니다")
            throw NullPointerException()
        }.join()

        async(Dispatchers.Default) {
            println("비동기적 작업을 실행하는 코드입니다2")
        }
    }

    //코루틴은 루틴에 대한 진입 / 탈출 지점이 여러개이다
    @Test
    fun testSuspend() {
        //하나의 스레드가 실행되며 suspend function을 만나면 해당 함수를 잠시 지연하고 코루틴 블록을 탈출, 그 아래의 코드를 실행한다
        //suspend의 작업이 끝나면 다시 작업이 정지된 시점으로 돌아와 코루틴을 계속 실행한다
        CoroutineScope(Dispatchers.Default).launch {
            init()
            aa()
            bb()
        }

        println("coroutine 탈출")
        Thread.sleep(1500)
        println("coroutine 2차 탈출")
        Thread.sleep(2500)
        println("coroutine 마지막 탈출")
    }

    fun init() {
        println("init()")
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