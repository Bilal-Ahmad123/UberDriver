package com.example.uberdriver.core.common

import android.content.Context
import android.content.res.Configuration

object Helper {
    fun isDarkMode(context: Context): Boolean {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    fun getUserFetchTime(distance : Double):Int{
        val distanceInMeters = distance * 1609.344
        return (distanceInMeters / 60).toInt()
    }
}