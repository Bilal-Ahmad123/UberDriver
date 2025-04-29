package com.example.uberdriver.domain.remote.socket.ride.usecase

import com.example.uberdriver.data.remote.api.backend.socket.ride.repository.RideRepository
import com.example.uberdriver.domain.remote.socket.ride.model.AcceptRideRequest
import javax.inject.Inject

class AcceptRideRequestUseCase @Inject constructor(private val rideRepository: RideRepository){
    operator fun invoke(ride:AcceptRideRequest) = rideRepository.acceptRide(ride)
}