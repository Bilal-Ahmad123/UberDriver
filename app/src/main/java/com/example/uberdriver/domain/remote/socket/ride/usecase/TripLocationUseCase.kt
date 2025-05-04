package com.example.uberdriver.domain.remote.socket.ride.usecase

import com.example.uberdriver.data.remote.api.backend.socket.ride.model.TripLocation
import com.example.uberdriver.data.remote.api.backend.socket.ride.repository.RideRepository
import com.example.uberdriver.data.remote.api.backend.socket.trip.repository.TripRepository
import javax.inject.Inject

class TripLocationUseCase @Inject constructor(private val tripRepository: TripRepository) {
    operator fun invoke(trip:TripLocation) = tripRepository.sendTripLocation(trip)
}