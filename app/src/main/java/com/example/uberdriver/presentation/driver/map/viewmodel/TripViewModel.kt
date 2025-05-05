package com.example.uberdriver.presentation.driver.map.viewmodel

import android.util.Log
import com.example.uber.data.remote.api.googleMaps.models.directionsResponse.DirectionsResponse
import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.common.Resource
import com.example.uberdriver.core.dispatcher.IDispatchers
import com.example.uberdriver.data.remote.api.backend.socket.ride.model.NearbyRideRequests
import com.example.uberdriver.data.remote.api.backend.socket.ride.model.TripLocation
import com.example.uberdriver.data.remote.api.backend.socket.trip.model.ReachedRider
import com.example.uberdriver.domain.remote.google.usecase.GetDirectionResponse
import com.example.uberdriver.domain.remote.google.usecase.GetDirectionsResponseNoWaypoints
import com.example.uberdriver.domain.remote.socket.ride.model.AcceptRideRequest
import com.example.uberdriver.domain.remote.socket.ride.usecase.TripLocationUseCase
import com.example.uberdriver.domain.remote.socket.trip.usecase.ReachedRiderUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    dispatcher: IDispatchers,
    private val directionsResponseNoWaypoints: GetDirectionsResponseNoWaypoints,
    private val reachedRiderUseCase: ReachedRiderUseCase,
    private val tripLocationUseCase: TripLocationUseCase
) : BaseViewModel(dispatcher) {
    private val _directions = MutableSharedFlow<Resource<DirectionsResponse>?>()
    val directions get() = _directions.asSharedFlow()

    private val _ride = MutableStateFlow<AcceptRideRequest?>(null)
    val ride get() = _ride.asStateFlow()

    private fun <T> handleResponse(response: Response<T>): Resource<T>? {
        if (response.isSuccessful) {
            return response.body()?.let {
                Resource.Success(it)
            }
        }

        return Resource.Error("Error ${response.code()}: ${response.message()}")
    }

    fun directionsRequest(origin: LatLng, destination: LatLng) {
        launchOnBack {
            val response = directionsResponseNoWaypoints(origin, destination)
            Log.d("directionsRequest", "directionsRequest: $response")
            _directions.emit(handleResponse<DirectionsResponse>(response))
        }
    }

    fun reachedRiderPickUpSpot(value:ReachedRider){
        launchOnBack {
            reachedRiderUseCase(value)
        }
    }



    fun sendTripLocation(trip: TripLocation){
        launchOnBack {
            tripLocationUseCase(trip)
        }
    }

    suspend fun setRide(value: AcceptRideRequest){
        _ride.emit(value)
    }
}