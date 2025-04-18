package com.example.uberdriver.domain.remote.socket.socket.usecase

import com.example.uberdriver.data.remote.api.backend.socket.socketBroker.service.SocketBroker
import com.example.uberdriver.domain.remote.socket.location.repository.LocationRepositoryImpl
import javax.inject.Inject

class ConnectSocketUseCase @Inject constructor(private val broker: SocketBroker) {
     operator fun invoke(url: String) = broker.connect(url)
}