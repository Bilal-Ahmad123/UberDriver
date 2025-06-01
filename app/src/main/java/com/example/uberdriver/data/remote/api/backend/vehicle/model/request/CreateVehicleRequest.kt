package com.example.uberdriver.data.remote.api.backend.vehicle.model.request

import java.util.UUID

data class CreateVehicleRequest(
    val driverId: UUID,
    val vehicleType: String,
    val vehicleModel: String,
    val vehicleYear: String,
    val vehiclePlateNumber: String,
    val vehicleColor: String,
    val vehicleMake: String
)
 {
    init {
        require(vehiclePlateNumber.isNotBlank()) { "Model cannot be blank" }
        require(vehicleType.isNotBlank()) { "Number cannot be blank" }
        require(vehicleColor.isNotBlank()) { "Color cannot be blank" }
        require(vehicleYear.isNotBlank()) { "Year cannot be blank" }
        require(vehicleMake.isNotBlank()) { "Make cannot be blank" }
        require(driverId.toString().isNotBlank()) { "Type cannot be blank" }
        require(vehicleModel.isNotBlank()) { "Type cannot be blank" }
    }
}

data class CreateVehicle(
    private val vehicle: CreateVehicleRequest
)