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

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl).client(okHttpClient)
        .build()
        .create(WeatherApi::class.java)

    override suspend fun getCurrentWeatherByCity(
        @Query("key") key: String,
        @Query("query") query: String,
        @Query("days") days: Int,
        @Query("aqi") aqi: String,
        @Query("alerts") alerts: String
    ): WeatherResponse {
        return retrofit.getCurrentWeatherByCity(key = key, query = query, days = days, aqi = aqi, alerts = alerts)
    }

    override suspend fun getLocationByGeographicalObject(
        key: String,
        query: String,
        days: Int,
        aqi: String,
        alerts: String
    ): ResponseFromGeoRequest {
        TODO("Not yet implemented")
    }
}