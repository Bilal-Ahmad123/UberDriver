package com.example.uberdriver.domain.local.Driver.repository

import com.example.uberdriver.data.local.driver.dao.DriverDao
import com.example.uberdriver.data.local.driver.mapper.toDomainModel
import com.example.uberdriver.data.local.driver.mapper.toEntityModel
import com.example.uberdriver.data.local.driver.repository.DriverRoomRepository
import com.example.uberdriver.domain.local.Driver.model.Driver
import javax.inject.Inject

class DriverRoomRepository @Inject constructor(private val driverDao: DriverDao) :
    DriverRoomRepository {
    override suspend fun getDriver(): Driver {
        return try {
            driverDao.getDriver().toDomainModel()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun insertDriver(driver: Driver) {
        driverDao.insertDriver(driver.toEntityModel())
    }
}