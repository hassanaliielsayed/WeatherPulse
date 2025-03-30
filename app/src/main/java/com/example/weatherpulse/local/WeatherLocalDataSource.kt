package com.example.weatherpulse.local

import com.example.weatherpulse.model.FavouritePlacesPojo
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(private val dao: WeatherDao): WeatherLocalDataSourceInterface {


    override suspend fun getAllLocations(): Flow<List<FavouritePlacesPojo>> {
        return dao.getAllLocations()
    }

    override suspend fun insertLocation(location: FavouritePlacesPojo): Long {
        return dao.insertLocation(location)
    }

    override suspend fun deleteLocation(location: FavouritePlacesPojo): Int {
        return dao.deleteLocation(location)
    }
}