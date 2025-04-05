package com.example.weatherpulse.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherpulse.ButtonBarScreen
import com.example.weatherpulse.model.Alarm
import com.example.weatherpulse.model.FavouritePlacesPojo
import com.example.weatherpulse.model.LocationKeyConverter

@Database(entities = [FavouritePlacesPojo::class, Alarm::class], version = 1)
@TypeConverters(LocationKeyConverter::class)
abstract class WeatherDataBase: RoomDatabase() {

    abstract fun getDao(): WeatherDao

    companion object {

        private var instanceOfDb: WeatherDataBase? = null

        fun getInstance(context: Context): WeatherDataBase {

            return instanceOfDb ?: synchronized(this){

                val temp: WeatherDataBase = Room.databaseBuilder(context, WeatherDataBase::class.java, "weatherDb").build()

                instanceOfDb = temp

                temp
            }
        }
    }

}