package com.example.uberdriver.presentation.driver.map.viewmodel

import android.util.Log
import com.example.uber.data.remote.api.googleMaps.models.directionsResponse.DirectionsResponse
import com.example.uberdriver.core.common.BaseViewModel
import com.example.uberdriver.core.common.Resource
import com.example.uberdriver.core.dispatcher.IDispatchers
import com.example.uberdriver.domain.remote.google.usecase.GetDirectionResponse
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class GoogleViewModel @Inject constructor(
    dispatcher: IDispatchers,
    private val directionResponseUseCase: GetDirectionResponse
) : BaseViewModel(dispatcher) {

    private val _directionsResponse = MutableStateFlow<Resource<DirectionsResponse>?>(null)
    val directionResponse get() = _directionsResponse
    fun getDirectionsResponse(origin:LatLng,destination:LatLng,wayPoints:LatLng){
        launchOnBack {
            _directionsResponse.emit(Resource.Loading())
            val result = directionResponseUseCase(origin,destination,wayPoints)
            _directionsResponse.emit(handleResponse(result))
        }
    }

    private fun <T> handleResponse(response: Response<T>): Resource<T>? {
        if (response.isSuccessful) {
            return response.body()?.let {
                Resource.Success(it)
            }
        }
        return Resource.Error("Error ${response.code()}: ${response.message()}")
    }
}