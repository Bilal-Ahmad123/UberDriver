package com.example.uberdriver.di.network

import com.example.uberdriver.core.common.Constants_Api
import com.example.uberdriver.data.remote.api.backend.driver.vehicle.api.VehicleService
import com.example.uberdriver.data.remote.api.google.api.GoogleMapService
import com.example.uberdriver.data.remote.api.google.repository.GoogleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GoogleViewModel {
    @Provides
    @Singleton
    fun provideGoogleApiService():GoogleMapService{
        return Retrofit.Builder().baseUrl(Constants_Api.GOOGLE_MAPS_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(GoogleMapService::class.java)
    }

    @Provides
    @Singleton
    fun provideGoogleRepositoryImpl(api : GoogleMapService):GoogleRepository{
        return com.example.uberdriver.domain.remote.google.repository.GoogleRepository(api)
    }
}