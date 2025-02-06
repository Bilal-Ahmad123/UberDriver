package com.example.uberdriver.data.remote.api.backend.authentication.model.response

import com.google.gson.annotations.SerializedName

data class CheckDriverExistsResponse(
    @SerializedName("DriverExists") val driverExists: Boolean
)



