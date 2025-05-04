package com.example.uberdriver.di.network

import com.example.uberdriver.data.remote.api.backend.socket.ride.repository.RideRepository
import com.example.uberdriver.data.remote.api.backend.socket.socketBroker.service.SocketBroker
import com.example.uberdriver.data.remote.api.backend.socket.trip.repository.TripRepository
import com.example.uberdriver.domain.remote.socket.trip.repository.TripRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TripModule {

    @Provides
    @Singleton
    fun provideTripModule(socket: SocketBroker): TripRepository {
        return TripRepositoryImpl(socket)
    }
}