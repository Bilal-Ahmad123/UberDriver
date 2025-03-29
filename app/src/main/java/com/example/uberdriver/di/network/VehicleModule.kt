package com.example.uberdriver.di.network

import com.example.uberdriver.core.common.Constants_Api
import com.example.uberdriver.data.remote.api.backend.driver.vehicle.api.VehicleService
import com.example.uberdriver.domain.remote.vehicle.repository.VehicleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class VehicleModule {
    @Provides
    @Singleton
    fun provideVehicleServiceImpl(): VehicleService {
        return Retrofit.Builder().baseUrl(Constants_Api.BACKEND_VEHICLE_API)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(VehicleService::class.java)
    }

    @Provides
    @Singleton
    fun VehicleRepositoryImpl(api:VehicleService): com.example.uberdriver.data.remote.api.backend.driver.vehicle.repository.VehicleRepository {
        return VehicleRepository(api)
    }
}