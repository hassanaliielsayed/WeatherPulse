package com.example.weatherpulse.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherpulse.model.Alarm
import com.example.weatherpulse.model.FavouritePlacesPojo
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM fav_table")
    fun getAllLocations(): Flow<List<FavouritePlacesPojo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocation(location: FavouritePlacesPojo): Long

    @Delete
    suspend fun deleteLocation(location: FavouritePlacesPojo): Int

    @Query("SELECT * FROM alert_table")
    fun getAllAlarms(): Flow<List<Alarm>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlarm(alarm: Alarm): Long

    @Delete
    suspend fun deleteAlarm(alarm: Alarm): Int
}