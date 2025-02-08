package com.example.uberdriver.data.local.driver.repository

import com.example.uberdriver.domain.local.Driver.model.Driver


interface DriverRoomRepository {
     suspend fun getDriver(): Driver
     suspend fun insertDriver(driver:Driver)
}