package com.example.uberdriver.domain.usecase

import com.example.uberdriver.data.remote.api.backend.authentication.model.request.CreateDriverRequest
import com.example.uberdriver.data.remote.api.backend.authentication.repository.AuthRepository
import com.example.uberdriver.domain.backend.authentication.model.response.CreateDriver
import javax.inject.Inject

class CreateDriverUseCase @Inject constructor(private val authRepository: AuthRepository){
    suspend operator fun invoke(driver: CreateDriverRequest) = authRepository.createDriver(driver)
}