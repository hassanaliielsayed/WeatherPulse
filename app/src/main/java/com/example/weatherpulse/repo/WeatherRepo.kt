package com.example.weatherpulse.repo

import android.location.Location
import com.example.weatherpulse.model.FavouritePlacesPojo
import com.example.weatherpulse.model.WeatherDetailsResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepo {

    suspend fun getCurrentWeather(location: Location, unit: String): WeatherDetailsResponse

    suspend fun getAllLocations(): Flow<List<FavouritePlacesPojo>>

    suspend fun insertLocation(location: FavouritePlacesPojo): Long

    suspend fun deleteLocation(location: FavouritePlacesPojo): Int

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


    suspend fun getCity(): String
}