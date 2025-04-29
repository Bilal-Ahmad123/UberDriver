package com.example.uberdriver.domain.remote.socket.ride.model

import java.util.UUID

data class AcceptRideRequest(
    val rideId: UUID,
    val driverId: UUID,
    val latitude: Double,
    val longitude: Double,
    val riderId: UUID
)
