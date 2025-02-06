package com.example.uberdriver.data.remote.api.backend.authentication.mapper

import com.example.uberdriver.data.remote.api.backend.authentication.model.response.CreateDriverResponse
import com.example.uberdriver.domain.backend.authentication.model.response.CreateDriver

fun CreateDriverResponse.toDomain(): CreateDriver {
    return CreateDriver(driverId)
}