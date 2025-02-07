package com.example.uberdriver.data.remote.api.backend.authentication.api

import com.example.uberdriver.data.remote.api.backend.authentication.model.request.CreateDriverReq
import com.example.uberdriver.data.remote.api.backend.authentication.model.response.CheckDriverExistsResponse
import com.example.uberdriver.data.remote.api.backend.authentication.model.response.CreateDriverResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthenticationService {
    @GET("api/driver/exists")
    suspend fun checkUserExists(
        @Query("email") email:String
    ): Response<CheckDriverExistsResponse>

    @POST("api/driver/create")
    suspend fun createDriver(
        @Body body: CreateDriverReq
    ):Response<CreateDriverResponse>
}