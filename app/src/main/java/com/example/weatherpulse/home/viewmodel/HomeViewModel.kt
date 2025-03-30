package com.example.weatherpulse.home.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherpulse.model.WeatherDetailsResponse
import com.example.weatherpulse.repo.WeatherRepo
import com.example.weatherpulse.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: WeatherRepo): ViewModel() {

    private val _mutableCurrentWeather = MutableStateFlow<Result<WeatherDetailsResponse>>(Result.Loading)
    val mutableCurrentWeather = _mutableCurrentWeather.asStateFlow()


    fun getCurrentWeather(location: Location){
        viewModelScope.launch(Dispatchers.IO){

            try {

                val result = repo.getCurrentWeather(location)
                _mutableCurrentWeather.value = Result.Success(result)

            } catch (ex: Exception){
                _mutableCurrentWeather.value = Result.Error("Unknown Error: ${ex.message}")
            }
        }
    }



    class HomeFactory(private val repo: WeatherRepo): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repo) as T
        }
    }

}

