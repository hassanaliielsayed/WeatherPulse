package com.example.weatherpulse.home.view

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherpulse.home.viewmodel.HomeViewModel
import com.example.weatherpulse.model.Daily
import com.example.weatherpulse.model.WeatherDetailsResponse
import com.example.weatherpulse.util.Constants.ZERO
import com.example.weatherpulse.util.Constants.getWeatherIconRes
import com.example.weatherpulse.util.Result
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    location: MutableState<Location?>,
    weatherResponse: WeatherDetailsResponse? = null,
) {
    val context = LocalContext.current
    val addressState = remember { mutableStateOf("") }
    val weatherState by viewModel.mutableCurrentWeather.collectAsStateWithLifecycle()
    var unitSystem by remember { mutableStateOf("metric") }

    LaunchedEffect(location.value) {
        weatherResponse?.let {
            viewModel.setNotificationWeatherData(weatherResponse)
        } ?: run {
            location.value?.let {
                val unit = viewModel.getSavedUnitSystem()
                unitSystem = unit
                viewModel.getCurrentWeather(it, unit)
            }
        }
    }

    when (weatherState) {
        is Result.Error -> ErrorScreen("Error")
        Result.Loading -> LoadingScreen()
        is Result.Success -> {
            val currentState = (weatherState as Result.Success).data

            LaunchedEffect(Unit) {
                addressState.value = location.value?.let { getAddressDetails(it, context) }
                    ?: viewModel.getSavedCity() ?: "Unknown"
            }

            val sunrise = currentState.current.sunrise.toLong().toFormatted()
            val sunset = currentState.current.sunset.toLong().toFormatted()

            val tempUnit = when (unitSystem) {
                "imperial" -> "°F"
                "standard" -> "K"
                else -> "°C"
            }

            val windUnit = if (unitSystem == "imperial") "mph" else "m/s"

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFB3E5FC))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SearchBar(addressState.value)
                Spacer(modifier = Modifier.height(16.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "${currentState.current.temp.toInt()}$tempUnit",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(currentState.current.weather[0].description, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("🌇 Sunset $sunset         🌅 Sunrise $sunrise", fontSize = 16.sp)
                    Text(
                        "Feels like ${currentState.current.feels_like.toInt()}$tempUnit",
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("   🧭 Pressure", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("      💨 Wind", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("      💧 Humidity", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("  ☀️ UV", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("☁️ Clouds", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text("${currentState.current.pressure} hPa", fontSize = 14.sp)
                        Text("${currentState.current.wind_speed} $windUnit", fontSize = 14.sp)
                        Text("      ${currentState.current.humidity}%", fontSize = 14.sp)
                        Text("   ${currentState.current.uvi}", fontSize = 14.sp)
                        Text("  ${currentState.current.clouds}%", fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Text("Hourly forecast", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow {
                        items(currentState.hourly.size - 24) { index ->
                            val hour = currentState.hourly[index]
                            WeatherCard(
                                time = hour.dt.toLong().toFormatted(),
                                icon = painterResource(getWeatherIconRes(hour.weather[ZERO].icon)),
                                temp = "${hour.temp.toInt()} $tempUnit"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    SevenDayForecast(dailyForecast = currentState.daily, tempUnit = tempUnit)
                }
            }
        }
    }
}

fun getAddressDetails(location: Location, context: Context): String {


    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
    if (!addresses.isNullOrEmpty()) {
        val address = addresses[0]
        return "${address.locality}, ${address.adminArea}, ${address.countryName}"
    } else {
        return "Unknown Address"
    }


}

fun getAddressDetails(lat: Double, long: Double, context: Context): String {

    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses = geocoder.getFromLocation(lat, long, 1)
    if (!addresses.isNullOrEmpty()) {
        val address = addresses[0]
        return "${address.locality}, ${address.adminArea}, ${address.countryName}"
    } else {
        return "Unknown Address"
    }
}


fun Long.toFormatted(pattern: String = "hh:mm a"): String {

    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(Date(this * 1000))
}

@Composable
fun WeatherCard(time: String, temp: String, icon: Painter) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(90.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White, // Background color

        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = temp, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Icon(icon, null, tint = Color.Unspecified)
            Text(text = time, fontSize = 14.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun SearchBar(location: String) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "\uD83D\uDCCD $location",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }


}


@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(errorMessage: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            fontSize = 18.sp
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SevenDayForecast(dailyForecast: List<Daily>, tempUnit: String) {

    val forecastToShow = if (dailyForecast.isNotEmpty() && dailyForecast[0].dt.toLong().isToday()) {
        dailyForecast
    } else {
        dailyForecast
    }

    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = "${forecastToShow.size}-day forecast",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.height(600.dp),
            userScrollEnabled = false
        ) {
            items(forecastToShow) { day ->
                val dayName =
                    if (day.dt.toLong().isToday()) "Today" else day.dt.toLong().toDayOfWeek()
                val dateStr = if (day.dt.toLong().isToday()) dayName else "$dayName, ${
                    day.dt.toLong().toFormatted("MMM dd")
                }"

                ForecastItem(
                    day = dateStr,
                    temp = "${day.temp.day.toInt()}$tempUnit/${day.temp.night.toInt()}$tempUnit",
                    icon = painterResource(getWeatherIconRes(day.weather[0].icon)),
                    isToday = day.dt.toLong().isToday()
                )
            }
        }
    }
}

@Composable
fun ForecastItem(day: String, temp: String, icon: Painter, isToday: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isToday) Color(0xFFE3F2FD) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = day,
                fontSize = 16.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                painter = icon,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = temp,
                fontSize = 16.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.width(80.dp),
                textAlign = TextAlign.End
            )
        }
    }
}

// Improved date extension functions
fun Long.isToday(): Boolean {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val todayStart = calendar.timeInMillis / 1000
    val todayEnd = todayStart + 86400 // 24 hours in seconds
    return this >= todayStart && this < todayEnd
}


fun Long.toDayOfWeek(): String {
    val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
    return sdf.format(Date(this * 1000))
}