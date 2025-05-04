package com.example.uberdriver.domain.remote.google.repository

import com.example.uber.data.remote.api.googleMaps.models.directionsResponse.DirectionsResponse
import com.example.uberdriver.data.remote.api.google.api.GoogleMapService
import com.example.uberdriver.data.remote.api.google.model.distanceMatrixResponse.DistanceMatrixResponse
import com.example.uberdriver.data.remote.api.google.repository.GoogleRepository
import com.google.android.gms.maps.model.LatLng
import retrofit2.Response
import javax.inject.Inject

class GoogleRepository @Inject constructor(private val googleApi: GoogleMapService) :
    GoogleRepository {
    override suspend fun directionsResponse(
        origin: LatLng,
        destination: LatLng,
        wayPoints: LatLng?
    ): Response<DirectionsResponse> {
        val directionsLink: String = ""

        return googleApi.directionsRequest(
            "${origin.latitude},${origin.longitude}",
            "${destination.latitude},${destination.longitude}",
            "${wayPoints?.latitude},${wayPoints?.longitude}"

        )
    }

    override suspend fun directionsResponseNoWaypoints(
        origin: LatLng,
        destination: LatLng,
    ): Response<DirectionsResponse> {
        val directionsLink: String = ""

        return googleApi.directionsRequest(
            "${origin.latitude},${origin.longitude}",
            "${destination.latitude},${destination.longitude}",
        )
    }

    override suspend fun distanceMatrixResponse(
        destination: LatLng,
        origin: LatLng
    ): Response<DistanceMatrixResponse> {
        return googleApi.distanceMatrixRequest(
            "${destination.latitude},${destination.longitude}",
            "${origin.latitude},${origin.longitude}"
        )
    }

}