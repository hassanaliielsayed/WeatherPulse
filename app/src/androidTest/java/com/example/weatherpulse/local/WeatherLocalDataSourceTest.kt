package com.example.weatherpulse.local

import com.example.weatherpulse.local.sharedpref.SharedPrefInterface
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherpulse.local.db.WeatherDao
import com.example.weatherpulse.local.db.WeatherDataBase
import com.example.weatherpulse.local.sharedpref.SharedPref
import com.example.weatherpulse.model.FavouritePlacesPojo
import com.example.weatherpulse.model.LocationKey
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherLocalDataSourceTest {

    private lateinit var dao: WeatherDao
    private lateinit var database: WeatherDataBase
    private lateinit var weatherLocalDataSource: WeatherLocalDataSourceInterface
    private lateinit var sharedPref: SharedPrefInterface


    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDataBase::class.java
        ).build()

        dao = database.getDao()

        weatherLocalDataSource = WeatherLocalDataSource(dao, SharedPref.getInstance(ApplicationProvider.getApplicationContext()))

    }

    @After
    fun dearDown() = database.close()

    @Test
    fun getAllLocations_emptyInitially() = runTest {

        // When
        val allLocations = weatherLocalDataSource.getAllLocations().first()

        // Then
        assertTrue(allLocations.isEmpty())
    }

    @Test
    fun getAllLocations_insertLocation() = runTest {

        val locationKey = LocationKey(lat = 37.7749, long = -122.4194)
        val testLocation = FavouritePlacesPojo(
            locationKey = locationKey,
            countryName = "San Francisco, US",
            temp = "22°C"
        )
        weatherLocalDataSource.insertLocation(testLocation)

        // When
        val resultList = weatherLocalDataSource.getAllLocations().first()

        // then

        val result = resultList.first()

        assertNotNull(result)

        assertThat(result.locationKey, `is`(testLocation.locationKey))
        assertThat(result.countryName, `is`(testLocation.countryName))
        assertThat(result.temp, `is`(testLocation.temp))

    }

    @Test
    fun deleteLocation() = runTest {
        // Given
        val locationKey = LocationKey(lat = 37.7749, long = -122.4194)
        val testLocation = FavouritePlacesPojo(
            locationKey = locationKey,
            countryName = "San Francisco, US",
            temp = "22°C"
        )

        // When
        weatherLocalDataSource.insertLocation(testLocation)
        val deleteResult = weatherLocalDataSource.deleteLocation(testLocation)
        val resultList = weatherLocalDataSource.getAllLocations().first()

        // Then
        assertThat(deleteResult, `is`(1))
        assertThat(resultList.isEmpty(), `is`(true))


    }




}