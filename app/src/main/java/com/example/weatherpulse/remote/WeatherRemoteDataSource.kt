package com.example.weatherpulse.remote

import com.example.weatherpulse.model.Location
import com.example.weatherpulse.model.WeatherDetailsResponse

class WeatherRemoteDataSource(private val service: WeatherService): WeatherRemoteDataSourceInterface {

    override suspend fun getCurrentWeather(
        location: Location,
        unit: String
    ): WeatherDetailsResponse =
        service.getCurrentWeather(
            lat = location.latitude,
            lon = location.longitude,
            units = unit
        )
}