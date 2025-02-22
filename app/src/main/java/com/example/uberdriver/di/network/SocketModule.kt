package com.example.uberdriver.di.network

import com.example.uberdriver.data.remote.api.backend.driver.location.api.SocketManager
import com.example.uberdriver.data.remote.api.backend.driver.location.repository.SocketRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class SocketModule {

    @Provides
    @Singleton
    fun provideSocketManager(): SocketManager {
        return SocketManager()
    }

    @Provides
    @Singleton
    fun provideLocationRepository(socketManager: SocketManager):SocketRepository{
        return com.example.uberdriver.domain.remote.location.repository.SocketRepository(socketManager)
    }
}