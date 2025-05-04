package com.example.uberdriver.data.remote.api.google.repository

import com.example.uber.data.remote.api.googleMaps.models.directionsResponse.DirectionsResponse
import com.example.uberdriver.data.remote.api.google.model.distanceMatrixResponse.DistanceMatrixResponse
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response

interface GoogleRepository {
    suspend fun directionsResponse(origin: LatLng, destination: LatLng,wayPoints:LatLng? = null): Response<DirectionsResponse>
    suspend fun directionsResponseNoWaypoints(origin: LatLng, destination: LatLng): Response<DirectionsResponse>
    suspend fun distanceMatrixResponse(destination: LatLng,origin: LatLng):Response<DistanceMatrixResponse>

}