package com.example.practicecoroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class FlowTest {

    @Test
    fun testFlow() {
        runBlocking {
            val flow = launch {
                flow.collect {
                    println("flow $it")
                }
            }
            delay(1000)
            flow.cancel()

            val flowToShared = launch {
                flowToSharedFlow.collect {
                    println("flow to sharedFlow $it")
                }
            }
            delay(1000)
            flowToShared.cancel()

            val flowToState = launch {
                flowToStateFlow.collect {
                    println("flow to stateFlow $it")
                }
            }
            delay(1000)
            flowToState.cancel()

            val stateFlow = launch {
                stateFlow().collect {
                    println("stateFlow $it")
                }
            }

            delay(1000)
            stateFlow.cancel()

            val sharedFlow = launch {
                sharedFlow().collect {
                    println("sharedFlow $it")
                }
            }

            delay(1000)
            sharedFlow.cancel()
        }
    }

    private val flow: Flow<Int> = flow {
        for (i in 1..3) {
            emit(i)
        }
    }

    private val flowToSharedFlow: Flow<Int> = flow {
        for (i in 1..3) {
            emit(i)
        }
    }.shareIn(CoroutineScope(Dispatchers.Default), SharingStarted.Eagerly, replay = 1)

    private val flowToStateFlow: Flow<Int> = flow {
        for (i in 1..3) {
            emit(i)
        }
    }.stateIn(CoroutineScope(Dispatchers.Default), SharingStarted.WhileSubscribed(), 0)

    private suspend fun stateFlow(): StateFlow<Int> {
        val stateFlow = MutableStateFlow(0)
        for (i in 1..3) {
            stateFlow.emit(i)
        }
        return stateFlow
    }

    private suspend fun sharedFlow(): SharedFlow<Int> {
        val sharedFlow = MutableSharedFlow<Int>(replay = 1)
        for (i in 1..3) {
            sharedFlow.emit(i)
        }
        return sharedFlow
    }
}

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class FlowTest2 {
    val flowInt = flowOf(1, 2, 3, 4).onEach { delay(10) }
    val flowChar = flowOf("a", "b", "c").onEach { delay(20) }
    val flow = flowOf<Int>(1, 2, 3)
    val flow2 = listOf(1, 2, 3).asFlow()
    val flow3 = flow {
        for (i in 1..3) {
            emit(i)
        }
    }

    @FlowPreview
    @Test
    fun testFlow(): Unit = runBlocking {
//        flowMap()
//        flowFilter()
//        flowCombine()
//        flowZip()
//        flowFlattenMerge()
//        testSharedFlow
//        launch { sharedFlow() }
        launch { sharedFlow2() }
//        launch { stateFlow() }
//        launch { stateFlow2() }
//        launch { setSharedFlow() }
//        launch { getSharedFlow() }
//        launch { getSharedFlow2() }
//        launch { setStateFlow() }
//        launch { getSharedFlow() }
//        launch { getSharedFlow2() }
    }

    suspend fun flowMap() {
        flow.map {
            "cold flow $it"
        }.collect {
            println(it)
        }
    }

    suspend fun flowFilter() {
        flow.filter {
            it % 2 != 0
        }.collect {
            println(it)
        }
    }

    // combine : 두 개의 flow를 결합하여 하나의 결과를 만들고 싶을 때
    // 최신 데이터만 결합하여 방출하고, 모든 flow가 완료될 때까지 멈추지 않는다
    suspend fun flowCombine() = runBlocking {
        println()
        flowInt.combine(flowChar) { int, char ->
            "$int$char"
        }.collect {
            println(it)
        }
    }

    // zip : 결합된 모든 flow 가 데이터를 방출하기를 기다리고, 모든 flow가 하나씩 방출이 완료되어서 한쌍이 나오는 시점에서 결합 데이터를 방출
    suspend fun flowZip() {
        println()
        flowInt.zip(flowChar) { int, char ->
            "$int$char"
        }.collect {
            println(it)
        }
    }

    // flattenMerge : 두 개의 flow에서 방출되는 결과들이 하나의 flow 결과물로 하나하나 방출된다
    @FlowPreview
    suspend fun flowFlattenMerge() {
        println()
        flowOf(flowInt, flowChar).flattenMerge().collect {
            println(it)
        }
    }


    val repeatedFlow = listOf(1, 1, 1, 1, 1).asFlow()
    // replay에 지정된 만큼의 방출 값을 준다
    val share = flow.shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, replay = 1)

    // SharingStarted.Lazily : default value와 최신 방출 값을 준다
    // SharingStarted.Eagerly : 최신 방출 값을 준다
    val state = runBlocking { flow.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Lazily, 0) }

    val mSharedFlow = MutableSharedFlow<Int>(replay = 4, extraBufferCapacity = 0, BufferOverflow.DROP_OLDEST)
    val sharedFlow: SharedFlow<Int> get() = mSharedFlow

    val mStateFlow = MutableStateFlow<Int>(0)
    val stateFlow: StateFlow<Int> get() = mStateFlow

    private suspend fun sharedFlow() {
        share.collect {
            println("first $it")
        }
    }

    private suspend fun sharedFlow2() {
//        share.collect {
//            println("second $it")
//        }

        repeatedFlow.shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.Lazily, replay = 3)
            .collect { println(it) }
    }

    private suspend fun stateFlow() {
//        state.collect {
//            println("first $it")
//        }
//        repeatedFlow.stateIn(
//            CoroutineScope(Dispatchers.Main),
//            SharingStarted.Eagerly,
//            0
//        ).collect {
//            println(it)
//        }
//
        repeatedFlow.collect {
            println(it)
        }
    }

    private suspend fun stateFlow2() {
        state.collect {
            println("second $it")
        }
    }

    private suspend fun setSharedFlow() {
        for (i in 0..5) {
            mSharedFlow.emit(i)
        }
    }

    private suspend fun getSharedFlow() {
        sharedFlow.collect {
            println(it)
        }
    }

    private suspend fun getSharedFlow2() {
        sharedFlow.collect {
            println(it)
        }
    }

    private suspend fun setStateFlow() {
        for (i in 0..5) {
            mStateFlow.value = i
        }
    }

    private suspend fun getStateFlow() {
        stateFlow.collect {
            println(it)
        }
    }

    private suspend fun getStateFlow2() {
        stateFlow.collect {
            println(it)
        }
    }


//    val testSharedFlow = runBlocking {
//        for (i in 0..4) {
//            mSharedFlow.emit(i)
//        }
//
//        sharedFlow.collect {
//            println(it)
//        }
//    }

    fun simple(): Flow<Int> = flow { // flow builder
        for (i in 1..3) {
            delay(100) // pretend we are doing something useful here
            emit(i) // emit next value
        }
    }

    @Test
    fun main() = runBlocking<Unit> {
        println("Calling simple function...")
        val flow = simple()
        println("Calling collect...")
        flow.collect { value -> println(value) }
        println("Calling collect again...")
        flow.collect { value -> println(value) }
    }
}