package com.example.weatherpulse.util

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherpulse.favourite.viewmodel.FavViewModel
import com.example.weatherpulse.home.view.HomeScreen
import com.example.weatherpulse.home.viewmodel.HomeViewModel
import com.example.weatherpulse.model.FavouritePlacesPojo
import com.example.weatherpulse.model.LocationKey
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PreviewScreen(
    lat: Double,
    lon: Double,
    cityName: String,
    homeViewModel: HomeViewModel,
    favViewModel: FavViewModel,
    onBack: () -> Unit
) {
    val locationState = remember {
        mutableStateOf<Location?>(Location("").apply {
            latitude = lat
            longitude = lon
        })
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var isAdded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preview Location") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val favorite = FavouritePlacesPojo(
                            locationKey = LocationKey(lat, lon),
                            countryName = cityName,
                            temp = "N/A"
                        )
                        favViewModel.insertLocation(favorite)
                        isAdded = true
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Added to favorites")
                        }
                    }) {
                        Icon(
                            imageVector = if (isAdded) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Add to favorites"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            HomeScreen(viewModel = homeViewModel, location = locationState)
        }
    }
}