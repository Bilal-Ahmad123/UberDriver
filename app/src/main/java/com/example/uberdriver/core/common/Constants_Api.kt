package com.example.uberdriver.core.common

object Constants_Api {
    private const val END_POINT = "192.168.10.7"
    const val BACKEND_AUTH_API = "http://${END_POINT}:5232/"
    const val BACKEND_DRIVER_API = "http://${END_POINT}:5231/"
    const val DB_NAME = "uber_driver_db"
    const val BACKEND_VEHICLE_API = "http://${END_POINT}:5197/"
    const val LOCATION_SOCKET_API = "ws://${END_POINT}:5231/driverhub"
}