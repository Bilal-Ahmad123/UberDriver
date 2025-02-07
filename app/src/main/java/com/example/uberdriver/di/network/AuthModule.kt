package com.example.uberdriver.di.network

import com.example.uberdriver.core.common.Constants_Api
import com.example.uberdriver.data.remote.api.backend.authentication.api.AuthenticationService
import com.example.uberdriver.data.remote.api.backend.authentication.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {
    @Provides
    @Singleton
    fun provideAuthenticationServiceImpl(): AuthenticationService {
        return Retrofit.Builder().baseUrl(Constants_Api.BACKEND_AUTH_API)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(AuthenticationService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepositoryImpl(api:AuthenticationService): AuthRepository {
        return com.example.uberdriver.domain.repository.AuthRepository(
            api
        )
    }
}