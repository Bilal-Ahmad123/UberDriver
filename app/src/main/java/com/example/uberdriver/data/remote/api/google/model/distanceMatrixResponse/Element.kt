package com.example.uberdriver.data.remote.api.google.model.distanceMatrixResponse

import com.example.uber.data.remote.api.googleMaps.models.directionsResponse.Distance
import com.example.uber.data.remote.api.googleMaps.models.directionsResponse.Duration

data class Element(
    val distance: Distance,
    val duration: Duration,
    val status: String
)