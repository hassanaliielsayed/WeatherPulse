package com.example.weatherpulse.remote

import android.location.Location
import com.example.weatherpulse.model.WeatherDetailsResponse
import retrofit2.http.Query

interface WeatherRemoteDataSourceInterface {

    suspend fun getCurrentWeather(location: Location): WeatherDetailsResponse
}