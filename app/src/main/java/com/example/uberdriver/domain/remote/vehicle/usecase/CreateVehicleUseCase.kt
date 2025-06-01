package com.example.uberdriver.domain.remote.vehicle.usecase

import com.example.uberdriver.data.remote.api.backend.vehicle.model.request.CreateVehicleRequest
import com.example.uberdriver.data.remote.api.backend.vehicle.repository.VehicleRepository
import javax.inject.Inject

class CreateVehicleUseCase @Inject constructor(private val vehicleRepository: VehicleRepository) {
    suspend operator fun invoke(vehicleRequest: CreateVehicleRequest) = vehicleRepository.createVehicle(vehicleRequest)
}