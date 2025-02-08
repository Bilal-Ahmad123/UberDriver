package com.example.uberdriver.domain.remote.authentication.usecase

import com.example.uberdriver.data.remote.api.backend.authentication.repository.AuthRepository
import com.example.uberdriver.domain.remote.authentication.model.response.DriverExists
import retrofit2.Response
import javax.inject.Inject

class CheckDriverExists @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email:String):Response<DriverExists> = authRepository.checkIfDriverExists(email)
}