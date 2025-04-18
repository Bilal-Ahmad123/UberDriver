package com.example.uberdriver.di.network

import com.example.uberdriver.data.remote.api.backend.socket.ride.repository.RideRepository
import com.example.uberdriver.data.remote.api.backend.socket.socketBroker.service.SocketBroker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RideModule {

    @Provides
    @Singleton
    fun provideRideModule(socket:SocketBroker):RideRepository{
        return com.example.uberdriver.domain.remote.socket.ride.repository.RideRepository(socket)
    }
}