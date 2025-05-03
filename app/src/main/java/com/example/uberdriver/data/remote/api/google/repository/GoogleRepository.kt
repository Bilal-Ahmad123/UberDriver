package com.example.uberdriver.data.remote.api.google.repository

import com.example.uber.data.remote.api.googleMaps.models.directionsResponse.DirectionsResponse
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response

interface GoogleRepository {
    suspend fun directionsResponse(origin: LatLng, destination: LatLng,wayPoints:LatLng? = null): Response<DirectionsResponse>
    suspend fun directionsResponseNoWaypoints(origin: LatLng, destination: LatLng): Response<DirectionsResponse>

}