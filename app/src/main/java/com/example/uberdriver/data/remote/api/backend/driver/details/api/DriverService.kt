package com.example.uberdriver.data.remote.api.backend.driver.details.api

import com.example.uberdriver.data.remote.api.backend.driver.details.model.DriverDetails
import com.example.uberdriver.domain.remote.authentication.model.response.DriverExists
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.UUID

interface DriverService {
    @GET("api/driver/details")
    suspend fun getDriverDetails(
        @Query("DriverId") driverId: UUID
    ): Response<DriverDetails>
}