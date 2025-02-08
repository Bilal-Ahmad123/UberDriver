package com.example.uberdriver.domain.local.Driver.usecase

import com.example.uberdriver.data.local.driver.repository.DriverRoomRepository
import javax.inject.Inject

class InsertDriver @Inject constructor(private val repository: DriverRoomRepository) {
    suspend operator fun invoke(driver: com.example.uberdriver.domain.local.Driver.model.Driver) {
        repository.insertDriver(driver)
    }
}