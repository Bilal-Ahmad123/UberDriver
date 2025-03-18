package com.example.uberdriver.data.remote.api.backend.driver.vehicle.repository

import com.example.uberdriver.data.remote.api.backend.driver.vehicle.model.request.CreateVehicleRequest
import com.example.uberdriver.domain.remote.vehicle.model.response.CheckVehicleExists
import com.example.uberdriver.domain.remote.vehicle.model.response.CreateVehicle
import com.example.uberdriver.domain.remote.vehicle.model.response.VehicleDetails
import retrofit2.Response
import java.util.UUID

interface VehicleRepository {
    suspend fun createVehicle(vehicleRequest: CreateVehicleRequest): Response<CreateVehicle>
    suspend fun checkVehicleExists(driverId: UUID): Response<CheckVehicleExists>
    suspend fun getVehicleDetails(driverId: UUID): Response<VehicleDetails>
}