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

@HiltViewModel
class FirstViewModel @Inject constructor(
    val retrofitClient: RetrofitClient,
    application: Application
): AndroidViewModel(application) {

    private val context: Context
        get() = getApplication()

    val weather = MutableStateFlow("10")
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
    fun getWeatherData(q: String = "Moscow") {
        Log.i("check","qweqwqweqweqweqweqwe")
        viewModelScope.launch {
            try {
//                uiState = uiState.copy(hasLocationAccess = isGranted)

//                uiState.value.geoLocationResponse = retrofitClient.getGeoLocation(q)
                uiState.value = uiState.value.copy(geoLocationResponse = retrofitClient.getGeoLocation(q))
//                {uiState.value.geoLocationResponse?.first()?.country}
                Log.i("check","rtyrtyrtyrtyrty")

                val appid = BuildConfig.apiKey
                val lat = uiState.value.geoLocationResponse!!.first().lat
                val lon = uiState.value.geoLocationResponse!!.first().lon

//                uiState.value.weatherResponse = retrofitClient.getCurrentAndForecastsWeatherData(
//                    appid = appid, lat = lat, lon = lon)
                uiState.value = uiState.value.copy(weatherResponse = retrofitClient.getCurrentAndForecastsWeatherData(
                    appid = appid, lat = lat, lon = lon))

                weather.value = uiState.value.weatherResponse!!.current.dt.toString()
            } catch (e: Exception) {
                uiState.value.error = true
            }
        }
    }

    fun getWeatherDataByLocationServices() {
        viewModelScope.launch {
            try {
                val place = uiState.value.place
                if (place != null) {
                    uiState.value.geoLocationResponseByLocationServices =
                        retrofitClient.getGeoLocation(place)
                    val appid = BuildConfig.apiKey
                    val lat = uiState.value.geoLocationResponseByLocationServices!!.first().lat
                    val lon = uiState.value.geoLocationResponseByLocationServices!!.first().lon
                    uiState.value.weatherResponseByLocationServices =
                        retrofitClient.getCurrentAndForecastsWeatherData(appid = appid, lat = lat, lon = lon)
                }
            } catch (e: Exception) {
                uiState.value.errorByLocationServices = true
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
    private fun mockGeoLocation(): List<ResponseFromGeoRequest>? {
        return null
    }
}

data class UiState(
    var hasLocationAccess: Boolean,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val date: Long,
    var place: String? = null,
    var geoLocationResponse: List<ResponseFromGeoRequest>?,
    var weatherResponse: WeatherResponse?,
    var error: Boolean,
    var geoLocationResponseByLocationServices: List<ResponseFromGeoRequest>?,
    var weatherResponseByLocationServices: WeatherResponse?,
    var errorByLocationServices: Boolean,
)