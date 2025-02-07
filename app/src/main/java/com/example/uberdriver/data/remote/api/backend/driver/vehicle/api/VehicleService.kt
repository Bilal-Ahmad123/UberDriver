package com.example.uberdriver.data.remote.api.backend.driver.vehicle.api

import com.example.uberdriver.data.remote.api.backend.driver.vehicle.model.request.CreateVehicle
import com.example.uberdriver.data.remote.api.backend.driver.vehicle.model.request.CreateVehicleRequest
import com.example.uberdriver.data.remote.api.backend.driver.vehicle.model.response.CheckVehicleExistsResponse
import com.example.uberdriver.data.remote.api.backend.driver.vehicle.model.response.CreateVehicleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.UUID

interface VehicleService {
    @POST("api/vehicle/create")
    suspend fun createDriverVehicle(
        @Body vehicleRequest:CreateVehicle
    ):Response<CreateVehicleResponse>

    @GET("api/vehicle/exists")
    suspend fun checkVehicleExists(
        @Query("DriverId") driverId: UUID
    ):Response<CheckVehicleExistsResponse>
}