package com.example.uberdriver.data.remote.api.backend.vehicle.mappers

import com.example.uberdriver.data.remote.api.backend.vehicle.model.response.GetVehicleDetailsResponse
import com.example.uberdriver.domain.remote.vehicle.model.response.VehicleDetails

fun GetVehicleDetailsResponse.toDomain():VehicleDetails{
    return VehicleDetails (
        make = vehicleMake,
        model = vehicleModel,
        year = vehicleYear,
        color = vehicleColor,
        plateNumber = vehiclePlateNumber,
        type = vehicleType
    )
}