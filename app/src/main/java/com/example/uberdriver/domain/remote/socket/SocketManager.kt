package com.example.uberdriver.domain.remote.socket

import android.util.Log
import com.example.uberdriver.data.remote.api.backend.socket.socketBroker.service.SocketBroker
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketManager @Inject constructor() : SocketBroker {
    private var hubConnection: HubConnection ? = null
    private val connectedToSocket = MutableSharedFlow<Boolean>()

    override fun connect(url:String){
        runCatching {
            hubConnection = HubConnectionBuilder.create(url)
                .withTransport(com.microsoft.signalr.TransportEnum.LONG_POLLING)
                .build()
            hubConnection?.start()?.blockingAwait()
        }.onFailure {
            Log.d("SocketManager", "Error connecting to socket: ${it.message}")
        }
    }
     override fun<T> send(message:T, method:String){
        runCatching {
            hubConnection?.send(method, message)
        }.onFailure {
            Log.d("SocketManager", "Error sending message: ${it.message}")
        }
    }
    override fun disconnect(): Unit {
        hubConnection?.stop()
    }

    override fun getHubConnection(): HubConnection? = hubConnection

    override fun connectedToSocket(): Flow<Boolean> = connectedToSocket
}