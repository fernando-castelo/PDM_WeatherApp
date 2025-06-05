package com.example.pdm_weatherapp.ui.nav

import com.example.weatherapp.ui.nav.Route
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pdm_weatherapp.HomePage
import com.example.pdm_weatherapp.ListPage
import com.example.pdm_weatherapp.MapPage

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage() }
        composable<Route.List> { ListPage() }
        composable<Route.Map>  { MapPage() }
    }
}