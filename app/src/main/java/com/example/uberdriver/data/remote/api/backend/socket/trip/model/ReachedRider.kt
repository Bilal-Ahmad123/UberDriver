package com.example.uberdriver.data.remote.api.backend.socket.trip.model

import java.util.UUID

data class ReachedRider(
    val driverId: UUID,
    val driverReachedPickUp: Boolean,
    val latitude: Double,
    val longitude: Double
)
