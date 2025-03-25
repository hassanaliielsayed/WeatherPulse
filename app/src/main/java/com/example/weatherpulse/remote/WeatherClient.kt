package com.example.weatherpulse.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherClient {

    private const val BASE_URL = "https://api.openweathermap.org/data/3.0/"
    private val retrofitInstance = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val weatherService by lazy {
        retrofitInstance.create(WeatherService::class.java)
    }


}