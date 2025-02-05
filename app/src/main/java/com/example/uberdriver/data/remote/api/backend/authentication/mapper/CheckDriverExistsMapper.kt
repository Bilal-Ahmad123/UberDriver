package com.example.uberdriver.data.remote.api.backend.authentication.mapper

import com.example.uberdriver.data.remote.api.backend.authentication.model.response.CheckDriverExistsResponse
import com.example.uberdriver.domain.usecase.CheckDriverExists

fun CheckDriverExistsResponse.toDomain(){
    return CheckDriverExists()
}