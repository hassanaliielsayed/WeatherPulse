package com.example.weatherpulse.alarm.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherpulse.alarm.viewmodel.AlarmViewModel
import com.example.weatherpulse.model.Alarm
import com.example.weatherpulse.util.Constants.AlarmType
import com.example.weatherpulse.util.WorkRequestManager.createWorkRequest
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddAlarmScreen(
    navController: NavController,
    lat: Double,
    lon: Double,
    city: String,
    viewModel: AlarmViewModel
) {
    val context = LocalContext.current
    var time by remember { mutableStateOf("Select Time") }
    var type by remember { mutableStateOf(AlarmType.ALARM) } // alarm / notification

    var calendar: Calendar? by remember { mutableStateOf(null) }

    var showPastTimeError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Location: $city", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            val currentDate = Calendar.getInstance()
            val date = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                context,
                { _, year, monthOfYear, dayOfMonth ->
                    date.set(year, monthOfYear, dayOfMonth)
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            date.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            date.set(Calendar.MINUTE, minute)

                            if (date.timeInMillis <= System.currentTimeMillis()) {
                                showPastTimeError = true
                                time = "Select Time"
                                calendar = null
                            } else {
                                showPastTimeError = false
                                calendar = date
                                time = String.format("%02d:%02d", hourOfDay, minute)
                            }

//                            Log.d("asd --> ", "AddAlarmScreen: calendar = ${(calendar as Calendar).time}")
//                            Log.d("asd --> ", "AddAlarmScreen: calendar = ${(calendar as Calendar).timeInMillis}")
//                            Log.d("asd --> ", "AddAlarmScreen: longitude = $lon")
//                            Log.d("asd --> ", "AddAlarmScreen: latitude = $lat")
//                            Log.d("asd --> ", "AddAlarmScreen: city = $city")
                        },
                        currentDate[Calendar.HOUR_OF_DAY],
                        currentDate[Calendar.MINUTE],
                        false,
                    ).show()
                },
                currentDate[Calendar.YEAR],
                currentDate[Calendar.MONTH],
                currentDate[Calendar.DATE],
            )
            datePickerDialog.datePicker.minDate = currentDate.timeInMillis
            datePickerDialog.show()
        }) {
            Text(text = time)
        }

        if (showPastTimeError) {
            Text(
                text = "Please select a future time",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }



        Spacer(modifier = Modifier.height(24.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Select Type:", style = MaterialTheme.typography.bodyMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                RadioButton(
                    selected = type == AlarmType.ALARM,
                    onClick = { type = AlarmType.ALARM }
                )
                Text("Alarm")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = type == AlarmType.NOTIFICATION,
                    onClick = { type = AlarmType.NOTIFICATION }
                )
                Text("Notification")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {

            if (calendar == null) {
                Toast.makeText(context, "Empty alarm message", Toast.LENGTH_LONG).show()
            } else {

                val alarm = Alarm(
                    time = (calendar as Calendar).timeInMillis,
                    longitude = lon,
                    latitude = lat,
                    city = city,
                    type = type
                )

                viewModel.insertAlarm(alarm = alarm)

                createWorkRequest(alarm, context, alarm.time)

                navController.popBackStack()
                navController.popBackStack()
            }
        }) {
            Text("Save Alarm")
        }
    }
}