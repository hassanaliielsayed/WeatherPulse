package com.example.weatherpulse

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ButtonBarScreen (
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    object Home: ButtonBarScreen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    object Favourite: ButtonBarScreen(
        route = "favourite",
        title = "Favourite",
        icon = Icons.Default.Favorite
    )

    object Alarm: ButtonBarScreen(
        route = "alarm",
        title = "Alarm",
        icon = Icons.Default.Notifications
    )

    object Setting: ButtonBarScreen(
        route = "setting",
        title = "Setting",
        icon = Icons.Default.Settings
    )

    object MapScreen: ButtonBarScreen(
        route = "MapScreen",
        title = "MapScreen",
        icon = Icons.Default.ShoppingCart
    )

    object Preview: ButtonBarScreen(
        route = "Preview",
        title = "Preview",
        icon = Icons.Default.Home
    )
}