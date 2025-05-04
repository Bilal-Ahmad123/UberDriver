package com.example.uberdriver.data.remote.api.backend.socket.ride.repository

import com.example.uberdriver.data.remote.api.backend.socket.ride.model.NearbyRideRequests
import com.example.uberdriver.data.remote.api.backend.socket.ride.model.TripLocation
import com.example.uberdriver.domain.remote.socket.ride.model.AcceptRideRequest
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface RideRepository {
    fun startObservingNearbyRideRequests()
    fun observeRideRequest():Flow<NearbyRideRequests>
    fun acceptRide(ride:AcceptRideRequest)
}