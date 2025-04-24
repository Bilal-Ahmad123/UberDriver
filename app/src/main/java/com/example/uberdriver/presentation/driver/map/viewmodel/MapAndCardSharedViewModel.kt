package com.example.uberdriver.presentation.driver.map.viewmodel

import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.dispatcher.IDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class MapAndCardSharedViewModel @Inject constructor(dispatcher:IDispatchers):BaseViewModel(dispatcher){
    private val acceptRideBtnClicked = MutableSharedFlow<Boolean>()
    val acceptRideBtnClick get() = acceptRideBtnClicked

    suspend fun setRideBtnClicked(value: Boolean) {
        acceptRideBtnClicked.emit(value)
    }
}