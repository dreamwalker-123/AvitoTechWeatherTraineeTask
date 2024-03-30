package com.example.avitotechweathertraineetask.data.network

import com.example.avitotechweathertraineetask.data.network.model.ResponseFromGeoRequest
import com.example.avitotechweathertraineetask.data.network.model.WeatherResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitClient @Inject constructor(): WeatherApi {

    private val baseUrl =
        "http://api.openweathermap.org/"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
        .baseUrl(baseUrl)
        .build()
        .create(WeatherApi::class.java)

    override suspend fun getCurrentAndForecastsWeatherData(
        @Query(value = "lat") lat: Double,
        @Query(value = "lon") lon: Double,
        @Query(value = "appid") appid: String,
        @Query(value = "exclude") exclude: List<String>,
        @Query(value = "units") units: String,
        @Query(value = "lang") lang: String
    ): WeatherResponse {
        return retrofit.getCurrentAndForecastsWeatherData(appid = appid, lat = lat, lon = lon)
    }

    override suspend fun getGeoLocation(
        q: String,
        appid: String,
        limit: Int
    ): List<ResponseFromGeoRequest> {
        return retrofit.getGeoLocation()
    }

}