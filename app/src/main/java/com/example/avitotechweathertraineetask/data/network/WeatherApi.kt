package com.example.avitotechweathertraineetask.data.network

import com.example.avitotechweathertraineetask.data.network.model.ResponseFromGeoRequest
import com.example.avitotechweathertraineetask.data.network.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast.json?")
    suspend fun getCurrentWeatherByCity(
        @Query("key") key: String = "d34279baafa24be7a0b92903243103",
        @Query("q") q: String = "London",
        @Query("days") days: Int = 11,
        @Query("aqi") aqi: String = "yes",
        @Query("alerts") alerts: String = "no",
    ): WeatherResponse

    // определить геолокауию перед запросом прогноза погоды
    @GET("/auth/search/cities/?")
    suspend fun getLocationByGeographicalObject(
//        d34279baafa24be7a0b92903243103
//        @Header("X-Gismeteo-Token") token: String = "56b30cb255.3443075",
        @Query("key") key: String = "d34279baafa24be7a0b92903243103",
        @Query("q") q: String = "москва",
        @Query("days") days: Int = 11,
        @Query("aqi") aqi: String = "yes",
        @Query("alerts") alerts: String = "no",
    ): ResponseFromGeoRequest
}