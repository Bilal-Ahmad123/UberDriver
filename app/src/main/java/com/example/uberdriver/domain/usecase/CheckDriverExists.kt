package com.example.uberdriver.domain.usecase

import com.example.uberdriver.data.remote.api.backend.authentication.model.response.CheckDriverExistsResponse
import com.example.uberdriver.data.remote.api.backend.authentication.repository.AuthRepository
import com.example.uberdriver.domain.backend.authentication.model.response.DriverExists
import retrofit2.Response
import javax.inject.Inject

class CheckDriverExists @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email:String):Response<DriverExists> = authRepository.checkIfDriverExists(email)
}