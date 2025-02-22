package com.example.uberdriver.data.remote.api.backend.driver.location.repository

import com.example.uberdriver.domain.remote.location.model.UpdateLocation

interface SocketRepository {
    suspend fun sendCurrentLocation(location: UpdateLocation)
    fun connect(url: String)
}