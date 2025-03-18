package com.example.uberdriver.domain.remote.vehicle.repository

import com.example.uberdriver.data.remote.api.backend.driver.vehicle.api.VehicleService
import com.example.uberdriver.data.remote.api.backend.driver.vehicle.mappers.toDomain
import com.example.uberdriver.data.remote.api.backend.driver.vehicle.model.request.CreateVehicleRequest
import com.example.uberdriver.data.remote.api.backend.driver.vehicle.repository.VehicleRepository
import com.example.uberdriver.domain.remote.vehicle.model.response.CheckVehicleExists
import com.example.uberdriver.domain.remote.vehicle.model.response.CreateVehicle
import com.example.uberdriver.domain.remote.vehicle.model.response.VehicleDetails
import com.example.uberdriver.data.remote.api.backend.driver.vehicle.model.request.CreateVehicle as CreateVehicleReq
import okhttp3.ResponseBody
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

class VehicleRepository @Inject constructor(private val api: VehicleService) :VehicleRepository {
    override suspend fun createVehicle(vehicleRequest: CreateVehicleRequest): Response<CreateVehicle> {
        return try {
            Response.success(api.createDriverVehicle(CreateVehicleReq(vehicleRequest)).body()?.toDomain())
        }
        catch (e:Exception){
            Response.error(500,ResponseBody.create(null, "Network error: ${e.localizedMessage}"))
        }
    }

    override suspend fun checkVehicleExists(driverId: UUID): Response<CheckVehicleExists> {
        return try {
            Response.success(api.checkVehicleExists(driverId).body()?.toDomain())
        }catch (e:Exception){
            Response.error(500,ResponseBody.create(null, "Network error: ${e.localizedMessage}"))
        }
    }

    override suspend fun getVehicleDetails(driverId: UUID): Response<VehicleDetails> {
        return try {
            Response.success(api.getVehicleDetails(driverId).body()?.toDomain())
        }
        catch (e:Exception){
            Response.error(500,ResponseBody.create(null, "Network error: ${e.localizedMessage}"))
        }
    }
}