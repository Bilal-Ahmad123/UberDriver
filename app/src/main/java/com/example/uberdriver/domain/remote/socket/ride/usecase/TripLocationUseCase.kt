package com.example.uberdriver.domain.remote.socket.ride.usecase

import com.example.uberdriver.data.remote.api.backend.socket.ride.model.TripLocation
import com.example.uberdriver.data.remote.api.backend.socket.ride.repository.RideRepository
import javax.inject.Inject

class TripLocationUseCase @Inject constructor(private val rideRepository: RideRepository) {
    operator fun invoke(trip:TripLocation) = rideRepository.sendTripLocation(trip)
}