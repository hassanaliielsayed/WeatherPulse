package com.example.weatherpulse.home.view

import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherpulse.home.viewmodel.HomeViewModel
import com.example.weatherpulse.util.Result

@Composable
fun HomeScreen(viewModel: HomeViewModel, location: MutableState<Location?>) {

    LaunchedEffect (location.value) {
        Log.i("asd --> ", "HomeScreen: launced effect")
        location.value?.let { viewModel.getCurrentWeather(it)}
    }

    val weatherState by viewModel.mutableCurrentWeather.collectAsStateWithLifecycle()

    when (weatherState) {
        is Result.Error -> {
            ErrorScreen("eeeeee")
        }
        Result.Loading -> {
            LoadingScreen()
        }
        is Result.Success -> {
            val currentState = (weatherState as Result.Success).data

            Box (
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center

            ){
                Text(
                    currentState.current.temp.toString(),
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black

                )
            }
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