package com.example.uberdriver.data.remote.api.backend.authentication.api

import com.example.uberdriver.data.remote.api.backend.authentication.model.response.CheckDriverExistsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthenticationService {
    @GET("api/rider/exists")
    suspend fun checkUserExists(
        @Query("email") email:String
    ): Response<CheckDriverExistsResponse>
}