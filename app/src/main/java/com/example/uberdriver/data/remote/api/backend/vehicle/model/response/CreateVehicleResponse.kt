package com.example.uberdriver.data.remote.api.backend.vehicle.model.response

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class CreateVehicleResponse(
    @SerializedName("id") val vehicleId:UUID
)