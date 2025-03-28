package com.example.weatherpulse.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//@Database()
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