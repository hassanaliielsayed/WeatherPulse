package com.example.weatherpulse

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherpulse.favourite.view.FavoriteScreen
import com.example.weatherpulse.favourite.viewmodel.FavViewModel
import com.example.weatherpulse.home.view.HomeScreen
import com.example.weatherpulse.home.viewmodel.HomeViewModel
import com.example.weatherpulse.local.WeatherDataBase
import com.example.weatherpulse.local.WeatherLocalDataSource
import com.example.weatherpulse.mapscreen.view.MapScreen
import com.example.weatherpulse.remote.WeatherClient
import com.example.weatherpulse.remote.WeatherRemoteDataSource
import com.example.weatherpulse.repo.Repo
import com.example.weatherpulse.screens.AlarmScreen
import com.example.weatherpulse.screens.SettingScreen
import com.example.weatherpulse.util.PreviewScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ButtonNavGraph(
    navController: NavHostController,
    myLocation: MutableState<Location?>
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = ButtonBarScreen.Home.route
    ) {
        // Home
        composable(route = ButtonBarScreen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.HomeFactory(
                    Repo.getInstance(
                        WeatherRemoteDataSource(WeatherClient.weatherService),
                        WeatherLocalDataSource(WeatherDataBase.getInstance(context).getDao())
                    )
                )
            )
            HomeScreen(homeViewModel, myLocation)
        }

        // Favorites
        composable(route = ButtonBarScreen.Favourite.route) {
            val favViewModel: FavViewModel = viewModel(
                factory = FavViewModel.FavFactory(
                    Repo.getInstance(
                        WeatherRemoteDataSource(WeatherClient.weatherService),
                        WeatherLocalDataSource(WeatherDataBase.getInstance(context).getDao())
                    )
                )
            )
            FavoriteScreen(
                viewModel = favViewModel,
                onPickLocation = {
                    navController.navigate(ButtonBarScreen.MapScreen.route)
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
            AlarmScreen()
        }

        // Settings
        composable(route = ButtonBarScreen.Setting.route) {
            SettingScreen()
        }

        // Map screen (used temporarily, not part of bottom bar)
        composable(route = ButtonBarScreen.MapScreen.route) {
            MapScreen(
                onLocationSelected = { lat, lon, city ->
                    navController.currentBackStackEntry?.savedStateHandle?.apply {
                        set("lat", lat)
                        set("lon", lon)
                        set("city", city)
                    }
                    navController.navigate(ButtonBarScreen.Preview.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = ButtonBarScreen.Preview.route) {

            val lat = navController.previousBackStackEntry?.savedStateHandle?.get<Double>("lat")
            val lon = navController.previousBackStackEntry?.savedStateHandle?.get<Double>("lon")
            val city = navController.previousBackStackEntry?.savedStateHandle?.get<String>("city")

            if (lat != null && lon != null && city != null) {

                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.HomeFactory(
                        Repo.getInstance(
                            WeatherRemoteDataSource(WeatherClient.weatherService),
                            WeatherLocalDataSource(WeatherDataBase.getInstance(context).getDao())
                        )
                    )
                )

                val favViewModel: FavViewModel = viewModel(
                    factory = FavViewModel.FavFactory(
                        Repo.getInstance(
                            WeatherRemoteDataSource(WeatherClient.weatherService),
                            WeatherLocalDataSource(WeatherDataBase.getInstance(context).getDao())
                        )
                    )
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