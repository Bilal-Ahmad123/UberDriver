package com.example.uberdriver.data.remote.api.backend.socket.ride.model

import java.util.UUID

data class TripLocation(
    val rideId: UUID,
    val driverId: UUID,
    val riderId: UUID,
    val latitude: Double,
    val longitude: Double
)
