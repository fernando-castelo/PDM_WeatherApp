package com.example.pdm_weatherapp

import android.app.Application
import com.google.firebase.FirebaseApp

class WeatherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}