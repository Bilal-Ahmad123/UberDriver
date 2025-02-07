package com.example.uberdriver.data.remote.api.backend.authentication.model.response

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class CreateDriverResponse(
    @SerializedName("id") val driverId:UUID
)