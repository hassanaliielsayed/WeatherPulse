package com.example.weatherpulse.repo

import android.location.Location
import com.example.weatherpulse.model.WeatherDetailsResponse

interface WeatherRepo {
    suspend fun getCurrentWeather(location: Location): WeatherDetailsResponse
}