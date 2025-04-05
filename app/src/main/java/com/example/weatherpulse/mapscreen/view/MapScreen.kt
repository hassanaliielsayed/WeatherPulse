package com.example.weatherpulse.mapscreen.view

import android.Manifest
import android.location.Geocoder
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.weatherpulse.R
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onLocationSelected: (lat: Double, lon: Double, city: String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var marker by remember { mutableStateOf<Marker?>(null) }
    var selectedCity by remember { mutableStateOf("No location selected") }
    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.pick_location)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val map = MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(5.0)
                        controller.setCenter(GeoPoint(30.0444, 31.2357))
                    }

                    if (ContextCompat.checkSelfPermission(
                            ctx,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        val locationOverlay = MyLocationNewOverlay(map).apply {
                            enableMyLocation()
                            runOnFirstFix {
                                val loc = myLocation
                                loc?.let {
                                    android.os.Handler(android.os.Looper.getMainLooper()).post {
                                        map.controller.animateTo(GeoPoint(it.latitude, it.longitude))
                                    }
                                }
                            }
                        }
                        map.overlays.add(locationOverlay)
                    }

                    val receiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                            p?.let { point ->
                                selectedPoint = point

                                marker?.let { map.overlays.remove(it) }

                                marker = Marker(map).apply {
                                    position = point
                                    title = context.getString(R.string.selected_location)
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                }
                                map.overlays.add(marker)
                                map.invalidate()

                                val geocoder = Geocoder(ctx, Locale.getDefault())
                                val address = try {
                                    geocoder.getFromLocation(point.latitude, point.longitude, 1)?.firstOrNull()
                                } catch (e: Exception) { null }

                                selectedCity = listOfNotNull(
                                    address?.locality,
                                    address?.adminArea,
                                    address?.countryName
                                ).filter { it.isNotBlank() && it.lowercase() != "null" }
                                    .joinToString(", ")
                                    .ifBlank { "Unknown" }
                            }
                            return true
                        }

                        override fun longPressHelper(p: GeoPoint?) = false
                    }

                    val mapEventsOverlay = MapEventsOverlay(receiver)
                    map.overlays.add(mapEventsOverlay)

                    mapView = map
                    map
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text(stringResource(R.string.search_city), color = Color.Black) },
                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        performSearch(
                            context = context,
                            query = searchQuery,
                            onResult = { point, name ->
                                selectedPoint = point
                                selectedCity = name

                                mapView?.controller?.animateTo(point)
                                mapView?.controller?.setZoom(10.0)

                                marker?.let { mapView?.overlays?.remove(it) }
                                val newMarker = Marker(mapView).apply {
                                    position = point
                                    title = name
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                }
                                mapView?.overlays?.add(newMarker)
                                mapView?.invalidate()
                                marker = newMarker
                            },
                            onNotFound = {
                                Toast.makeText(context,
                                    context.getString(R.string.city_not_found), Toast.LENGTH_SHORT).show()
                            }
                        )
                    })
                )
                IconButton(onClick = {
                    performSearch(
                        context = context,
                        query = searchQuery,
                        onResult = { point, name ->
                            selectedPoint = point
                            selectedCity = name

                            mapView?.controller?.animateTo(point)
                            mapView?.controller?.setZoom(10.0)

                            marker?.let { mapView?.overlays?.remove(it) }
                            val newMarker = Marker(mapView).apply {
                                position = point
                                title = name
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            mapView?.overlays?.add(newMarker)
                            mapView?.invalidate()
                            marker = newMarker
                        },
                        onNotFound = {
                            Toast.makeText(context, "City not found", Toast.LENGTH_SHORT).show()
                        }
                    )
                }) {
                    Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search), tint = Color.Black)
                }
            }

            Button(
                onClick = {
                    selectedPoint?.let { point ->
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val address = try {
                            geocoder.getFromLocation(point.latitude, point.longitude, 1)?.firstOrNull()
                        } catch (e: Exception) { null }

                        val city = listOfNotNull(
                            address?.locality,
                            address?.adminArea,
                            address?.countryName
                        ).filter { it.isNotBlank() && it.lowercase() != "null" }
                            .joinToString(", ")
                            .ifBlank { "Unknown" }

                        onLocationSelected(point.latitude, point.longitude, city)
                    }
                },
                enabled = selectedPoint != null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
                    .fillMaxWidth()
            ) {
                Text(stringResource(R.string.confirm_location))
            }

            if (selectedCity.isNotBlank() &&
                selectedCity != "No location selected" &&
                selectedCity.lowercase() != "null") {
                Text(
                    text = "City: $selectedCity",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

private fun performSearch(
    context: android.content.Context,
    query: String,
    onResult: (GeoPoint, String) -> Unit,
    onNotFound: () -> Unit
) {
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        val result = geocoder.getFromLocationName(query, 1)?.firstOrNull()
        if (result != null) {
            val point = GeoPoint(result.latitude, result.longitude)
            val name = listOfNotNull(
                result.locality,
                result.adminArea,
                result.countryName
            ).filter { it.isNotBlank() && it.lowercase() != "null" }
                .joinToString(", ")
                .ifBlank { "Selected" }
            onResult(point, name)
        } else {
            onNotFound()
        }
    } catch (e: Exception) {
        onNotFound()
    }
}