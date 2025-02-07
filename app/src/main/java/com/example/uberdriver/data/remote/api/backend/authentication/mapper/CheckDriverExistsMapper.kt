package com.example.uberdriver.data.remote.api.backend.authentication.mapper

import com.example.uberdriver.data.remote.api.backend.authentication.model.response.CheckDriverExistsResponse
import com.example.uberdriver.domain.backend.authentication.model.response.DriverExists

fun CheckDriverExistsResponse.toDomain():DriverExists{
    return DriverExists(driverId)
}