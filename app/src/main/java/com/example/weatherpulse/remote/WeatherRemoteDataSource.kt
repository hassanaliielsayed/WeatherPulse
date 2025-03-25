package com.example.weatherpulse.remote

import android.location.Location
import com.example.weatherpulse.model.WeatherDetailsResponse

class WeatherRemoteDataSource(private val service: WeatherService): WeatherRemoteDataSourceInterface {
    override suspend fun getCurrentWeather(location: Location): WeatherDetailsResponse =
        service.getCurrentWeather(
            lat = location.latitude,
            lon = location.longitude
        )

}