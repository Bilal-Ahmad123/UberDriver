package com.example.uberdriver.domain.remote.vehicle.usecase

import com.example.uberdriver.data.remote.api.backend.driver.vehicle.repository.VehicleRepository
import java.util.UUID
import javax.inject.Inject

class GetVehicleDetails @Inject constructor(private val repository: VehicleRepository) {
    suspend operator fun invoke(driverId: UUID) = repository.getVehicleDetails(driverId)
}