package com.example.uberdriver.data.remote.api.backend.driver.vehicle.model.response

data class GetVehicleDetailsResponse(
    val vehicleMake: String,
    val vehicleModel: String,
    val vehicleYear: String,
    val vehicleColor: String,
    val vehiclePlateNumber: String,
    val vehicleType: String
)
