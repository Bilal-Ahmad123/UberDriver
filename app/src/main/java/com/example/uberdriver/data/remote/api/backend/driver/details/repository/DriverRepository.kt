package com.example.uberdriver.data.remote.api.backend.driver.details.repository

import com.example.uberdriver.data.remote.api.backend.driver.details.model.DriverDetails
import com.example.uberdriver.domain.remote.authentication.model.response.DriverExists
import retrofit2.Response
import java.util.UUID

interface DriverRepository {
    suspend fun getDriverDetails(driverId:UUID): Response<DriverDetails>
}