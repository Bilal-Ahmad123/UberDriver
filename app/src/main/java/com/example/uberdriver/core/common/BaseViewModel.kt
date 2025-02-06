package com.example.uberdriver.core.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.uberdriver.core.dispatcher.IDispatchers
import kotlinx.coroutines.launch

open class BaseViewModel @Inject constructor(private val dispatcher: IDispatchers): ViewModel() {
    fun launchOnMain(coroutine: suspend CoroutineScope.() -> Unit) =
        launchOnViewModelScope(dispatcher.main, coroutine)

    fun launchOnBack(coroutine: suspend CoroutineScope.() -> Unit) =
        launchOnViewModelScope(dispatcher.io, coroutine)

    fun launchOnComputation(coroutine: suspend CoroutineScope.() -> Unit) =
        launchOnViewModelScope(dispatcher.computation, coroutine)

    fun launchOnDb(coroutine: suspend CoroutineScope.() -> Unit) =
        launchOnViewModelScope(dispatcher.db, coroutine)

    private fun launchOnViewModelScope(
        coroutineDispatcher: CoroutineDispatcher,
        coroutine: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(coroutineDispatcher + coroutineExceptionHandler, block = coroutine)

    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }
}