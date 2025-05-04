package com.example.uberdriver.data.remote.api.google.model.distanceMatrixResponse

data class DistanceMatrixResponse(
    val destination_addresses: List<String>,
    val origin_addresses: List<String>,
    val rows: List<Row>,
    val status: String
)