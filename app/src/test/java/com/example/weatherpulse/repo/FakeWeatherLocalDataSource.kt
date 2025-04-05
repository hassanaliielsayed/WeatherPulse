package com.example.weatherpulse.repo

import com.example.weatherpulse.local.WeatherLocalDataSourceInterface
import com.example.weatherpulse.model.Alarm
import com.example.weatherpulse.model.FavouritePlacesPojo
import com.example.weatherpulse.util.Constants
import kotlinx.coroutines.flow.Flow

class FakeWeatherLocalDataSource(
    val favDataSource: MutableList<FavouritePlacesPojo>,
    val alarmDataSource: MutableList<Alarm>
) : WeatherLocalDataSourceInterface {
    override suspend fun getAllLocations(): Flow<List<FavouritePlacesPojo>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertLocation(location: FavouritePlacesPojo): Long {
        if (location !in favDataSource){
            favDataSource.add(location)
            return 1L
        } else {
            return 0L
        }
    }

    override suspend fun deleteLocation(location: FavouritePlacesPojo): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getAllAlarms(): Flow<List<Alarm>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlarm(alarm: Alarm): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlarm(alarm: Alarm): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getLanguage(): String {
        TODO("Not yet implemented")
    }

    override suspend fun setLanguage(value: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getUnitSystem(): String {
        TODO("Not yet implemented")
    }

    override suspend fun setUnitSystem(value: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getLocationSource(): String {
        TODO("Not yet implemented")
    }

    override suspend fun setLocationSource(value: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getLat(): Double {
        TODO("Not yet implemented")
    }

    override suspend fun setLat(lat: Double) {
        TODO("Not yet implemented")
    }

    override suspend fun getLon(): Double {
        TODO("Not yet implemented")
    }

    override suspend fun setLon(lon: Double) {
        TODO("Not yet implemented")
    }

    override suspend fun setAlarmType(type: Constants.AlarmType) {
        TODO("Not yet implemented")
    }

    override suspend fun getAlarmType(): Constants.AlarmType {
        TODO("Not yet implemented")
    }

    override suspend fun getCity(): String {
        TODO("Not yet implemented")
    }


}