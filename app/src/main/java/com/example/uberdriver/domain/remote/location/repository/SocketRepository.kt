package com.example.uberdriver.domain.remote.location.repository

import com.example.uberdriver.data.remote.api.backend.driver.location.api.SocketManager
import com.example.uberdriver.data.remote.api.backend.driver.location.mapper.UpdateDriverLocation
import com.example.uberdriver.data.remote.api.backend.driver.location.mapper.toData
import com.example.uberdriver.data.remote.api.backend.driver.location.repository.SocketRepository
import com.example.uberdriver.domain.remote.location.model.UpdateLocation
import javax.inject.Inject

class SocketRepository @Inject constructor(private val socket: SocketManager) :
    SocketRepository {
    override suspend fun sendCurrentLocation(location: UpdateLocation) {
        socket.send<UpdateDriverLocation>(location.toData(),"UpdateLocation")
    }

    override fun connect(url: String) {
        socket.connect(url)
    }
}