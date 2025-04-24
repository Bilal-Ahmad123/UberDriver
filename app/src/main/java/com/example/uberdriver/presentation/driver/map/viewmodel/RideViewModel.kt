package com.example.uberdriver.presentation.driver.map.viewmodel

import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.dispatcher.IDispatchers
import com.example.uberdriver.data.remote.api.backend.socket.ride.model.NearbyRideRequests
import com.example.uberdriver.domain.remote.socket.ride.usecase.ObserveRideRequests
import com.example.uberdriver.domain.remote.socket.ride.usecase.StartObservingNearbyRideRequests
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class RideViewModel @Inject constructor(
    dispatcher: IDispatchers,
    private val startObservingNearbyRideRequestsUseCase: StartObservingNearbyRideRequests,
    private val observeRideRequestsUseCase: ObserveRideRequests
) : BaseViewModel(dispatcher) {
    private val _rideRequests = MutableStateFlow<NearbyRideRequests?>(null)
    val rideRequests = _rideRequests

    fun startObservingNearbyRideRequests(){
        launchOnBack {
            startObservingNearbyRideRequestsUseCase()
        }
    }

    fun observeRideRequests(){
        launchOnBack {
            observeRideRequestsUseCase().collectLatest {
                _rideRequests.emit(it)
            }
        }
    }
}