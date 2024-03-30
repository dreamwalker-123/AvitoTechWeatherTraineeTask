package com.example.avitotechweathertraineetask.presentation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun FirstScreen(
    viewModel: FirstViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    val state = viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val weather = viewModel.weather.collectAsState()

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

        Row {
            Text(text = "Населенный пункт", fontSize = 25.sp)
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
        Text(text = state.value.weatherResponse?.current?.dt.toString(), fontSize = 70.sp)
        Text(text = weather.value, fontSize = 20.sp)
        Text(text = "-10/2", fontSize = 40.sp)
        LazyRow(

        ) {
            items(listOf("-7","-7","-7","-7","-7","-7","-7")) {

            }
        }
        Card(

        ) {

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