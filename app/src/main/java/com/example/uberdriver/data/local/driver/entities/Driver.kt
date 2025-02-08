package com.example.uberdriver.data.local.driver.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "driver")
data class Driver (
    @PrimaryKey val driverId: UUID
)