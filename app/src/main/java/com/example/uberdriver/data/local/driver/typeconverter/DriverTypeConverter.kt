package com.example.uberdriver.data.local.driver.typeconverter

import androidx.room.TypeConverter
import java.util.UUID

class DriverTypeConverter {

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? { // ✅ Make UUID nullable
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? { // ✅ Handle null case
        return uuid?.let { UUID.fromString(it) }
    }
}
