package com.example.practicecoroutine

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.practicecoroutine.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val vm: MainViewModel by viewModels()

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val view = binding.root



//        vm.setStateFlow("아아아")
//        vm.setSharedFlow("??")

        /**
         * flow to liveData
         */
//        lifecycleScope.launch {
//            vm.setStateFlow("아아아")
//            vm.stateFlow.asLiveData().observe(this@MainActivity, Observer {
//                binding.mainText.text = it
//            })
//
//            vm.setSharedFlow("??")
//            vm.sharedFlow.asLiveData().observe(this@MainActivity, Observer {
//                binding.mainText2.text = it
//            })
//        }

        /**
         * use separate coroutineScope
         */
//        lifecycleScope.launch {
//            vm.setStateFlow("아아아")
//            vm.stateFlow. collect {
//                binding.mainText.text = it
//            }
//        }
//
//        lifecycleScope.launch {
//            vm.setSharedFlow("??")
//            vm.sharedFlow.collect {
//                binding.mainText2.text = it
//            }
//        }

        /**
         * use repeatOnLifecycle
         * **** UI에서 flow를 collect할 때 필수적으로 사용할 것
         * 사용하지 않을 시 UI의 라이프사이클이 끝나는 때 flow를 직접 없애는 작업을 해야한
         * 사용하려면 lifecycle dependency를
         * implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.4.0-alpha03" 로 사용해야함
         */
//        val job = lifecycleScope.launch {
//            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                launch {
//                    vm.stateFlow. collect {
//                        binding.mainText.text = it
//                    }
//                }
//
//                launch {
//                    vm.sharedFlow.collect {
//                        binding.mainText2.text = it
//                    }
//                }
//            }
//        }

        /**
         * use merge
         */
//        lifecycleScope.launch {
//            merge(
//                vm.stateFlow, vm.sharedFlow
//            ).collect {
//                Log.d("flow_merge", "$it")
//
//            }
//        }

        /**
         * use combine
         */
//        lifecycleScope.launch {
//            combine(vm.stateFlow, vm.sharedFlow) { state, shared ->
//                state + shared
//            }.collect {
//                binding.mainText.text = it
//                binding.mainText2.text = it
//            }
//        }

        /**
         * use flowWithLifecycle
         */
//        lifecycleScope.launch {
////            vm.setStateFlow("아아아")
//            vm.stateFlow.flowWithLifecycle(lifecycle = lifecycle, minActiveState = Lifecycle.State.STARTED)
//                    .collect {
//                        binding.mainText.text = it
//                    }
//
//            vm.sharedFlow.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
//                    .collect {
//                        binding.mainText2.text = it
//                    }
//        }

        setContentView(view)
    }
}