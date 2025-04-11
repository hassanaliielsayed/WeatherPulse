package com.example.weatherpulse.alarm.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherpulse.alarm.viewmodel.AlarmViewModel
import com.example.weatherpulse.model.Alarm
import com.example.weatherpulse.util.Constants.AlarmType
import com.example.weatherpulse.util.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
    viewModel: AlarmViewModel = koinViewModel<AlarmViewModel>(),
    onPickTime: () -> Unit,
) {

    val snackBarHostState = remember { SnackbarHostState() }
    val state by viewModel.mutableAlarms.collectAsStateWithLifecycle()
    var deletedAlarmForUndo by remember { mutableStateOf<Alarm?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getAllAlarms()
    }

    LaunchedEffect(deletedAlarmForUndo) {
        deletedAlarmForUndo?.let {
            val result = snackBarHostState.showSnackbar(
                message = "${it.id} removed",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.insertAlarm(it)
            }
            deletedAlarmForUndo = null
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
                    onClick = onPickTime,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Place, contentDescription = "Pick location to add")
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
                (state as Result.Success<List<Alarm>>).data
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
                    is Result.Error -> Text("Error loading favorites")
                    is Result.Success<*> -> {
                        LazyColumn {
                            items(
                                count = favorites.size,
                                key = {
                                    favorites[it].id ?: it.toLong()
                                }
                            ) { index ->
                                val favAlarm = favorites[index]
                                val dismissState = rememberSwipeToDismissBoxState(
                                    initialValue = SwipeToDismissBoxValue.Settled
                                )
                                val currentAlarm by rememberUpdatedState(favAlarm)

                                LaunchedEffect(dismissState.currentValue) {
                                    if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                                        viewModel.deleteAlarm(currentAlarm)
                                        deletedAlarmForUndo = currentAlarm
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
                                            alarm = favAlarm,
                                            onDelete = {
                                                deletedAlarmForUndo = favAlarm
                                                viewModel.deleteAlarm(favAlarm)
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
    alarm: Alarm,
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alarm.city,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatTime(alarm.time),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = alarm.type.name.capitalize(Locale.getDefault()),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (alarm.type == AlarmType.ALARM) Color.Red else Color(0xFF1976D2)
                )
            }
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

fun formatTime(millis: Long): String {
    val formatter = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
    return formatter.format(java.util.Date(millis))
}