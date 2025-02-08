package com.example.uberdriver.data.remote.api.backend.driver.vehicle.mappers

import com.example.uberdriver.domain.remote.vehicle.model.response.CreateVehicle
import com.example.uberdriver.data.remote.api.backend.driver.vehicle.model.response.CreateVehicleResponse

fun CreateVehicleResponse.toDomain():CreateVehicle{
    return CreateVehicle(vehicleId)
}