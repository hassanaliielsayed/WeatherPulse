package com.example.weatherpulse.local

import SharedPrefInterface
import com.example.weatherpulse.local.db.WeatherDao
import com.example.weatherpulse.model.Alarm
import com.example.weatherpulse.model.FavouritePlacesPojo
import com.example.weatherpulse.util.Constants
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(
    private val dao: WeatherDao,
    private val sharedPref: SharedPrefInterface
): WeatherLocalDataSourceInterface {


    override suspend fun getAllLocations(): Flow<List<FavouritePlacesPojo>> {
        return dao.getAllLocations()
    }

    override suspend fun insertLocation(location: FavouritePlacesPojo): Long {
        return dao.insertLocation(location)
    }

    override suspend fun deleteLocation(location: FavouritePlacesPojo): Int {
        return dao.deleteLocation(location)
    }

    override suspend fun getAllAlarms(): Flow<List<Alarm>> {
        return dao.getAllAlarms()
    }

    override suspend fun insertAlarm(alarm: Alarm): Long {
        return dao.insertAlarm(alarm)
    }

    override suspend fun deleteAlarm(alarm: Alarm): Int {
        return dao.deleteAlarm(alarm)
    }

    override suspend fun getLanguage(): String = sharedPref.getLanguage()

    override suspend fun setLanguage(value: String) = sharedPref.setLanguage(value)

    override suspend fun getUnitSystem(): String = sharedPref.getUnitSystem()

    override suspend fun setUnitSystem(value: String) = sharedPref.setUnitSystem(value)

    override suspend fun getLocationSource(): String = sharedPref.getLocationSource()

    override suspend fun setLocationSource(value: String) = sharedPref.setLocationSource(value)

    override suspend fun getLat(): Double = sharedPref.getLat()

    override suspend fun setLat(lat: Double) = sharedPref.setLat(lat)

    override suspend fun getLon(): Double = sharedPref.getLon()

    override suspend fun setLon(lon: Double) = sharedPref.setLon(lon)

    override suspend fun setAlarmType(type: Constants.AlarmType) = sharedPref.setAlarmType(type.name)

    override suspend fun getAlarmType(): Constants.AlarmType {
        return if (sharedPref.getAlarmType() == Constants.AlarmType.NOTIFICATION.name) {
            Constants.AlarmType.NOTIFICATION
        } else {
            Constants.AlarmType.ALARM
        }
    }

    override suspend fun getCity(): String = sharedPref.getCity()
}