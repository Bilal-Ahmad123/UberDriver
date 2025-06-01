package com.example.uberdriver.di.network

import com.example.uberdriver.core.common.Constants_Api
import com.example.uberdriver.data.remote.api.backend.driver.details.api.DriverService
import com.example.uberdriver.data.remote.api.backend.driver.details.repository.DriverRepository
import com.example.uberdriver.data.remote.api.google.api.GoogleMapService
import com.example.uberdriver.data.remote.api.google.repository.GoogleRepository
import com.example.uberdriver.domain.remote.driver.repository.DriverRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DriverModule {
    @Provides
    @Singleton
    fun provideDriverApiService(): DriverService {
        return Retrofit.Builder().baseUrl(Constants_Api.BACKEND_DRIVER_API)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(DriverService::class.java)
    }

    @Provides
    @Singleton
    fun provideDriverRepositoryImpl(api : DriverService): DriverRepository {
        return DriverRepositoryImpl(api)
    }
}