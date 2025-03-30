package com.example.weatherpulse.local

import com.example.weatherpulse.model.FavouritePlacesPojo
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSourceInterface {

    suspend fun getAllLocations(): Flow<List<FavouritePlacesPojo>>

    suspend fun insertLocation(location: FavouritePlacesPojo): Long

    suspend fun deleteLocation(location: FavouritePlacesPojo): Int

}