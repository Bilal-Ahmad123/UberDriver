package com.example.uberdriver.domain.remote.location.usecase

import com.example.uberdriver.data.remote.api.backend.driver.location.repository.SocketRepository
import com.example.uberdriver.domain.remote.location.model.UpdateLocation
import javax.inject.Inject

class SendDriverLocation @Inject constructor(private val repository: SocketRepository) {
     suspend operator fun invoke(location: UpdateLocation) = repository.sendCurrentLocation(location)
}