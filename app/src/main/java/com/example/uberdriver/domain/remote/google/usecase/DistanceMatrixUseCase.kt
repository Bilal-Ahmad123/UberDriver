package com.example.uberdriver.domain.remote.google.usecase

import com.example.uberdriver.data.remote.api.google.repository.GoogleRepository
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class DistanceMatrixUseCase @Inject constructor(private val repository: GoogleRepository) {
    suspend operator fun invoke(origin: LatLng, destination: LatLng, wayPoints: LatLng? = null) = repository.distanceMatrixResponse(origin, destination )
}