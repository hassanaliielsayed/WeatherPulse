package com.example.weatherpulse.favourite.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherpulse.model.FavouritePlacesPojo
import com.example.weatherpulse.repo.WeatherRepo
import com.example.weatherpulse.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlin.math.exp

class FavViewModel(private val repo: WeatherRepo): ViewModel() {

    private val _mutableLocations = MutableStateFlow<Result<List<FavouritePlacesPojo>>>(Result.Loading)
    val mutableLocations = _mutableLocations.asStateFlow()

    private val _message = MutableStateFlow<Result<String>>(Result.Loading)
    val message = _message.asStateFlow()

    fun getAllLocations() {

        viewModelScope.launch(Dispatchers.IO) {

            repo.getAllLocations()
                .catch {
                    _mutableLocations.value = Result.Error(it.message.toString())
                }
                .collect{
                    _mutableLocations.value = Result.Success(it)
                }
        }
    }

     fun insertLocation(location: FavouritePlacesPojo?) {

        if (location != null){

            viewModelScope.launch(Dispatchers.IO) {

                try {
                    val result = repo.insertLocation(location)
                    if (result > 0) {
                        _message.value = Result.Success("Added Successfully")
                    } else {
                        _message.value = Result.Error("Can't Added")
                    }
                } catch (ex: Exception) {
                    _message.value = Result.Error("Unknown Error ${ex.message.toString()}")
                }


            }
        }

    }


    fun deleteLocation(location: FavouritePlacesPojo?){
        if (location != null){
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    repo.deleteLocation(location)
                    _message.value = Result.Success("Removed Successfully")
                } catch (ex: Exception) {
                    _message.value = Result.Error("Can't Remove Location ${ex.message.toString()}")
                }
            }
        } else {
            _message.value = Result.Error("Location is Null")
        }
    }





    class FavFactory(private val repo: WeatherRepo): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavViewModel(repo) as T
        }
    }
}