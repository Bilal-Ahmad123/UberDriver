package com.example.uber.data.remote.api.googleMaps.models.directionsResponse

data class Route(
    val bounds: Bounds,
    val copyrights: String,
    val legs: List<Leg>,
    val overview_polyline: OverviewPolyline,
    val summary: String,
    val warnings: List<String>,
    val waypoint_order: List<Any?>
)