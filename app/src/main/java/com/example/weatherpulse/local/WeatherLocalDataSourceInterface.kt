package com.example.weatherpulse.local

import com.example.weatherpulse.model.Alarm
import com.example.weatherpulse.model.FavouritePlacesPojo
import com.example.weatherpulse.util.Constants
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSourceInterface {

    suspend fun getAllLocations(): Flow<List<FavouritePlacesPojo>>

    suspend fun insertLocation(location: FavouritePlacesPojo): Long

    suspend fun deleteLocation(location: FavouritePlacesPojo): Int

    suspend fun getAllAlarms(): Flow<List<Alarm>>

    suspend fun insertAlarm(alarm: Alarm): Long

    suspend fun deleteAlarm(alarm: Alarm): Int

    suspend fun getLanguage(): String
    suspend fun setLanguage(value: String)

    suspend fun getUnitSystem(): String
    suspend fun setUnitSystem(value: String)

    suspend fun getLocationSource(): String
    suspend fun setLocationSource(value: String)

    suspend fun getLat(): Double
    suspend fun setLat(lat: Double)

    suspend fun getLon(): Double
    suspend fun setLon(lon: Double)

    suspend fun setAlarmType(type: Constants.AlarmType)
    suspend fun getAlarmType(): Constants.AlarmType

    suspend fun getCity(): String

}