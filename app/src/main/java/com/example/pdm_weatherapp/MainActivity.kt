package com.example.pdm_weatherapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.Consumer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pdm_weatherapp.ui.CityDialog
import com.example.pdm_weatherapp.ui.nav.BottomNavBar
import com.example.pdm_weatherapp.ui.nav.MainNavHost
import com.example.pdm_weatherapp.ui.theme.PDM_WeatherAPPTheme
import com.example.weatherapp.ui.nav.BottomNavItem
import com.example.weatherapp.ui.nav.Route
import androidx.navigation.NavDestination.Companion.hasRoute
import coil.compose.AsyncImage
import com.example.pdm_weatherapp.api.WeatherService
import com.example.pdm_weatherapp.db.fb.FBDatabase
import com.example.pdm_weatherapp.monitor.ForecastMonitor
import com.example.pdm_weatherapp.ui.ForecastItem
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val fbDB = remember { FBDatabase() }
            val weatherService = remember { WeatherService() }
            val forecastMonitor = remember { ForecastMonitor(this) }
            val viewModel : MainViewModel = viewModel(
                factory = MainViewModelFactory(fbDB, weatherService, forecastMonitor)
            )
            DisposableEffect(Unit) {
                val listener = Consumer<Intent> { intent ->
                    val name = intent.getStringExtra("city")
                    val city = viewModel.cities.find { it.name == name }
                    viewModel.city = city
                    viewModel.page = Route.Home
                }
                addOnNewIntentListener(listener)
                onDispose { removeOnNewIntentListener(listener) }
            }
            val navController = rememberNavController()
            var showDialog by remember { mutableStateOf(false) }
            val currentRoute = navController.currentBackStackEntryAsState()
            val showButton = currentRoute.value?.destination?.hasRoute(Route.List::class) == true
            val launcher = rememberLauncherForActivityResult(contract =
                ActivityResultContracts.RequestPermission(), onResult = {} )
            val notificationLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), onResult = {})

            PDM_WeatherAPPTheme {
                if (showDialog) CityDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { city ->
                        if (city.isNotBlank()) viewModel.add(city)
                        showDialog = false
                    })
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Bem-vindo/a!") },

                            actions = {
                                IconButton( onClick = { finish() } ) {
                                    Icon(
                                        imageVector =
                                            Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Localized description"
                                    )
                                }
                            }
                        )
                    },

                    bottomBar = {
                        val items = listOf(
                            BottomNavItem.HomeButton,
                            BottomNavItem.ListButton,
                            BottomNavItem.MapButton,

                            )

                        BottomNavBar(viewModel, items)

                    },

                    floatingActionButton = {

                        FloatingActionButton(onClick = { showDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Adicionar")
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        MainNavHost(navController = navController,
                                    viewModel = viewModel)
                    }
                    LaunchedEffect(viewModel.page) {
                        navController.navigate(viewModel.page) {
                            // Volta pilha de navegação até HomePage (startDest).
                            navController.graph.startDestinationRoute?.let {
                                popUpTo(it) {
                                    saveState = true
                                }
                                restoreState = true
                            }
                            launchSingleTop = true
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun HomePage(viewModel: MainViewModel) {
    Column {
        if (viewModel.city == null) {
            Column( modifier = Modifier.fillMaxSize()
                .background(Color.Blue).wrapContentSize(Alignment.Center)
            ) {
                Text(
                    text = "Selecione uma cidade!",
                    fontWeight = FontWeight.Bold, color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center, fontSize = 28.sp
                )
            }
        } else {
            Row {
                AsyncImage(
                    model = viewModel.city?.weather?.imgUrl,
                    modifier = Modifier.size(100.dp),
                    error = painterResource(id = R.drawable.loading),
                    contentDescription = "Imagem"
                )

                Column {
                    Spacer(modifier = Modifier.size(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = viewModel.city?.name ?: "Selecione uma cidade...",
                            fontSize = 28.sp,
                            modifier = Modifier.weight(1f)
                        )

                        val icon = if (viewModel.city!!.isMonitored) {
                            Icons.Filled.Notifications
                        } else {
                            Icons.Outlined.Notifications
                        }

                        Icon(
                            imageVector = icon, contentDescription = "Monitorada?",
                            modifier = Modifier
                                .size(32.dp)
                                .clickable(enabled = viewModel.city != null) {
                                    viewModel.update(
                                        viewModel.city!!.copy(
                                            isMonitored = !viewModel.city!!.isMonitored
                                        )
                                    )
                                }
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                    Text( text = viewModel.city?.name ?: "Selecione uma cidade...",
                        fontSize = 28.sp )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text( text = viewModel.city?.weather?.toString()?: "...",
                        fontSize = 22.sp )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text( text = "Temp: " + viewModel.city?.weather + "℃",
                        fontSize = 22.sp )
                }
            }
            LaunchedEffect(viewModel.city!!.name) {
                if (viewModel.city!!.forecast == null ||
                    viewModel.city!!.forecast!!.isEmpty()
                ) {
                    viewModel.loadForecast(viewModel.city!!.name)
                }
            }
            if (viewModel.city?.forecast != null) {
                LazyColumn {
                    items(viewModel.city!!.forecast!!) { forecast ->
                        ForecastItem(forecast, onClick = { })
                    }
                }
            }
        }
    }
}