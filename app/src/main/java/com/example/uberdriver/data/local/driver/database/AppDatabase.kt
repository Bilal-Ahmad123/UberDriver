package com.example.uberdriver.data.local.driver.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.uberdriver.data.local.driver.dao.DriverDao
import com.example.uberdriver.data.local.driver.entities.Driver
import com.example.uberdriver.data.local.driver.typeconverter.DriverTypeConverter

@TypeConverters(DriverTypeConverter::class)
@Database(entities = [Driver::class], version = 1)
abstract class AppDatabase:RoomDatabase() {
    abstract fun getDriverDao():DriverDao
}