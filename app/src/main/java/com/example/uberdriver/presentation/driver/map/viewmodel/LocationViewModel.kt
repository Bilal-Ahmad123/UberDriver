package com.example.uberdriver.presentation.driver.map.viewmodel

import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.dispatcher.IDispatchers
import com.example.uberdriver.domain.remote.socket.location.model.UpdateLocation
import com.example.uberdriver.domain.remote.socket.location.usecase.SendDriverLocation
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    dispatcher: IDispatchers,
    private val sendDriverLocationUseCase: SendDriverLocation,
) : BaseViewModel(dispatcher) {

    private val driverLocation = MutableStateFlow<LatLng?>(null)
    val location get() = driverLocation
    fun sendDriverLocation(location: UpdateLocation) {
        launchOnBack {
            sendDriverLocationUseCase(location)
        }
    }

    suspend fun setDriverLocation(location : LatLng?){
        driverLocation.emit(location)
    }
}