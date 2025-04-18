package com.example.uberdriver.data.remote.api.backend.socket.ride.repository

import com.example.uberdriver.data.remote.api.backend.socket.ride.model.NearbyRideRequests
import kotlinx.coroutines.flow.Flow

interface RideRepository {
    fun startObservingNearbyRideRequests()
    fun observeRideRequest():Flow<NearbyRideRequests>
}