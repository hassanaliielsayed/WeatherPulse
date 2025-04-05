package com.example.weatherpulse.model

import java.io.Serializable

data class WeatherDetailsResponse(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Int
): Serializable