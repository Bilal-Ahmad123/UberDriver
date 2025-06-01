package com.example.uberdriver.data.remote.api.backend.rider.repository

import com.example.uberdriver.data.remote.api.backend.rider.model.RiderDetails
import retrofit2.Response
import java.util.UUID

interface RiderRepository {
    suspend fun getRiderDetails(riderId: UUID):Response<RiderDetails>
}