package com.example.weatherpulse

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weatherpulse.model.WeatherDetailsResponse

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(myLocation: MutableState<Location?>, weatherResponse: WeatherDetailsResponse?) {

    val navController = rememberNavController()
    Scaffold (
        bottomBar = { ButtonBar(navController = navController) }
    ) {
        ButtonNavGraph(navController = navController, myLocation, weatherResponse)
    }
}

@Composable
fun ButtonBar(navController: NavHostController){
    val screens = listOf(
        ButtonBarScreen.Home,
        ButtonBarScreen.Favourite,
        ButtonBarScreen.Alarm,
        ButtonBarScreen.Setting

    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomNavigation {
        screens.forEach { screen ->
            if (currentDestination != null) {
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    }

}

@Composable
fun RowScope.AddItem(
    screen: ButtonBarScreen,
    currentDestination: NavDestination,
    navController: NavHostController
) {
    BottomNavigationItem(
        label = {
            Text(text = screen.title)
        },
        icon = {
            Icon(imageVector = screen.icon, contentDescription = "Navigation Icon")
        },
        selected = currentDestination.hierarchy.any {
            it.route == screen.route
        },
        modifier = Modifier.background(color = Color(0xFFF2DF98)),
        selectedContentColor = Color.White,
        unselectedContentColor = Color.Gray,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }

    )
}