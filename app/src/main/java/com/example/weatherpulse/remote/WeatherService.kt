package com.example.weatherpulse.remote

import com.example.weatherpulse.model.WeatherDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("onecall")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appId: String = "02f7303416defaa054fd9589e2bd7ce2",
        @Query("exclude") exclude: String = "minutely,alerts",
        @Query("units") units: String = "metric"
    ): WeatherDetailsResponse
}