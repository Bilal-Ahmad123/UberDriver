package com.example.uberdriver.data.remote.api.google.api

import com.example.uber.data.remote.api.googleMaps.models.directionsResponse.DirectionsResponse
import com.example.uberdriver.BuildConfig
import com.example.uberdriver.data.remote.api.google.model.distanceMatrixResponse.DistanceMatrixResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleMapService {
    @GET("maps/api/directions/json")
    suspend fun directionsRequest(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("waypoints") waypoints: String? = null,
        @Query("key") accessToken: String = BuildConfig.MAPS_API_KEY,
        @Query("mode") mode: String = "driving"
    ): Response<DirectionsResponse>

    @GET("maps/api/directions/json")
    suspend fun directionsRequestNoWayPoints(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") accessToken: String = BuildConfig.MAPS_API_KEY,
        @Query("mode") mode: String = "driving"
    ): Response<DirectionsResponse>

    @GET("maps/api/distancematrix/json")
    suspend fun distanceMatrixRequest(
        @Query("destination") destination: String,
        @Query("origin") origin: String,
        @Query("key") accessToken: String = BuildConfig.MAPS_API_KEY,
        @Query("mode") mode: String = "driving"
    ):Response<DistanceMatrixResponse>

}