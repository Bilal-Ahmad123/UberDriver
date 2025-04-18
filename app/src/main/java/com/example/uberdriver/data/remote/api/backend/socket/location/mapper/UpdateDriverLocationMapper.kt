package com.example.uberdriver.data.remote.api.backend.driver.location.mapper

import com.example.uberdriver.domain.remote.socket.location.model.UpdateLocation
import java.util.UUID

fun UpdateLocation.toData():UpdateDriverLocation{
    return UpdateDriverLocation(driverId, longitude, latitude)
}

data class UpdateDriverLocation(val userId:UUID, val longitude:Double, val latitude:Double)