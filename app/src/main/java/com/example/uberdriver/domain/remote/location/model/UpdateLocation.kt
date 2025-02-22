package com.example.uberdriver.domain.remote.location.model

import java.util.UUID

data class UpdateLocation(val driverId:UUID,val longitude: Double,val latitude:Double)
