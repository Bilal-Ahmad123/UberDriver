package com.example.uberdriver.domain.remote.google.repository

import com.example.uber.data.remote.api.googleMaps.models.directionsResponse.DirectionsResponse
import com.example.uberdriver.data.remote.api.google.api.GoogleMapService
import com.example.uberdriver.data.remote.api.google.repository.GoogleRepository
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response
import javax.inject.Inject

class GoogleRepository @Inject constructor(private val googleApi: GoogleMapService) :
    GoogleRepository {
    override suspend fun directionsResponse(
        origin: LatLng,
        destination: LatLng
    ): Response<DirectionsResponse> {
        return googleApi.directionsRequest(
            "${origin.latitude},${origin.longitude}",
            "${destination.latitude},${destination.longitude}"
        )
    }

}