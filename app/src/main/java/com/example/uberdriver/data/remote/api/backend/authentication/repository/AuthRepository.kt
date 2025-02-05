package com.example.uberdriver.data.remote.api.backend.authentication.repository

import com.example.uberdriver.data.remote.api.backend.authentication.model.response.CheckDriverExistsResponse
import retrofit2.Response

interface AuthRepository {
    suspend fun checkIfDriverExists(email:String):Response<CheckDriverExistsResponse>
}