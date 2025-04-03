package com.example.weatherpulse.setting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherpulse.repo.WeatherRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel(private val repo: WeatherRepo) : ViewModel() {

    private val _language = MutableStateFlow("")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _unitSystem = MutableStateFlow("")
    val unitSystem: StateFlow<String> = _unitSystem.asStateFlow()

    private val _locationSource = MutableStateFlow("")
    val locationSource: StateFlow<String> = _locationSource.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _language.value = repo.getLanguage()
            _unitSystem.value = repo.getUnitSystem()
            _locationSource.value = repo.getLocationSource()
        }
    }

    fun setLanguage(lang: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repo.setLanguage(lang)
            _language.value = lang
        }
    }

    fun setUnitSystem(unit: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repo.setUnitSystem(unit)
            _unitSystem.value = unit
        }
    }

    fun setLocationSource(source: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repo.setLocationSource(source)
            _locationSource.value = source
        }
    }

    class SettingFactory(private val repo: WeatherRepo) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingViewModel(repo) as T
        }
    }
}