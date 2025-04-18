package com.example.uberdriver.di.network

import com.example.uberdriver.data.remote.api.backend.socket.socketBroker.service.SocketBroker
import com.example.uberdriver.domain.remote.socket.socket.api.SocketManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BrokerModule {

    @Binds
    @Singleton
    abstract fun bindSocketBroker(manager: SocketManager): SocketBroker

}