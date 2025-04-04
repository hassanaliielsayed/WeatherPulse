package com.example.weatherpulse.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherpulse.util.Constants.AlarmType

@Entity(tableName = "alert_table")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val time: Long,
    val longitude: Double,
    val latitude: Double,
    val city: String,
    val type: AlarmType
)
