package com.example.weatherpulse

import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherpulse.screens.AlarmScreen
import com.example.weatherpulse.favourite.view.FavoriteScreen
import com.example.weatherpulse.home.view.HomeScreen
import com.example.weatherpulse.home.viewmodel.HomeViewModel
import com.example.weatherpulse.remote.WeatherClient
import com.example.weatherpulse.remote.WeatherRemoteDataSource
import com.example.weatherpulse.repo.Repo
import com.example.weatherpulse.screens.SettingScreen

@Composable
fun ButtonNavGraph(navController: NavHostController, myLocation: MutableState<Location?>){
    NavHost(
        navController = navController,
        startDestination = ButtonBarScreen.Home.route
    ) {
        composable(route = ButtonBarScreen.Home.route){
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.HomeFactory(
                    Repo.getInstance(WeatherRemoteDataSource(WeatherClient.weatherService)))
            )
            HomeScreen(homeViewModel, myLocation)
        }
        composable(route = ButtonBarScreen.Favourite.route){
            FavoriteScreen()
        }
        composable(route = ButtonBarScreen.Alarm.route){
            AlarmScreen()
        }
        composable(route = ButtonBarScreen.Setting.route){
            SettingScreen()
        }
    }
}