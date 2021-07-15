package com.forjrking.lubankt.ext

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import java.lang.Exception

/**
 * @description: 扩展
 * @author: 岛主
 * @date: 2020/10/13 23:28
 * @version: 1.0.0
 */

@MainThread
fun <T, R> CompressLiveData<T, R>.compressObserver(owner: LifecycleOwner,
                                                   compressResult: CompressResult<T, R>.() -> Unit
) {
    val result = CompressResult<T, R>();result.compressResult()
    observe(owner, androidx.lifecycle.Observer {
        when (it) {
            is State.Start -> {
                result.onStart()
            }
            is State.Completion -> {
                result.onCompletion()
            }
            is State.Success -> {
                result.onSuccess(it.data)
            }
            is State.Error -> {
                result.onError(it.error, it.src)
            }
        }
    })
}

@MainThread
fun <T, R> CompressLiveData<T, R>.onCompressListener(owner: LifecycleOwner,
                                                   compressResult: OnCompressResult<T,R>
) {
    observe(owner, androidx.lifecycle.Observer {
        when (it) {
            is State.Start -> {
                compressResult.onStart()
            }
            is State.Completion -> {
                compressResult.onCompletion()
            }
            is State.Success -> {
                compressResult.onSuccess(it.data)
            }
            is State.Error -> {
                compressResult.onError(it.src!!)
            }
        }
    })
}

open  class CompressResult<T, R> {
    var onStart: () -> Unit = {}
    var onCompletion: () -> Unit = {}
    var onSuccess: (data: R) -> Unit = {}
    var onError: (Throwable, T?) -> Unit = { _: Throwable, _: T? -> }
}

interface OnCompressResult<T,R>{
    fun onStart()
    fun onCompletion()
    fun onSuccess(data:R)
    fun onError(exception: T)
}

sealed class State<out T, out R> {
    object Start : State<Nothing, Nothing>()
    object Completion : State<Nothing, Nothing>()
    data class Success<out R>(val data: R) : State<Nothing, R>()
    data class Error<out T>(val error: Throwable, val src: T? = null) : State<T, Nothing>()
}

typealias CompressLiveData<T, R> = MutableLiveData<State<T, R>>

/**单位换算*/
enum class MemoryUnit {
    BYTE,
    KB,
    MB,
}