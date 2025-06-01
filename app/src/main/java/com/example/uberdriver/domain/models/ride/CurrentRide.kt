package com.example.uberdriver.domain.models.ride

import com.example.uberdriver.data.remote.api.backend.socket.ride.model.NearbyRideRequests
import java.util.UUID

data class CurrentRide(
    val riderId: UUID,
    val pickupLongitude: Double,
    val pickupLatitude: Double,
    val dropOffLongitude: Double,
    val dropOffLatitude: Double,
    val rideId : UUID
)

fun NearbyRideRequests.toCurrentRide():CurrentRide{
    return CurrentRide(riderId,pickupLongitude,pickupLatitude,dropOffLongitude,dropOffLatitude,rideId)
}
