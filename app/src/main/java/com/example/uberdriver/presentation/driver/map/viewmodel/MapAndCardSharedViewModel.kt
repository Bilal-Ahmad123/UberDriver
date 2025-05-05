package com.example.uberdriver.presentation.driver.map.viewmodel

import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.dispatcher.IDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MapAndCardSharedViewModel @Inject constructor(dispatcher:IDispatchers):BaseViewModel(dispatcher){
    private val acceptRideBtnClicked = MutableSharedFlow<Boolean>()
    val acceptRideBtnClick get() = acceptRideBtnClicked.asSharedFlow()

    private val startRideBtnClicked = MutableSharedFlow<Boolean>()
    val startRideClick get() = startRideBtnClicked.asSharedFlow()

    private val reachedPickUpLocation = MutableStateFlow<Boolean>(false)
    val reachPickUpLocation get() = reachedPickUpLocation.asStateFlow()

    private val reachedDropOffLocation = MutableStateFlow<Boolean>(false)
    val reachDropOffLocation get() = reachedDropOffLocation.asStateFlow()

    private val _goBtnClicked = MutableSharedFlow<Boolean>()
    val goBtnClicked get() = _goBtnClicked.asSharedFlow()

    private val _reachedRider = MutableSharedFlow<Boolean>()
    val reachedRider = _reachedRider.asSharedFlow()

    suspend fun setRideBtnClicked(value: Boolean) {
        acceptRideBtnClicked.emit(value)
    }

    suspend fun setStartRideBtnClicked(value:Boolean){
        startRideBtnClicked.emit(value)
    }

    suspend fun setPickUpLocationReached(value : Boolean){
        reachedPickUpLocation.emit(value)
    }

    suspend fun setDropOffLocationReached(value : Boolean){
        reachedDropOffLocation.emit(true)
    }

    suspend fun setGoBtnClicked(value:Boolean){
        _goBtnClicked.emit(value)
    }
}