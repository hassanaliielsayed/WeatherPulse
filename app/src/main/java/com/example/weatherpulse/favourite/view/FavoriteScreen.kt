package com.example.weatherpulse.favourite.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun FavoriteScreen(){

    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center

    ){
        Text(
            "Favorite Screen",
            fontSize = MaterialTheme.typography.labelSmall.fontSize,
            fontWeight = FontWeight.Bold,
            color = Color.Black

        )
    }

}