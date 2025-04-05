package com.example.weatherpulse.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherpulse.model.Location
import com.example.weatherpulse.model.WeatherDetailsResponse
import com.example.weatherpulse.repo.WeatherRepo
import com.example.weatherpulse.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.location.Location as AndroidLocation

class HomeViewModel(
    private val repo: WeatherRepo
) : ViewModel() {

    private val _mutableCurrentWeather = MutableStateFlow<Result<WeatherDetailsResponse>>(Result.Loading)
    val mutableCurrentWeather: StateFlow<Result<WeatherDetailsResponse>> = _mutableCurrentWeather

    var positionLat = 0.0
    var positionLong = 0.0

    fun getCurrentWeather(deviceLocation: AndroidLocation?, unit: String) {
        viewModelScope.launch {
            try {
                val source = repo.getLocationSource()
                val location = if (source == "map") {
                    positionLat = repo.getLat()
                    positionLong = repo.getLon()
                    AndroidLocation("map").apply {
                        latitude = repo.getLat()
                        longitude = repo.getLon()
                    }
                } else {
                    positionLat = deviceLocation?.latitude ?: 0.0
                    positionLong = deviceLocation?.longitude ?: 0.0
                    deviceLocation
                }

                if (location != null) {
                    _mutableCurrentWeather.value = Result.Loading
                    val response = repo.getCurrentWeather(Location(location.longitude, location.latitude), unit)
                    _mutableCurrentWeather.value = Result.Success(response)
                } else {
                    _mutableCurrentWeather.value = Result.Error("Location not available")
                }
            } catch (e: Exception) {
                _mutableCurrentWeather.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun setNotificationWeatherData(weatherResponse: WeatherDetailsResponse) {
        _mutableCurrentWeather.value = Result.Success(weatherResponse)
    }

    suspend fun getSavedUnitSystem(): String {
        return repo.getUnitSystem()
    }

    suspend fun getSavedCity(): String {
        return repo.getCity()
    }

    class HomeFactory(private val repo: WeatherRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repo) as T
        }
    }
}