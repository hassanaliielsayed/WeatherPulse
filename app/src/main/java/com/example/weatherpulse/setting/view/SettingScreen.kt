package com.example.weatherpulse.setting.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.weatherpulse.setting.viewmodel.SettingViewModel

@Composable
fun SettingScreen(
    viewModel: SettingViewModel,
    onRequestMapPicker: () -> Unit,
    onRequestLocationPermission: () -> Unit,
    onRequestLocationEnable: () -> Unit,
    onRecreateActivity: () -> Unit
) {
    val language = viewModel.language.collectAsState().value
    val unitSystem = viewModel.unitSystem.collectAsState().value
    val locationSource = viewModel.locationSource.collectAsState().value

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB3E5FC))
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text("Language")
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = language == "device",
                        onClick = {
                            viewModel.setLanguage("device")
                            onRecreateActivity()
                        }
                    )
                    Text("Device Language")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = language == "en",
                        onClick = {
                            viewModel.setLanguage("en")
                            onRecreateActivity()
                        }
                    )
                    Text("English")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = language == "ar",
                        onClick = {
                            viewModel.setLanguage("ar")
                            onRecreateActivity()
                        }
                    )
                    Text("Arabic")
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Measurement System")
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = unitSystem == "metric",
                        onClick = { viewModel.setUnitSystem("metric") }
                    )
                    Column {
                        Text("Metric")
                        Text("Temp: °C, Wind: m/s", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = unitSystem == "imperial",
                        onClick = { viewModel.setUnitSystem("imperial") }
                    )
                    Column {
                        Text("Imperial")
                        Text("Temp: °F, Wind: mph", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = unitSystem == "standard",
                        onClick = { viewModel.setUnitSystem("standard") }
                    )
                    Column {
                        Text("Standard")
                        Text("Temp: K, Wind: m/s", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Location Source")
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = locationSource == "gps",
                    onClick = {
                        viewModel.setLocationSource("gps")
                        onRequestLocationPermission()
                        onRequestLocationEnable()
                    }
                )
                Text("GPS")

                RadioButton(
                    selected = locationSource == "map",
                    onClick = {
                        viewModel.setLocationSource("map")
                        onRequestMapPicker()
                    }
                )
                Text("Map Picker")
            }
        }
    }
}