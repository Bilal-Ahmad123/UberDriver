package com.example.uberdriver.domain.backend.vehicle.usecase

import com.example.uberdriver.data.remote.api.backend.driver.vehicle.repository.VehicleRepository
import java.util.UUID
import javax.inject.Inject

class CheckVehicleExistsUseCase @Inject constructor(private val vehicleRepository: VehicleRepository) {
    suspend operator fun invoke(driverId: UUID) = vehicleRepository.checkVehicleExists(driverId)
}