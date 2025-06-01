package com.example.uberdriver.domain.remote.rider.usecase

import com.example.uberdriver.data.remote.api.backend.rider.repository.RiderRepository
import java.util.UUID
import javax.inject.Inject

class GetRiderDetails @Inject constructor( private val riderRepository: RiderRepository) {
    suspend operator fun invoke(riderId: UUID) = riderRepository.getRiderDetails(riderId)
}