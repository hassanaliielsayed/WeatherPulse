package com.example.weatherpulse.util

import android.content.Context
import com.example.weatherpulse.R
import com.example.weatherpulse.model.Alarm
import com.example.weatherpulse.model.WeatherDetailsResponse

object Constants {

    const val EMPTY: String = ""
    const val MY_ALERT: String = "my alert"
    const val TIME_IN_MILLIS: String = "timeInMillis"
    const val WEATHER_RESPONSE: String = "Weather_Response"

    const val ZERO = 0

    enum class AlarmType {
        ALARM, NOTIFICATION
    }

    fun openNotification(context: Context, alarm: Alarm, weatherDetailsResponse: WeatherDetailsResponse, title: String) {
        val notificationHelper = Notification(context, weatherDetailsResponse, title)
        val nb = notificationHelper.getChannelNotification()
        notificationHelper.getManager()?.notify(alarm.id.hashCode(), nb.build())
    }

    fun getWeatherIconRes(imageString: String): Int {
        val imageInInteger: Int
        when (imageString) {
            "01d" -> imageInInteger = R.drawable.icon_01d
            "01n" -> imageInInteger = R.drawable.icon_01n
            "02d" -> imageInInteger = R.drawable.icon_02d
            "02n" -> imageInInteger = R.drawable.icon_02n
            "03n" -> imageInInteger = R.drawable.icon_03n
            "03d" -> imageInInteger = R.drawable.icon_03d
            "04d" -> imageInInteger = R.drawable.icon_04d
            "04n" -> imageInInteger = R.drawable.icon_04n
            "09d" -> imageInInteger = R.drawable.icon_09d
            "09n" -> imageInInteger = R.drawable.icon_09n
            "10d" -> imageInInteger = R.drawable.icon_10d
            "10n" -> imageInInteger = R.drawable.icon_10n
            "11d" -> imageInInteger = R.drawable.icon_11d
            "11n" -> imageInInteger = R.drawable.icon_11n
            "13d" -> imageInInteger = R.drawable.icon_13d
            "13n" -> imageInInteger = R.drawable.icon_13n
            "50d" -> imageInInteger = R.drawable.icon_50d
            "50n" -> imageInInteger = R.drawable.icon_50n
            else -> imageInInteger = R.drawable.icon_50n
        }
        return imageInInteger
    }
}