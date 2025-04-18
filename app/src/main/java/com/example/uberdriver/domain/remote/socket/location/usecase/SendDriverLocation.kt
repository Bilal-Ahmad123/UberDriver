package com.example.uberdriver.domain.remote.socket.location.usecase

import com.example.uberdriver.data.remote.api.backend.driver.location.repository.LocationRepository
import com.example.uberdriver.domain.remote.socket.location.model.UpdateLocation
import javax.inject.Inject

class SendDriverLocation @Inject constructor(private val repository: LocationRepository) {
     suspend operator fun invoke(location: UpdateLocation) = repository.sendCurrentLocation(location)
}