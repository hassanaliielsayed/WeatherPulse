package com.example.weatherpulse.local.sharedpref

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherpulse.util.Constants.EMPTY

class SharedPref private constructor(context: Context): SharedPrefInterface {

    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "MyPref"
        private var instance: SharedPref? = null

        fun getInstance(context: Context): SharedPref {
            return instance ?: synchronized(this){
                instance ?: SharedPref(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    override fun getLanguage(): String = sharedPref.getString("language", "device") ?: "device"

    override fun setLanguage(value: String) = sharedPref.edit().putString("language", value).apply()

    override fun getUnitSystem(): String = sharedPref.getString("unit_system", "metric") ?: "metric"

    override fun setUnitSystem(value: String) = sharedPref.edit().putString("unit_system", value).apply()

    override fun getLocationSource(): String = sharedPref.getString("location_source", "gps") ?: "gps"

    override fun setLocationSource(value: String) = sharedPref.edit().putString("location_source", value).apply()

    override fun getLat(): Double = Double.fromBits(sharedPref.getLong("lat", 0L))

    override fun setLat(lat: Double) = sharedPref.edit().putLong("lat", lat.toBits()).apply()

    override fun getLon(): Double = Double.fromBits(sharedPref.getLong("lon", 0L))

    override fun setLon(lon: Double) = sharedPref.edit().putLong("lon", lon.toBits()).apply()

    override fun getCity(): String = sharedPref.getString("city", "") ?: ""

    override fun setCity(city: String) = sharedPref.edit().putString("city", city).apply()

    override suspend fun setAlarmType(type: String) = sharedPref.edit().putString("alarmType", type).apply()

    override suspend fun getAlarmType(): String = sharedPref.getString("alarmType", EMPTY) ?: EMPTY
}