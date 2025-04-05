package test.fakes

import com.example.weatherpulse.model.*
import com.example.weatherpulse.remote.WeatherRemoteDataSourceInterface

class FakeWeatherRemoteDataSource : WeatherRemoteDataSourceInterface {

    var shouldReturnError = false

    override suspend fun getCurrentWeather(location: Location, unit: String): WeatherDetailsResponse {
        if (shouldReturnError) throw Exception("Network error")

        return WeatherDetailsResponse(
            lat = location.latitude,
            lon = location.longitude,
            timezone = "Fake/TimeZone",
            timezone_offset = 0,
            current = Current(
                dt = 1680000000,
                sunrise = 1680000000,
                sunset = 1680040000,
                temp = 25.0,
                feels_like = 26.0,
                pressure = 1012,
                humidity = 60,
                dew_point = 15.0,
                uvi = 3.0,
                clouds = 10,
                visibility = 10000,
                wind_speed = 5.0,
                wind_deg = 90,
                weather = listOf(
                    Weather(
                        id = 800,
                        main = "Clear",
                        description = "Sunny",
                        icon = "01d"
                    )
                )
            ),
            hourly = emptyList(),
            daily = emptyList()
        )
    }
}