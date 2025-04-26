package com.example.uberdriver.core.common

import android.content.Context
import android.content.SharedPreferences
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

    fun<T> saveInSharedPreference(key:String,value : T,context: Context){
        val preferences = context.getSharedPreferences("trip_key",Context.MODE_PRIVATE)
        val editor = preferences.edit()
        when(value){
            is Boolean -> editor.putBoolean(key,value)
            is String ->  editor.putString(key,value)
            is Int -> editor.putInt(key,value)
        }
    }

    fun <T> getValueFromSharedPreference(context: Context, key: String, defaultValue: T): T {
        val sharedPref = context.getSharedPreferences("trip_key", Context.MODE_PRIVATE)
        @Suppress("UNCHECKED_CAST")
        return when (defaultValue) {
            is Boolean -> sharedPref.getBoolean(key, defaultValue) as T
            is String -> sharedPref.getString(key, defaultValue) as T
            is Int -> sharedPref.getInt(key, defaultValue) as T
            is Float -> sharedPref.getFloat(key, defaultValue) as T
            is Long -> sharedPref.getLong(key, defaultValue) as T
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

}