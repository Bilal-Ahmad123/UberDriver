package com.example.uberdriver.presentation.driver.map.viewmodel

import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.dispatcher.IDispatchers
import com.example.uberdriver.data.remote.api.backend.socket.ride.model.NearbyRideRequests
import com.example.uberdriver.data.remote.api.backend.socket.ride.model.TripLocation
import com.example.uberdriver.domain.models.ride.CurrentRide
import com.example.uberdriver.domain.remote.socket.ride.model.AcceptRideRequest
import com.example.uberdriver.domain.remote.socket.ride.usecase.AcceptRideRequestUseCase
import com.example.uberdriver.domain.remote.socket.ride.usecase.ObserveRideRequests
import com.example.uberdriver.domain.remote.socket.ride.usecase.StartObservingNearbyRideRequests
import com.example.uberdriver.domain.remote.socket.ride.usecase.TripLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RideViewModel @Inject constructor(
    dispatcher: IDispatchers,
    private val startObservingNearbyRideRequestsUseCase: StartObservingNearbyRideRequests,
    private val observeRideRequestsUseCase: ObserveRideRequests,
    private val acceptRideRequestUseCase: AcceptRideRequestUseCase,
    private val tripLocationUseCase: TripLocationUseCase
) : BaseViewModel(dispatcher) {
    private val _rideRequests = MutableStateFlow<NearbyRideRequests?>(null)
    val rideRequests = _rideRequests

    private var _currentRide:CurrentRide? = null
    val currentRide get() = _currentRide

    private var _rideStarted= MutableStateFlow<Boolean>(false);
    val rideStarted get() = _rideStarted

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

    fun acceptRideRequest(ride:AcceptRideRequest){
        launchOnBack {
            acceptRideRequestUseCase(ride)
        }
    }

    fun setCurrentRide(ride: CurrentRide){
        _currentRide = ride
    }

    suspend fun rideStarted(value:Boolean){
        _rideStarted.emit(value)
    }

}