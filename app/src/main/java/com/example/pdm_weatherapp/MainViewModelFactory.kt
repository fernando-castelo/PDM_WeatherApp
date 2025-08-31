package com.example.pdm_weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pdm_weatherapp.api.WeatherService
import com.example.pdm_weatherapp.db.fb.FBDatabase
import com.example.pdm_weatherapp.monitor.ForecastMonitor

class MainViewModelFactory(private val db : FBDatabase,
                           private val service: WeatherService,
                           private val forecastMonitor: ForecastMonitor) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(db, service, forecastMonitor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
