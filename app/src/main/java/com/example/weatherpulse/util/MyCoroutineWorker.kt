package com.example.weatherpulse.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherpulse.R
import com.example.weatherpulse.local.WeatherLocalDataSource
import com.example.weatherpulse.local.db.WeatherDataBase
import com.example.weatherpulse.local.sharedpref.SharedPref
import com.example.weatherpulse.model.Alarm
import com.example.weatherpulse.model.Location
import com.example.weatherpulse.remote.WeatherClient
import com.example.weatherpulse.remote.WeatherRemoteDataSource
import com.example.weatherpulse.repo.Repo
import com.example.weatherpulse.repo.WeatherRepo
import com.example.weatherpulse.util.Constants.EMPTY
import com.example.weatherpulse.util.Constants.MY_ALERT
import com.example.weatherpulse.util.Constants.openNotification
import com.example.weatherpulse.util.WorkRequestManager.convertToAlarm
import com.example.weatherpulse.util.WorkRequestManager.removeWork
import kotlinx.coroutines.*
import java.util.*

class MyCoroutineWorker(private val context: Context, parameters: WorkerParameters) : CoroutineWorker(context, parameters) {

    private lateinit var repo: WeatherRepo
    private lateinit var sharedPref: SharedPref

    override suspend fun doWork(): Result {
        val alarm = convertToAlarm(inputData.getString(MY_ALERT) ?: EMPTY)
        sharedPref = SharedPref.getInstance(context)

        repo = Repo.getInstance(
            WeatherRemoteDataSource(WeatherClient.weatherService),
            WeatherLocalDataSource(
                WeatherDataBase.getInstance(context).getDao(),
                sharedPref,
            )
        )

        // call the weather api to get the details using async await
        val deferredJob = CoroutineScope(Dispatchers.IO).async {
            repo.getCurrentWeather(
                Location(alarm.longitude, alarm.latitude),
                sharedPref.getUnitSystem()
            )
        }

        val weatherDetailsResponse = deferredJob.await()

        val timeInMillis = alarm.time

        if (checkTime(alarm)) {
            if (repo.getAlarmType() == Constants.AlarmType.NOTIFICATION || alarm.type == Constants.AlarmType.NOTIFICATION) {
                openNotification(context, alarm, weatherDetailsResponse, context.getString(R.string.app_name))
            }

            if (alarm.type == Constants.AlarmType.ALARM) {
                /*if (Settings.canDrawOverlays(context)) {
                    withContext(Dispatchers.Main) {
                        val intent = Intent(context, DialogActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_INCLUDE_STOPPED_PACKAGES or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.putExtra(DESCRIPTION, description)
                        intent.putExtra(ICON, icon)
                        context.startActivity(intent)
                    }
                }*/
            }

            WorkRequestManager.createWorkRequest(
                alarm,
                context,
                (timeInMillis + 86400000),
            )
        } else {
            repo.deleteAlarm(alarm)
            removeWork("${alarm.id}", context)
        }

        return Result.success()
    }

    private fun checkTime(alarm: Alarm): Boolean {
        val currentTimeInMillis = Calendar.getInstance().timeInMillis
        return currentTimeInMillis >= alarm.time
    }
}
