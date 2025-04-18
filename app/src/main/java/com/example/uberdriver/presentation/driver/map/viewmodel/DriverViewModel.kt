package com.example.uberdriver.presentation.driver.map.viewmodel

import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.dispatcher.IDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DriverViewModel @Inject constructor(dispatcher:IDispatchers) : BaseViewModel(dispatcher) {
    private var _driverId : UUID? = null
    val driverId get() = _driverId


    fun setDriverId(driverId : UUID){
        _driverId = driverId
    }


}