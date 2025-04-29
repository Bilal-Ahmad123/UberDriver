package com.example.uberdriver.data.remote.api.backend.socket.ride.model

import java.util.UUID

data class NearbyRideRequests(
    val riderId: UUID,
    val pickupLongitude: Double,
    val pickupLatitude: Double,
    val dropOffLongitude: Double,
    val dropOffLatitude: Double,
    val id: String = UUID.randomUUID().toString(),
    val rideId : UUID
)