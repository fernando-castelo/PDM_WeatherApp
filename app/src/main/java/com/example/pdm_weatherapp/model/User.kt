package com.example.pdm_weatherapp.model

data class User(val name: String, val email: String, var weather: Weather? = null)

