package com.example.weatherpulse.remote

import com.example.weatherpulse.model.Location
import com.example.weatherpulse.model.WeatherDetailsResponse

interface WeatherRemoteDataSourceInterface {

    suspend fun getCurrentWeather(location: Location, unit: String = "metric"): WeatherDetailsResponse
}