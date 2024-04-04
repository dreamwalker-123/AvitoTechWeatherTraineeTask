package com.example.avitotechweathertraineetask.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avitotechweathertraineetask.data.network.RetrofitClient
import com.example.avitotechweathertraineetask.data.network.model.WeatherResponse
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class FirstViewModel @Inject constructor(
    private val retrofitClient: RetrofitClient,
    @ApplicationContext private val context: Context,
): ViewModel() {

    var uiState = MutableStateFlow(
        UiState(
            weatherResponse = mockWeather(),
            error = false,
            weatherResponseWithLocationServicesData = mockWeather(),
            errorByLocationServices = false,
            hasLocationAccess = hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION),
            date = getTodayDateInMillis(),
        )
    )
        private set

    init {
        getWeatherData()
    }

    // take the weather forecast from the city specified by the user
    // by default city = "Moscow"
    fun getWeatherData(q: String = "Moscow") {
        Log.i("check","1")
        viewModelScope.launch {
            try {
                Log.i("check","2.1")
                uiState.value = uiState.value.copy(weatherResponse = retrofitClient.getCurrentWeatherByCity(q = q))
                Log.i("check","2.2")
            } catch (e: Exception) {
                uiState.value = uiState.value.copy(error = true)
                Log.i("check","3")
            }
        }
    }

    fun getWeatherDataByLocationServices() {
        viewModelScope.launch {
            try {

            } catch (e: Exception) {
                uiState.value = uiState.value.copy(errorByLocationServices = true)
            }
        }
    }

    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun onPermissionChange(permission: String, isGranted: Boolean) {
        when (permission) {
            Manifest.permission.ACCESS_COARSE_LOCATION -> {
                uiState.value.hasLocationAccess = isGranted
            }
            else -> {
                Log.e("Permission change", "Unexpected permission: $permission")
            }
        }
    }

    private fun getTodayDateInMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.timeInMillis
    }

    // region Location management
    @SuppressLint("MissingPermission")
    fun fetchLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location ?: return@addOnSuccessListener

            val geocoder = Geocoder(context, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= 33)
            {
                geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                    val address = addresses.firstOrNull()
                    val place = address?.locality ?: address?.subAdminArea ?: address?.adminArea
                    ?: address?.countryName
                    uiState.value.place = place
                }
            }
            else {
                val address =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()
                        ?: return@addOnSuccessListener
                val place =
                    address.locality ?: address.subAdminArea ?: address.adminArea ?: address.countryName
                    ?: return@addOnSuccessListener

                uiState.value.place = place
            }
        }
    }
    // endregion

    private fun mockWeather(): WeatherResponse? {
        return null
    }
}

data class UiState(
    var hasLocationAccess: Boolean,
    val date: Long,
    var place: String? = null,
    var weatherResponse: WeatherResponse?,
    var error: Boolean,
    var weatherResponseWithLocationServicesData: WeatherResponse?,
    var errorByLocationServices: Boolean,
)