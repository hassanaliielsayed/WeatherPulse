package com.example.weatherpulse.alarm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherpulse.model.Alarm
import com.example.weatherpulse.repo.WeatherRepo
import com.example.weatherpulse.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class AlarmViewModel (
    private val repo: WeatherRepo 
): ViewModel()
{

    private val _mutableAlarms = MutableStateFlow<Result<List<Alarm>>>(Result.Loading)
    val mutableAlarms = _mutableAlarms.asStateFlow()

    private val _message = MutableStateFlow<Result<String>>(Result.Loading)
    val message = _message.asStateFlow()
    
    
    fun getAllAlarms() {
        
        viewModelScope.launch(Dispatchers.IO) { 
            
            repo.getAllAlarms()
                .catch {
                _mutableAlarms.value = Result.Error(it.message.toString())
                }
                .collect{
                    _mutableAlarms.value = Result.Success(it)   
                }
        }
        
    }
    
    fun insertAlarm(alarm: Alarm?) {
        if (alarm != null) {
            
            viewModelScope.launch(Dispatchers.IO) { 
                try {

                    val result = repo.insertAlarm(alarm)
                    if (result > 0) {
                        _message.value = Result.Success("Added Successfully")
                    } else {
                        _message.value = Result.Error("Can't Added")
                    }
                    
                } catch (ex: Exception) {
                    _message.value = Result.Error("Unknown Error ${ex.message.toString()}")
                }
            }
        } else {
            _message.value = Result.Error("Alarm is Null")
        }
    }
    
    fun deleteAlarm(alarm: Alarm?) {
        if (alarm != null) {
            
            viewModelScope.launch(Dispatchers.IO) { 
                try {
                    val result = repo.deleteAlarm(alarm)
                    if (result > 0) {
                        _message.value = Result.Success("Added Successfully")
                    } else {
                        _message.value = Result.Error("Can't Removed")
                    }
                } catch (ex: Exception) {
                    _message.value = Result.Error("Unknown Error ${ex.message.toString()}")
                }
            }
        } else {
            _message.value = Result.Error("Alarm is Null")
        }
    }






    class AlarmFactory(private val repo: WeatherRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlarmViewModel(repo) as T
        }
    }
}