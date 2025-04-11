package com.example.weatherpulse.favourite.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherpulse.R
import com.example.weatherpulse.favourite.viewmodel.FavViewModel
import com.example.weatherpulse.model.FavouritePlacesPojo
import com.example.weatherpulse.model.LocationKey
import com.example.weatherpulse.util.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    viewModel: FavViewModel = koinViewModel<FavViewModel>(),
    onPickLocation: () -> Unit,
    onCityClick: (locationKey: LocationKey, cityName: String) -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.getAllLocations()
    }

    val state by viewModel.mutableLocations.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    var deletedLocationForUndo by remember { mutableStateOf<FavouritePlacesPojo?>(null) }

    LaunchedEffect(deletedLocationForUndo) {
        deletedLocationForUndo?.let {
            val result = snackBarHostState.showSnackbar(
                message = "${it.locationKey} removed",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.insertLocation(it)
            }
            deletedLocationForUndo = null
        }
    }

    Scaffold(
        floatingActionButton = {
            Column(
                modifier = Modifier
                    .padding(bottom = 48.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(
                    modifier = Modifier.offset(y = 2.dp),
                    onClick = onPickLocation,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Place, contentDescription = stringResource(R.string.pick_location_to_add))
                }

                SnackbarHost(
                    hostState = snackBarHostState,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFB3E5FC))
        ) {
            val favorites = if (state is Result.Success<*>) {
                (state as Result.Success<List<FavouritePlacesPojo>>).data
            } else emptyList()

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        if (favorites.isEmpty())
                            Color(0xFFB3E5FC).copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.background.copy(alpha = 0.3f)
                    )
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                when (state) {
                    is Result.Loading -> CircularProgressIndicator()
                    is Result.Error -> Text(stringResource(R.string.error_loading_favorites))
                    is Result.Success<*> -> {
                        LazyColumn {
                            items(
                                count = favorites.size,
                                key = {
                                    // convert to String
                                    val loc = favorites[it].locationKey
                                    "${loc.lat},${loc.long}"
                                }
                            ) { index ->
                                val favLocation = favorites[index]
                                val dismissState = rememberSwipeToDismissBoxState(
                                    initialValue = SwipeToDismissBoxValue.Settled
                                )
                                val currentLocation by rememberUpdatedState(favLocation)

                                LaunchedEffect(dismissState.currentValue) {
                                    if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                                        viewModel.deleteLocation(currentLocation)
                                        deletedLocationForUndo = currentLocation
                                        dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                                    }
                                }

                                SwipeToDismissBox(
                                    state = dismissState,
                                    enableDismissFromStartToEnd = true,
                                    enableDismissFromEndToStart = true,
                                    backgroundContent = {},
                                    content = {
                                        FancyCityCard(
                                            location = favLocation,
                                            onClick = {
                                                onCityClick(
                                                    favLocation.locationKey,
                                                    favLocation.countryName
                                                )
                                            },
                                            onDelete = {
                                                deletedLocationForUndo = favLocation
                                                viewModel.deleteLocation(favLocation)
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FancyCityCard(
    location: FavouritePlacesPojo,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var animateDelete by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animateDelete) 1.3f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "DeleteIconScale"
    )
    val rotation by animateFloatAsState(
        targetValue = if (animateDelete) 15f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "DeleteIconRotate"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = location.countryName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .weight(1f)
            )
            IconButton(
                onClick = {
                    animateDelete = true
                    onDelete()
                    scope.launch {
                        delay(300)
                        animateDelete = false
                    }
                },
                modifier = Modifier
                    .scale(scale)
                    .graphicsLayer { rotationZ = rotation }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove city",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}