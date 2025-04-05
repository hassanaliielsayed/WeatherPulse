package com.example.weatherpulse.repo

import com.example.weatherpulse.model.Alarm
import com.example.weatherpulse.model.FavouritePlacesPojo
import com.example.weatherpulse.model.Location
import com.example.weatherpulse.model.LocationKey
import com.example.weatherpulse.util.Constants.AlarmType
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import test.fakes.FakeWeatherRemoteDataSource
import kotlin.test.assertNotNull


class RepoTest {


    private lateinit var localDataSource: FakeWeatherLocalDataSource
    private lateinit var remoteDataSource: FakeWeatherRemoteDataSource
    private lateinit var repo: WeatherRepo

    @Before
    fun setup() {

        val favList = mutableListOf(
            FavouritePlacesPojo(
                locationKey = LocationKey(lat = 37.7749, long = -122.4194),
                countryName = "San Francisco, US",
                temp = "22°C"
            ),
            FavouritePlacesPojo(
                locationKey = LocationKey(lat = 25.210, long = -201.021),
                countryName = "Egypt, US",
                temp = "52°C"
            )
        )

        val alarmList = mutableListOf(
            Alarm(
                id = 1,
                time = 2,
                longitude = -122.4194,
                latitude = 37.7749,
                city = "mansora",
                type = AlarmType.ALARM,
            ),

            Alarm(
                id = 2,
                time = 5,
                longitude = 225.012,
                latitude = 36.012,
                city = "Demietta",
                type = AlarmType.NOTIFICATION,
            )
        )
        localDataSource = FakeWeatherLocalDataSource(favList, alarmList)
        remoteDataSource = FakeWeatherRemoteDataSource()
        repo = Repo.getInstance(remoteDataSource, localDataSource)
    }

    @Test
    fun insertLocation() = runTest {

        val location = FavouritePlacesPojo(
            locationKey = LocationKey(lat = 40.7128, long = -74.0060),
            countryName = "s",
            temp = "18°C"
        )

        val result = repo.insertLocation(location)
        assertThat(result, `is`(1L))
    }

    @Test
    fun getCurrentWeather() = runTest {

        val location = Location(31.000, 32.000)
        val unit = "metric"

        val result = repo.getCurrentWeather(location, unit)

        assertNotNull(result)

        assertThat(result.lon, `is`(location.longitude))
        assertThat(result.lat, `is`(location.latitude))

    }


}