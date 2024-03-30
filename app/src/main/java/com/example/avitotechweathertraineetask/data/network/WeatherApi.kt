package com.example.avitotechweathertraineetask.data.network

import com.example.avitotechweathertraineetask.BuildConfig
import com.example.avitotechweathertraineetask.data.network.model.ResponseFromGeoRequest
import com.example.avitotechweathertraineetask.data.network.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/3.0/onecall?")
    suspend fun getCurrentAndForecastsWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appid: String,
        @Query("exclude") exclude: List<String> = listOf("minutely", "alerts"),
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ru",
    ): WeatherResponse

    // определить геолокауию перед запросом прогноза погоды
    @GET("geo/1.0/direct?")
    suspend fun getGeoLocation(
        @Query("q") q: String = "Moscow",
        @Query("appid") appid: String = BuildConfig.apiKey,
        @Query("limit") limit: Int = 5,
    ): List<ResponseFromGeoRequest>
}