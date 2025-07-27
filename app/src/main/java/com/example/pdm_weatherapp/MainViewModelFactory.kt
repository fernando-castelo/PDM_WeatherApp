package com.example.pdm_weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pdm_weatherapp.api.WeatherService
import com.example.pdm_weatherapp.db.fb.FBDatabase

class MainViewModelFactory(private val db : FBDatabase,
                           private val service: WeatherService) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(db, service) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
