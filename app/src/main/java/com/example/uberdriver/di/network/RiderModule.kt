package com.example.uberdriver.di.network

import com.example.uberdriver.core.common.Constants_Api
import com.example.uberdriver.data.remote.api.backend.driver.details.api.DriverService
import com.example.uberdriver.data.remote.api.backend.driver.details.repository.DriverRepository
import com.example.uberdriver.data.remote.api.backend.rider.api.RiderService
import com.example.uberdriver.data.remote.api.backend.rider.repository.RiderRepository
import com.example.uberdriver.domain.remote.driver.repository.DriverRepositoryImpl
import com.example.uberdriver.domain.remote.rider.repository.RiderRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RiderModule {
    @Provides
    @Singleton
    fun provideRiderApiService(): RiderService {
        return Retrofit.Builder().baseUrl(Constants_Api.BACKEND_RIDER_API)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(RiderService::class.java)
    }

    @Provides
    @Singleton
    fun provideRiderRepositoryImpl(api : RiderService): RiderRepository {
        return RiderRepositoryImpl(api)
    }
}