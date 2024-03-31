package com.example.avitotechweathertraineetask.presentation

import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.example.avitotechweathertraineetask.BuildConfig
import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.service.autofill.UserData
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.avitotechweathertraineetask.data.network.RetrofitClient
import com.example.avitotechweathertraineetask.data.network.model.ResponseFromGeoRequest
import com.example.avitotechweathertraineetask.data.network.model.WeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class FirstViewModel @Inject constructor(
    val retrofitClient: RetrofitClient,
    application: Application
): AndroidViewModel(application) {

    private val context: Context
        get() = getApplication()

    private val _temperature = MutableStateFlow("qwe")
    val temperature = _temperature.asStateFlow()
    var uiState = MutableStateFlow(
        UiState(
            weatherResponse = mockWeather(),
            geoLocationResponse = mockGeoLocation(),
            error = false,
            weatherResponseByLocationServices = mockWeather(),
            geoLocationResponseByLocationServices = mockGeoLocation(),
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
    fun getWeatherData(query: String = "Москва") {
        Log.i("check","1")
        viewModelScope.launch {
            try {
                Log.i("check","21")
                val weather = retrofitClient.getCurrentWeatherByCity(query = query)
//                uiState.value.weatherResponse = weather
                _temperature.value = weather.location.name
//                uiState.value = uiState.value.copy(weatherResponse = retrofitClient.getCurrentWeatherByCity(query = query))
                Log.i("check","22")
            } catch (e: Exception) {
//                uiState.value.error = true
                Log.i("check","3")
            }
       }
    }

    fun getWeatherDataByLocationServices() {
        viewModelScope.launch {
//            try {
//                val place = uiState.value.place
//                if (place != null) {
//                    uiState.value.geoLocationResponseByLocationServices =
//                        retrofitClient.getLocationByGeographicalObject(place)
//                    val appid = BuildConfig.apiKey
//                }
//            } catch (e: Exception) {
//                uiState.value.errorByLocationServices = true
//            }
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
    private fun mockGeoLocation(): ResponseFromGeoRequest? {
        return null
    }
}

data class UiState(
    var hasLocationAccess: Boolean,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val date: Long,
    var place: String? = null,
    var geoLocationResponse: ResponseFromGeoRequest?,
    var weatherResponse: WeatherResponse?,
    var error: Boolean,
    var geoLocationResponseByLocationServices: ResponseFromGeoRequest?,
    var weatherResponseByLocationServices: WeatherResponse?,
    var errorByLocationServices: Boolean,
)