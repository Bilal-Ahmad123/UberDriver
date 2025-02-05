package com.example.uberdriver.domain.repository

import com.example.uberdriver.data.remote.api.backend.authentication.api.AuthenticationService
import com.example.uberdriver.data.remote.api.backend.authentication.model.response.CheckDriverExistsResponse
import com.example.uberdriver.data.remote.api.backend.authentication.repository.AuthRepository
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(private val api:AuthenticationService): AuthRepository {
    override suspend fun checkIfDriverExists(email: String):Response<CheckDriverExistsResponse> {
        return try {
            api.checkUserExists(email)
        }
        catch (e:Exception){
            Response.error(500,ResponseBody.create(null, "Network error: ${e.localizedMessage}"))
        }
    }
}