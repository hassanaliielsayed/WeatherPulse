package com.example.weatherpulse.alarm.view

import android.annotation.SuppressLint
import android.app.Activity
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherpulse.R
import com.example.weatherpulse.ui.theme.WeatherPulseTheme

class AlarmDialogActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        val title = intent.getStringExtra("ALARM_TITLE") ?: "Weather Alarm"
        val desc = intent.getStringExtra("ALARM_DESC") ?: "Weather condition triggered"
        val city = intent.getStringExtra("ALARM_CITY") ?: "Unknown Location"

        setContent {
            WeatherPulseTheme {
                AlarmDialogContent(title = title, description = desc, city = city, onDismiss = {
                    finish()
                })
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // prevent back press
    }
}

@Composable
fun AlarmDialogContent(title: String, description: String, city: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val soundUri = remember {
        Uri.parse("android.resource://${context.packageName}/raw/alarm_sound")
    }
    val mediaPlayer = remember {
        MediaPlayer.create(context, soundUri).apply { isLooping = true }
    }

    DisposableEffect(Unit) {
        mediaPlayer.start()

        val handler = Handler(Looper.getMainLooper())
        val autoDismiss = Runnable {
            if (mediaPlayer.isPlaying) mediaPlayer.stop()
            mediaPlayer.release()
            onDismiss()
        }
        handler.postDelayed(autoDismiss, 20000)

        onDispose {
            if (mediaPlayer.isPlaying) mediaPlayer.stop()
            mediaPlayer.release()
            handler.removeCallbacks(autoDismiss)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB3E5FC)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = description, fontSize = 18.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "📍 $city", fontSize = 16.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.dismiss),
                fontSize = 18.sp,
                color = Color.Red,
                modifier = Modifier
                    .clickable {
                        if (mediaPlayer.isPlaying) mediaPlayer.stop()
                        mediaPlayer.release()
                        onDismiss()
                    }
                    .padding(16.dp)
            )
        }
    }
}