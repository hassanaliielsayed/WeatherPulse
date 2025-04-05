package com.example.weatherpulse.util

import android.content.Context
import androidx.room.TypeConverter
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.weatherpulse.model.Alarm
import com.example.weatherpulse.util.Constants.MY_ALERT
import com.example.weatherpulse.util.Constants.TIME_IN_MILLIS
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.Calendar
import java.util.concurrent.TimeUnit


object WorkRequestManager {

    fun createWorkRequest(
        alarm: Alarm,
        context: Context,
        timeInMillis: Long
    ) {
        val data = Data.Builder()
            .putString(MY_ALERT, convertAlarmToString(alarm))
            .putLong(TIME_IN_MILLIS, timeInMillis)
            .build()

        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Network constraints
            .setRequiresBatteryNotLow(true) // Battery not low
            //.setRequiresDeviceIdle(true) // Device idle (API 23+)
            .build()

        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyCoroutineWorker::class.java)
            .setInitialDelay(timeInMillis - Calendar.getInstance().timeInMillis, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .setConstraints(constraints)
            .addTag(alarm.id.toString())
            .build()

        WorkManager
            .getInstance(context)
            .enqueueUniqueWork("${alarm.id}", ExistingWorkPolicy.REPLACE, oneTimeWorkRequest)
    }

    fun removeWork(tag: String, context: Context) {
        val worker = WorkManager.getInstance(context)
        worker.cancelAllWorkByTag(tag)
    }

    @TypeConverter
    fun convertToAlarm(value: String): Alarm {
        val type: Type = object : TypeToken<Alarm>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun convertAlarmToString(alarm: Alarm): String = Gson().toJson(alarm)
}
