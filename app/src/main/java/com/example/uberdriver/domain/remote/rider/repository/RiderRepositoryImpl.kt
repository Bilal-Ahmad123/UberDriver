package com.example.uberdriver.domain.remote.rider.repository

import com.example.uberdriver.data.remote.api.backend.rider.api.RiderService
import com.example.uberdriver.data.remote.api.backend.rider.model.RiderDetails
import com.example.uberdriver.data.remote.api.backend.rider.repository.RiderRepository
import com.example.uberdriver.data.remote.api.backend.vehicle.mappers.toDomain
import okhttp3.ResponseBody
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

class RiderRepositoryImpl @Inject constructor(private val api: RiderService) : RiderRepository {
    override suspend fun getRiderDetails(riderId: UUID): Response<RiderDetails>{
        return try {
            Response.success(api.getRiderDetails(riderId).body())
        }
        catch (e:Exception){
            Response.error(500, ResponseBody.create(null, "Network error: ${e.localizedMessage}"))
        }
    }
}