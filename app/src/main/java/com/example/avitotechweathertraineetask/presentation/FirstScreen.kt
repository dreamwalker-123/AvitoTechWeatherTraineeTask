package com.example.avitotechweathertraineetask.presentation

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun FirstScreen(
    viewModel: FirstViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    val state = viewModel.uiState.collectAsState()
    val weather = state.value.weatherResponse
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // requestPermissionLauncher
    val requestLocationPermissions =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                viewModel.onPermissionChange(Manifest.permission.ACCESS_COARSE_LOCATION, isGranted)
                viewModel.fetchLocation()
                viewModel.getWeatherDataByLocationServices()
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Location currently disabled due to denied permission.")
                }
            }
        }

    var showExplanationDialogForLocationPermission by remember { mutableStateOf(false) }
    if (showExplanationDialogForLocationPermission) {
        LocationExplanationDialog(
            onConfirm = {
                requestLocationPermissions.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                showExplanationDialogForLocationPermission = false
            },
            onDismiss = { showExplanationDialogForLocationPermission = false }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = state.value.weatherResponse?.location?.name ?: "", fontSize = 25.sp)
                IconButton(onClick = {
                    when {
                        state.value.hasLocationAccess -> viewModel.fetchLocation()
                        ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) ->
                            showExplanationDialogForLocationPermission = true
                        else -> requestLocationPermissions.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    }
                }) {
                    Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "Location Button")
                }
            }
            Text(text = (weather?.current?.temp_c ?: 0.0).toInt().toString() + "°", fontSize = 100.sp)
            Text(text = weather?.current?.condition?.text ?: "", fontSize = 25.sp)
            Text(text = "${(weather?.forecast?.forecastday?.first()?.day?.mintemp_c ?: 0.0).toInt()}°/" +
                    "${(weather?.forecast?.forecastday?.first()?.day?.maxtemp_c ?: 0.0).toInt()}°", fontSize = 18.sp)
        }
        LazyRow(
            modifier = Modifier.padding(start = 12.dp).fillMaxWidth()
        ) {
            // возможно фильтрацию стоит перенести в viewModel
            val list = mutableListOf<List<String>>()
            state.value.weatherResponse?.forecast?.forecastday?.first()?.hour?.forEach { list.add(listOf(it.time.split(" ").last(), "https:" + it.condition.icon, it.temp_c.toInt().toString())) }
            items(list) {
                Column(modifier = Modifier
                    .height(140.dp)
                    .width(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    ) {
                    Text(text = it.first())
                    AsyncImage(model = it[1],
                        contentDescription = "weather icon",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(60.dp)
                            .width(40.dp)
                            .padding(top = 10.dp, bottom = 10.dp)
                            .clip(RectangleShape))
                    Text(text = it.last() + "°")
                }
            }
        }
        Card(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(8.dp)
            ) {
                // возможно фильтрацию стоит перенести в viewModel
                val list = mutableListOf<List<String>>()
                val filteredList = state.value.weatherResponse?.forecast?.forecastday
                if (filteredList != null) {
                    for (i in filteredList.indices) {
                        if (i == 0)
                            continue
                        list.add(listOf(
                            filteredList[i].date.split("-").last() + "/" + filteredList[i].date.split("-")[1],
                            "https:" + filteredList[i].day.condition.icon,
                            "${(filteredList[i].day.mintemp_c).toInt()}°/" +
                                    "${(filteredList[i].day.maxtemp_c).toInt()}°"
                        ))
                    }
                }
                items(list) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = it[0], modifier = Modifier.weight(5f))
                        Text(text = "Mon", modifier = Modifier.weight(5f))
                        Row(modifier = Modifier.weight(9f)) {
                            AsyncImage(model = it[1], contentDescription = "weather icon",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(60.dp)
                                    .width(40.dp)
                                    .padding(top = 10.dp, bottom = 10.dp)
                                    .clip(RectangleShape))
                        }
                        Text(text = it[2], modifier = Modifier.weight(5f))
                    }
                }
            }
        }
    }
}

@Composable
fun LocationPicker(address: String?, fetchLocation: () -> Unit) {
    TextButton(onClick = { fetchLocation() }) {
        Icon(Icons.Filled.LocationOn, null)
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(address ?: "Get location")
    }
}

@Composable
fun LocationExplanationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Location access") },
        text = { Text("PhotoLog would like access to your location to save it when creating a log") },
        icon = {
            Icon(
                Icons.Filled.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surfaceTint
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Continue")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    )
}

@Preview
@Composable
fun PreviewFirstScreen() {
    FirstScreen()
}