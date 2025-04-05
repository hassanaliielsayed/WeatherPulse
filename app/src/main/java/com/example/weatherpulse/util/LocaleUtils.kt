package com.example.weatherpulse.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.Locale

object LocaleUtils {

    @SuppressLint("ObsoleteSdkInt")
    fun Context.setAppLocale(language: String): Context {
        val locale = when (language) {
            "ar" -> Locale("ar")
            "en" -> Locale.ENGLISH
            else -> Resources.getSystem().configuration.locales.get(0)
        }

        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createConfigurationContext(config)
        } else {
            resources.updateConfiguration(config, resources.displayMetrics)
            this
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    fun getSystemLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            Resources.getSystem().configuration.locale
        }
    }
}