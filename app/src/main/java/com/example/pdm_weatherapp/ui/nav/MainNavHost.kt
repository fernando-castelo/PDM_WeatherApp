package com.example.pdm_weatherapp.ui.nav

import com.example.weatherapp.ui.nav.Route
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pdm_weatherapp.HomePage
import com.example.pdm_weatherapp.ui.ListPage
import com.example.pdm_weatherapp.MainViewModel
import com.example.pdm_weatherapp.ui.MapPage

@Composable
fun MainNavHost(navController: NavHostController,
                viewModel: MainViewModel
) {
    NavHost(navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage(viewModel = viewModel) }
        composable<Route.List> { ListPage(viewModel = viewModel) }
        composable<Route.Map>  { MapPage(viewModel = viewModel) }
    }
}