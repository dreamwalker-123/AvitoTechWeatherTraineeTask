package com.example.avitotechweathertraineetask.data.network

import com.example.avitotechweathertraineetask.data.network.model.ResponseFromGeoRequest
import com.example.avitotechweathertraineetask.data.network.model.WeatherResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitClient @Inject constructor(): WeatherApi {

    private val baseUrl =
        "https://api.weatherapi.com/v1/"

//    наше апи
//    https://www.weatherapi.com/api-explorer.aspx#forecast
//    профиль
//    https://www.weatherapi.com/my/

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private var json = Json { ignoreUnknownKeys = true }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl).client(okHttpClient)
        .build()
        .create(WeatherApi::class.java)

    override suspend fun getCurrentWeatherByCity(
        @Query("key") key: String,
        @Query("q") q: String,
        @Query("days") days: Int,
        @Query("aqi") aqi: String,
        @Query("alerts") alerts: String,
    ): WeatherResponse {
        return retrofit.getCurrentWeatherByCity(key = key, q = q, days = days, aqi = aqi, alerts = alerts)
    }

    override suspend fun getLocationByGeographicalObject(
        key: String,
        q: String,
        days: Int,
        aqi: String,
        alerts: String
    ): ResponseFromGeoRequest {
        return retrofit.getLocationByGeographicalObject()
    }
}