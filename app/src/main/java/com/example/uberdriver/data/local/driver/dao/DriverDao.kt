package com.example.uberdriver.data.local.driver.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.uberdriver.data.local.driver.entities.Driver

@Dao
interface DriverDao {
    @Query("SELECT * from driver order by driverId limit 1")
    fun getDriver():Driver

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDriver(driver: Driver)
}