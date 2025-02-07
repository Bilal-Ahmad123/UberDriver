package com.example.uberdriver.data.remote.api.backend.driver.vehicle.model.response

import com.google.gson.annotations.SerializedName

data class CheckVehicleExistsResponse(
    @SerializedName("exists") val exists:Boolean
)