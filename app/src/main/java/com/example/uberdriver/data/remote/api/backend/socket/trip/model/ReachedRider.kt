package com.example.uberdriver.data.remote.api.backend.socket.trip.model

import java.util.UUID

data class ReachedRider(
    val riderId:UUID,
    val driverId: UUID,
    val rideId:UUID,
    val reached:Boolean
)
