package com.example.uberdriver.presentation.driver.map.viewmodel

import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.dispatcher.IDispatchers
import com.example.uberdriver.domain.remote.location.model.UpdateLocation
import com.example.uberdriver.domain.remote.location.usecase.ConnectSocketUseCase
import com.example.uberdriver.domain.remote.location.usecase.SendDriverLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SocketViewModel @Inject constructor(
    private val sendDriverLocationUseCase: SendDriverLocation,
    dispatcher: IDispatchers,
    private val connectSocketUseCase: ConnectSocketUseCase
) : BaseViewModel(dispatcher) {
    fun sendDriverLocation(location: UpdateLocation) {
        launchOnBack {
            sendDriverLocationUseCase(location)
        }
    }

    fun connectSocket(url: String) {
        connectSocketUseCase(url)
    }
}