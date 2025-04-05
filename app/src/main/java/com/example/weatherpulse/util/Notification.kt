package com.example.weatherpulse.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.weatherpulse.MainActivity
import com.example.weatherpulse.model.WeatherDetailsResponse
import com.example.weatherpulse.util.Constants.WEATHER_RESPONSE
import com.example.weatherpulse.util.Constants.ZERO
import com.example.weatherpulse.util.Constants.getWeatherIconRes

class Notification(
    context: Context,
    private val weatherDetailsResponse: WeatherDetailsResponse,
    private var title: String,
) : ContextWrapper(context) {

    private val CHANNEL_ID = "Channel ID"
    private val CHANNEL_NAME = "Channel Name"
    private val CHANNEL_DESCRIPTION = "Channel Name"

    private var mManager: NotificationManager? = null
    private var uniqueInt = (System.currentTimeMillis() and 0xfffffff).toInt()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)

        channel.enableVibration(true)
        channel.description = CHANNEL_DESCRIPTION
        getManager()?.createNotificationChannel(channel)
    }

    fun getManager(): NotificationManager? {
        if (mManager == null) {
            mManager = getSystemService(NotificationManager::class.java)
        }
        return mManager
    }

    fun getChannelNotification(): NotificationCompat.Builder {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra(WEATHER_RESPONSE, weatherDetailsResponse)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val bitmap = BitmapFactory.decodeResource(applicationContext.resources, getWeatherIconRes(weatherDetailsResponse.current.weather[ZERO].icon))

        @SuppressLint("UnspecifiedImmutableFlag")
        val pendingIntent = PendingIntent.getActivity(
            this,
            uniqueInt,
            intent,
            PendingIntent.FLAG_ONE_SHOT,
        )
        return NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID,
        )
            .setContentTitle(title)
            .setContentText(weatherDetailsResponse.current.weather[ZERO].description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSmallIcon(getWeatherIconRes(weatherDetailsResponse.current.weather[ZERO].icon))
            .setLargeIcon(bitmap)
    }
}
