package com.example.uberdriver.domain.remote.socket.ride.usecase

import com.example.uberdriver.data.remote.api.backend.socket.ride.repository.RideRepository
import javax.inject.Inject

class StartObservingNearbyRideRequests @Inject constructor(private val repository : RideRepository) {
    operator fun invoke () = repository.startObservingNearbyRideRequests()
}