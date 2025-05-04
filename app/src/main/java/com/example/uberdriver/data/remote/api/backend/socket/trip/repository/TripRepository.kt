package com.example.uberdriver.data.remote.api.backend.socket.trip.repository

import com.example.uberdriver.data.remote.api.backend.socket.ride.model.TripLocation
import com.example.uberdriver.data.remote.api.backend.socket.trip.model.ReachedRider

interface TripRepository {
    fun reachedRider(value:ReachedRider)
    fun sendTripLocation(trip: TripLocation)
}