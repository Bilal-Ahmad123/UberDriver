package com.example.uberdriver.domain.remote.driver.usecase

import com.example.uberdriver.data.remote.api.backend.driver.details.model.DriverDetails
import com.example.uberdriver.data.remote.api.backend.driver.details.repository.DriverRepository
import com.example.uberdriver.domain.remote.authentication.model.response.DriverExists
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject

class GetDriverDetailsUseCase @Inject constructor(private val driverRepository: DriverRepository) {
    suspend operator fun invoke(driverId: UUID): Response<DriverDetails> {
        return driverRepository.getDriverDetails(driverId)
    }
}