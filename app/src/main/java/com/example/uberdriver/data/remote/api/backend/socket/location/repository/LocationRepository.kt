package com.example.uberdriver.data.remote.api.backend.driver.location.repository

import com.example.uberdriver.domain.remote.socket.location.model.UpdateLocation

interface LocationRepository {
    suspend fun sendCurrentLocation(location: UpdateLocation)
}