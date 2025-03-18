package com.example.uberdriver.domain.remote.vehicle.model.response

data class VehicleDetails(
    val make: String,
    val model: String,
    val year: String,
    val color: String,
    val plateNumber: String,
    val type: String
)