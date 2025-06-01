package com.example.uberdriver.data.remote.api.backend.rider.api

import com.example.uberdriver.data.remote.api.backend.rider.model.RiderDetails
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.UUID

interface RiderService {
    @GET("api/rider/details")
    suspend fun getRiderDetails(
        @Query("RiderId") riderId: UUID
    ):Response<RiderDetails>
}