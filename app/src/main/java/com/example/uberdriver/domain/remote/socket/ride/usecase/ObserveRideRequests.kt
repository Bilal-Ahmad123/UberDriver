package com.example.uberdriver.domain.remote.socket.ride.usecase

import com.example.uberdriver.data.remote.api.backend.socket.ride.model.NearbyRideRequests
import com.example.uberdriver.data.remote.api.backend.socket.ride.repository.RideRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRideRequests @Inject constructor(private val repository: RideRepository) {
   operator fun invoke():Flow<NearbyRideRequests> = repository.observeRideRequest()
}