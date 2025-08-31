package com.example.pdm_weatherapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pdm_weatherapp.MainViewModel
import com.example.pdm_weatherapp.R
import com.example.pdm_weatherapp.db.fb.toFBCity
import com.example.pdm_weatherapp.model.City
import com.example.weatherapp.ui.nav.BottomNavItem.HomeButton.icon
import com.example.weatherapp.ui.nav.Route

@SuppressLint("ContextCastToActivity")
@Composable
fun ListPage(modifier: Modifier = Modifier,
             viewModel: MainViewModel
) {

    val cityList = viewModel.cities
    val activity = LocalContext.current as? Activity
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(cityList, key = { it.name }) { city ->
            LaunchedEffect(city.name) {
                if (city.weather == null) {
                    viewModel.loadWeather(city.name)
                }
            }
            CityItem(city = city,
                     onClick = {
                         viewModel.city = city
                         viewModel.page = Route.Home},
                     onClose = {
                     viewModel.onCityRemoved(city.toFBCity())
                     Toast.makeText(activity, "Cidade removida: $city.name", Toast.LENGTH_LONG).show()
                        })
        }
    }
}

@Composable
fun CityItem(
    city: City,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {

    val icon = if (city.isMonitored) {
        Icons.Filled.Notifications
    } else {
        Icons.Outlined.Notifications
    }
    Row(
        modifier = modifier.fillMaxWidth().padding(8.dp).clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = city.weather?.imgUrl,
            modifier = Modifier.size(75.dp),
            error = painterResource(id = R.drawable.loading),
            contentDescription = "Imagem"
        )
        Spacer(modifier = modifier.size(12.dp))
        Column(modifier = modifier.weight(1f)) {
            Text(
                modifier = Modifier,
                text = city.name,
                fontSize = 24.sp
            )
            Text(
                modifier = Modifier,
                text = city.weather?.desc?:"carregando...",
                fontSize = 16.sp
            )
        }

        Icon(
            imageVector = icon,
            contentDescription = "Status de monitoramento",
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.size(8.dp))
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
    }
}