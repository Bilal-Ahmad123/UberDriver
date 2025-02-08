package com.example.uberdriver.data.local.driver.mapper

import com.example.uberdriver.data.local.driver.entities.Driver


fun Driver.toDomainModel():com.example.uberdriver.domain.local.Driver.model.Driver{
    return com.example.uberdriver.domain.local.Driver.model.Driver(driverId)
}

fun com.example.uberdriver.domain.local.Driver.model.Driver.toEntityModel():Driver{
    return Driver(driverId)
}