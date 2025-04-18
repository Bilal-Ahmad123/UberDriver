package com.example.uberdriver.domain.remote.socket.socket.usecase

import com.example.uberdriver.data.remote.api.backend.socket.socketBroker.service.SocketBroker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConnectedToSocket @Inject constructor(private val broker: SocketBroker) {
    operator fun invoke(): Flow<Boolean> {
        return broker.connectedToSocket()
    }
}