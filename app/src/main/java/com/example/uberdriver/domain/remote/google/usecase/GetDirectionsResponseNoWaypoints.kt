package com.example.uberdriver.domain.remote.google.usecase

import com.example.uberdriver.data.remote.api.google.repository.GoogleRepository
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class GetDirectionsResponseNoWaypoints @Inject constructor(private val repository: GoogleRepository) {
    suspend operator fun invoke(origin: LatLng, destination: LatLng) = repository.directionsResponseNoWaypoints(origin, destination )
}