package com.example.uberdriver.data.remote.api.backend.vehicle.mappers

import com.example.uberdriver.domain.remote.vehicle.model.response.CreateVehicle
import com.example.uberdriver.data.remote.api.backend.vehicle.model.response.CreateVehicleResponse

fun CreateVehicleResponse.toDomain():CreateVehicle{
    return CreateVehicle(vehicleId)
}