package com.example.weatherpulse

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.weatherpulse.ui.theme.WeatherPulseTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.delay
import java.util.Locale

const val REQUEST_LOCATION_CODE = 2000
class MainActivity : ComponentActivity() {

    val myLocation = mutableStateOf<Location?>(null)

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        setContent {



            val response = remember { mutableStateOf(true) }
            installSplashScreen().setKeepOnScreenCondition {
                response.value
            }
            LaunchedEffect(true) {
                delay(2000)
                response.value = false
            }
            WeatherPulseTheme {
                MainScreen(myLocation)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(checkPermission()){
            if(isLocationEnable()){
                getFreshLocation()
            } else {
                enableLocationServices()
            }
        } else {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_CODE

            )
        }
    }

    private fun checkPermission(): Boolean{

        var result = false
        if ((ContextCompat.checkSelfPermission (this ,
                ACCESS_COARSE_LOCATION ) == PackageManager. PERMISSION_GRANTED )
            ||
            (ContextCompat.checkSelfPermission(this ,
                ACCESS_FINE_LOCATION
            ) == PackageManager. PERMISSION_GRANTED ))
            result = true
        return result
    }

    private fun isLocationEnable(): Boolean {

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {
        // getting entry point
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // getting location
        fusedLocationProviderClient.requestLocationUpdates(
            getLocationRequest(),
            getLocationCallback(),
            Looper.myLooper()
        )
    }

    private fun getLocationRequest() = LocationRequest.Builder(0).apply {
        setPriority(Priority.PRIORITY_HIGH_ACCURACY)
    }.build()

    private fun getLocationCallback(): LocationCallback {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                for (location in locationResult.locations) {
                    if (location != null) {
                        Log.d("asd --> ", "longitude = ${location.longitude}")
                        Log.d("asd --> ", "latitude = ${location.latitude}")
                        myLocation.value = location
                        stopLocationUpdates()
                    }
                }

                //getAddressDetails(location.lastLocation?: Location(LocationManager.GPS_PROVIDER))

            }
        }

        return locationCallback
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("```TAG```", "onRequestPermissionsResult: konck")
        if (requestCode == REQUEST_LOCATION_CODE){
            if (grantResults.get(0) == PackageManager.PERMISSION_GRANTED || grantResults.get(1) == PackageManager.PERMISSION_GRANTED){
                if (isLocationEnable()){
                    getFreshLocation()
                    Log.d("```TAG```", "onRequestPermissionsResult: true")
                } else {
                    enableLocationServices()
                }
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_LOCATION_CODE

                )
            }

        }
    }

    private fun enableLocationServices() {

        Toast.makeText(this, "Please Turn on Location", Toast.LENGTH_LONG).show()
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    private fun getAddressDetails(location: Location): String {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                "${address.getAddressLine(0)}, ${address.locality}, ${address.adminArea}, ${address.countryName}"
            } else {
                "Unknown Address"
            }
        } catch (e: Exception) {
            "Unable to get address"
        }
    }
}

