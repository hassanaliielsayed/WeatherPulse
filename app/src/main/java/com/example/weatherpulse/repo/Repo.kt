package com.example.weatherpulse.repo

import android.location.Location
import com.example.weatherpulse.local.WeatherLocalDataSourceInterface
import com.example.weatherpulse.model.WeatherDetailsResponse
import com.example.weatherpulse.remote.WeatherRemoteDataSourceInterface

class Repo(
    private val remoteDataSource: WeatherRemoteDataSourceInterface
): WeatherRepo{
    override suspend fun getCurrentWeather(location: Location): WeatherDetailsResponse {
        return remoteDataSource.getCurrentWeather(location)
    }



    companion object{

        private var INSTANCE: Repo? = null

        fun getInstance(
            remoteDataSource: WeatherRemoteDataSourceInterface
        ) :WeatherRepo{

            return INSTANCE ?: synchronized(this){
                val temp = Repo(remoteDataSource)
                INSTANCE = temp

                temp
            }

        }


    }


}
