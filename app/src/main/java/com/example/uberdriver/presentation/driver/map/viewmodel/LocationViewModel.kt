package com.example.uberdriver.presentation.driver.map.viewmodel

import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.dispatcher.IDispatchers
import com.example.uberdriver.domain.remote.socket.location.model.UpdateLocation
import com.example.uberdriver.domain.remote.socket.location.usecase.SendDriverLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    dispatcher: IDispatchers,
    private val sendDriverLocationUseCase: SendDriverLocation,
) : BaseViewModel(dispatcher) {
    fun sendDriverLocation(location: UpdateLocation) {
        launchOnBack {
            sendDriverLocationUseCase(location)
        }
    }
}