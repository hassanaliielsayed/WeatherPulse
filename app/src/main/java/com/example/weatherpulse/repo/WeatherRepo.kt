package com.example.weatherpulse.repo

import android.location.Location
import com.example.weatherpulse.model.FavouritePlacesPojo
import com.example.weatherpulse.model.WeatherDetailsResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepo {

    suspend fun getCurrentWeather(location: Location): WeatherDetailsResponse

    suspend fun getAllLocations(): Flow<List<FavouritePlacesPojo>>

    suspend fun insertLocation(location: FavouritePlacesPojo): Long

    suspend fun deleteLocation(location: FavouritePlacesPojo): Int


}