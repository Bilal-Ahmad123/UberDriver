package com.example.uberdriver.data.remote.api.backend.driver.location.api

import android.util.Log
import com.example.uberdriver.data.remote.api.backend.driver.location.mapper.toData
import com.example.uberdriver.domain.remote.location.model.UpdateLocation
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import javax.inject.Inject

class SocketManager @Inject constructor() {
    private var socket: WebSocket? = null
    private val client = OkHttpClient()
    private lateinit var hubConnection: HubConnection

    fun connect(url:String){
        runCatching {
            hubConnection = HubConnectionBuilder.create(url)
                .withTransport(com.microsoft.signalr.TransportEnum.LONG_POLLING)
                .build()
            hubConnection.start().blockingAwait()
        }.onFailure {
            Log.d("SocketManager", "Error connecting to socket: ${it.message}")
        }
    }
     fun<T> send(data:T,method:String){
        runCatching {
            hubConnection.send(method, data)
        }.onFailure {
            Log.d("SocketManager", "Error sending message: ${it.message}")
        }
    }
    fun disconnect() = hubConnection.stop()
}