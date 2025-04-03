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
    override suspend fun getCurrentWeather(
        location: Location,
        unit: String
    ): WeatherDetailsResponse {
        return remoteDataSource.getCurrentWeather(location, unit)
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

    override suspend fun getLanguage(): String {
        return localDataSource.getLanguage()
    }

    override suspend fun setLanguage(value: String) {
        localDataSource.setLanguage(value)
    }

    override suspend fun getUnitSystem(): String {
        return localDataSource.getUnitSystem()
    }

    override suspend fun setUnitSystem(value: String) {
        localDataSource.setUnitSystem(value)
    }

    override suspend fun getLocationSource(): String {
        return localDataSource.getLocationSource()
    }

    override suspend fun setLocationSource(value: String) {
        localDataSource.setLocationSource(value)
    }

    override suspend fun getLat(): Double = localDataSource.getLat()
    override suspend fun setLat(lat: Double) = localDataSource.setLat(lat)

    override suspend fun getLon(): Double = localDataSource.getLon()
    override suspend fun setLon(lon: Double) = localDataSource.setLon(lon)

    override suspend fun getCity(): String = localDataSource.getCity()



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
