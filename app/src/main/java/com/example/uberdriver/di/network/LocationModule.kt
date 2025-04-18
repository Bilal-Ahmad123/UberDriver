package com.example.uberdriver.di.network

import com.example.uberdriver.data.remote.api.backend.driver.location.repository.LocationRepository
import com.example.uberdriver.data.remote.api.backend.socket.socketBroker.service.SocketBroker
import com.example.uberdriver.domain.remote.socket.location.repository.LocationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class LocationModule {
    @Provides
    @Singleton
    fun provideLocationRepository(socketManager: SocketBroker):LocationRepository{
        return LocationRepositoryImpl(
            socketManager
        )
    }
}