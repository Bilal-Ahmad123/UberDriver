package com.example.uberdriver.domain.remote.socket.location.repository

import com.example.uberdriver.core.common.SocketMethods
import com.example.uberdriver.data.remote.api.backend.driver.location.mapper.UpdateDriverLocation
import com.example.uberdriver.data.remote.api.backend.driver.location.mapper.toData
import com.example.uberdriver.data.remote.api.backend.driver.location.repository.LocationRepository
import com.example.uberdriver.data.remote.api.backend.socket.socketBroker.service.SocketBroker
import com.example.uberdriver.domain.remote.socket.location.model.UpdateLocation
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(private val socket: SocketBroker) :
    LocationRepository {
    override suspend fun sendCurrentLocation(location: UpdateLocation) {
        socket.send<UpdateDriverLocation>(location.toData(),SocketMethods.UPDATE_LOCATION)
    }
}