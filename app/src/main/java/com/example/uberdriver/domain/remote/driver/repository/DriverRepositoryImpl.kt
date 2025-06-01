package com.example.uberdriver.domain.remote.driver.repository

import com.example.uberdriver.data.remote.api.backend.driver.details.api.DriverService
import com.example.uberdriver.data.remote.api.backend.driver.details.model.DriverDetails
import com.example.uberdriver.data.remote.api.backend.driver.details.repository.DriverRepository
import com.example.uberdriver.domain.remote.authentication.model.response.DriverExists
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

class DriverRepositoryImpl @Inject constructor(private val api: DriverService): DriverRepository {
    override suspend fun getDriverDetails(driverId: UUID): Response<DriverDetails> = api.getDriverDetails(driverId)
}