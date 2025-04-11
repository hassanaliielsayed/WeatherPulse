package com.example.weatherpulse.di


import com.example.weatherpulse.alarm.viewmodel.AlarmViewModel
import com.example.weatherpulse.favourite.viewmodel.FavViewModel
import com.example.weatherpulse.home.viewmodel.HomeViewModel
import com.example.weatherpulse.local.WeatherLocalDataSource
import com.example.weatherpulse.local.WeatherLocalDataSourceInterface
import com.example.weatherpulse.local.db.WeatherDao
import com.example.weatherpulse.local.db.WeatherDataBase
import com.example.weatherpulse.local.sharedpref.SharedPref
import com.example.weatherpulse.local.sharedpref.SharedPrefInterface
import com.example.weatherpulse.remote.WeatherRemoteDataSource
import com.example.weatherpulse.remote.WeatherRemoteDataSourceInterface
import com.example.weatherpulse.remote.WeatherService
import com.example.weatherpulse.repo.Repo
import com.example.weatherpulse.repo.WeatherRepo
import com.example.weatherpulse.setting.viewmodel.SettingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<WeatherDao>{
        WeatherDataBase.getInstance(androidContext()).getDao()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/3.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<WeatherService> {
        get<Retrofit>().create(WeatherService::class.java)
    }

    single<SharedPrefInterface> {
        SharedPref.getInstance(androidContext())
    }



    factory<WeatherLocalDataSourceInterface> {
        WeatherLocalDataSource(
            get(),
            get()
        )
    }

    factory<WeatherRemoteDataSourceInterface> {
        WeatherRemoteDataSource(get())
    }

    single<WeatherRepo> {
        Repo(
            remoteDataSource = get(),
            localDataSource = get()
        )
    }

    viewModel<AlarmViewModel> {AlarmViewModel(get())}
    viewModel <FavViewModel> { FavViewModel(get() ) }
    viewModel <HomeViewModel> { HomeViewModel(get()) }
    viewModel <SettingViewModel> { SettingViewModel(get()) }

}