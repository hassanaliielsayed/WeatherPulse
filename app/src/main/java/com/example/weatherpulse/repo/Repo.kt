package com.example.weatherpulse.repo

import android.location.Location
import com.example.weatherpulse.local.WeatherLocalDataSourceInterface
import com.example.weatherpulse.model.FavouritePlacesPojo
import com.example.weatherpulse.model.WeatherDetailsResponse
import com.example.weatherpulse.remote.WeatherRemoteDataSourceInterface
import kotlinx.coroutines.flow.Flow

class Repo(
    private val remoteDataSource: WeatherRemoteDataSourceInterface,
    private val localDataSource: WeatherLocalDataSourceInterface
): WeatherRepo{
    override suspend fun getCurrentWeather(location: Location): WeatherDetailsResponse {
        return remoteDataSource.getCurrentWeather(location)
    }

    override suspend fun getAllLocations(): Flow<List<FavouritePlacesPojo>> {
        return localDataSource.getAllLocations()
    }

    override suspend fun insertLocation(location: FavouritePlacesPojo): Long {
        return localDataSource.insertLocation(location)
    }

    override suspend fun deleteLocation(location: FavouritePlacesPojo): Int {
        return localDataSource.deleteLocation(location)
    }


    companion object{

        private var INSTANCE: Repo? = null

        fun getInstance(
            remoteDataSource: WeatherRemoteDataSourceInterface,
            localDataSource: WeatherLocalDataSourceInterface
        ) :WeatherRepo{

            return INSTANCE ?: synchronized(this){
                val temp = Repo(remoteDataSource, localDataSource)
                INSTANCE = temp

                temp
            }

        }


    }


}
