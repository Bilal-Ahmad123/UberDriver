package com.example.uberdriver.data.remote.api.backend.driver.vehicle.mappers

import com.example.uberdriver.data.remote.api.backend.driver.vehicle.model.response.CheckVehicleExistsResponse
import com.example.uberdriver.domain.remote.vehicle.model.response.CheckVehicleExists

fun CheckVehicleExistsResponse.toDomain():CheckVehicleExists{
    return CheckVehicleExists(exists)
}