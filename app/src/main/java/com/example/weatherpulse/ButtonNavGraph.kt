package com.example.weatherpulse

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.weatherpulse.favourite.view.FavoriteScreen
import com.example.weatherpulse.favourite.viewmodel.FavViewModel
import com.example.weatherpulse.home.view.HomeScreen
import com.example.weatherpulse.home.viewmodel.HomeViewModel
import com.example.weatherpulse.local.db.WeatherDataBase
import com.example.weatherpulse.local.WeatherLocalDataSource
import com.example.weatherpulse.local.sharedpref.SharedPref
import com.example.weatherpulse.util.MapScreen
import com.example.weatherpulse.remote.WeatherClient
import com.example.weatherpulse.remote.WeatherRemoteDataSource
import com.example.weatherpulse.repo.Repo
import com.example.weatherpulse.alarm.view.AlarmScreen
import com.example.weatherpulse.alarm.view.AddAlarmScreen
import com.example.weatherpulse.alarm.viewmodel.AlarmViewModel
import com.example.weatherpulse.model.WeatherDetailsResponse
import com.example.weatherpulse.setting.view.SettingScreen
import com.example.weatherpulse.setting.viewmodel.SettingViewModel
import com.example.weatherpulse.util.PreviewScreen
import com.example.weatherpulse.util.SettingHelpers

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ButtonNavGraph(
    navController: NavHostController,
    myLocation: MutableState<Location?>,
    weatherResponse: WeatherDetailsResponse?
) {
    val context = LocalContext.current

    val repo = Repo.getInstance(
        WeatherRemoteDataSource(WeatherClient.weatherService),
        WeatherLocalDataSource(
            WeatherDataBase.getInstance(context).getDao(),
            SharedPref.getInstance(context)
        )
    )

    NavHost(
        navController = navController,
        startDestination = ButtonBarScreen.Home.route
    ) {
        // Home
        composable(route = ButtonBarScreen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.HomeFactory(repo)
            )
            HomeScreen(homeViewModel, myLocation, weatherResponse)
        }

        // Favorites
        composable(route = ButtonBarScreen.Favourite.route) {
            val favViewModel: FavViewModel = viewModel(
                factory = FavViewModel.FavFactory(repo)
            )
            FavoriteScreen(
                viewModel = favViewModel,
                onPickLocation = {
                    navController.navigate("MapScreen?source=favorites")
                },
                onCityClick = { locationKey, cityName ->
                    navController.currentBackStackEntry?.savedStateHandle?.apply {
                        set("lat", locationKey.lat)
                        set("lon", locationKey.long)
                        set("city", cityName)
                    }
                    navController.navigate(ButtonBarScreen.Preview.route)
                }
            )
        }

        // Alarms
        composable(route = ButtonBarScreen.Alarm.route) {
            val alarmViewModel: AlarmViewModel = viewModel(
                factory = AlarmViewModel.AlarmFactory(repo)
            )
            AlarmScreen(
                viewModel = alarmViewModel,
                onPickTime = {
                    navController.navigate("MapScreen?source=alarm")
                }
            )
        }

        // Settings
        composable(route = ButtonBarScreen.Setting.route) {
            val settingViewModel: SettingViewModel = viewModel(
                factory = SettingViewModel.SettingFactory(repo)
            )
            SettingScreen(
                viewModel = settingViewModel,
                onRequestMapPicker = {
                    navController.navigate("MapScreen?source=settings")
                },
                onRequestLocationPermission = {
                    SettingHelpers.checkAndRequestLocationPermission(context)
                },
                onRequestLocationEnable = {
                    SettingHelpers.promptEnableLocation(context)
                },
                onRecreateActivity = {
                    SettingHelpers.recreateActivity(context)
                }
            )
        }

        // Map screen
        composable(
            route = "MapScreen?source={source}",
            arguments = listOf(
                navArgument("source") { defaultValue = "favorites" }
            )
        ) { backStackEntry ->
            val source = backStackEntry.arguments?.getString("source") ?: "favorites"

            MapScreen(
                onLocationSelected = { lat, lon, city ->
                    when (source) {
                        "settings" -> {
                            val sharedPref = SharedPref.getInstance(context)
                            sharedPref.setLat(lat)
                            sharedPref.setLon(lon)
                            sharedPref.setCity(city)

                            navController.navigate(ButtonBarScreen.Home.route) {
                                popUpTo(ButtonBarScreen.Setting.route) { inclusive = true }
                            }
                        }
                        "alarm" -> {
                            navController.navigate("add_alarm_screen?lat=$lat&lon=$lon&city=$city")
                        }
                        else -> {
                            navController.currentBackStackEntry?.savedStateHandle?.apply {
                                set("lat", lat)
                                set("lon", lon)
                                set("city", city)
                            }
                            navController.navigate(ButtonBarScreen.Preview.route)
                        }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Add Alarm Screen
        composable(
            route = "add_alarm_screen?lat={lat}&lon={lon}&city={city}",
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lon") { type = NavType.FloatType },
                navArgument("city") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
            val lon = backStackEntry.arguments?.getFloat("lon")?.toDouble() ?: 0.0
            val city = backStackEntry.arguments?.getString("city") ?: ""
            val alarmViewModel: AlarmViewModel = viewModel(
                factory = AlarmViewModel.AlarmFactory(repo)
            )

            AddAlarmScreen(
                navController = navController,
                lat = lat,
                lon = lon,
                city = city,
                viewModel = alarmViewModel
            )
        }

        // Preview
        composable(route = ButtonBarScreen.Preview.route) {
            val lat = navController.previousBackStackEntry?.savedStateHandle?.get<Double>("lat")
            val lon = navController.previousBackStackEntry?.savedStateHandle?.get<Double>("lon")
            val city = navController.previousBackStackEntry?.savedStateHandle?.get<String>("city")

            if (lat != null && lon != null && city != null) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.HomeFactory(repo)
                )
                val favViewModel: FavViewModel = viewModel(
                    factory = FavViewModel.FavFactory(repo)
                )
                PreviewScreen(
                    lat = lat,
                    lon = lon,
                    cityName = city,
                    homeViewModel = homeViewModel,
                    favViewModel = favViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}