package com.example.avitotechweathertraineetask.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    @SerialName("lat") val lat: Double,
    @SerialName("lon") val lon: Double,
    @SerialName("timezone") val timezone: String,
    @SerialName("timezone_offset") val timezoneOffset: Int,
    @SerialName("current") val current: CurrentWeather,
    @SerialName("minutely") val minutely: List<Minutely>,
    @SerialName("hourly") val hourly: List<Hourly>,
    @SerialName("daily") val daily: List<Daily>,
    @SerialName("alerts") val alerts: List<Alerts>
)

@Serializable
data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
)

@Serializable
data class CurrentWeather(
    val dt: Int,
    val sunrise: Int,
    val sunset: Int,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val wind_gust: Double,
    val weather: List<Weather>,
)

@Serializable
data class Minutely(
    val dt: Int,
    val precipitation: Int,
)

@Serializable
data class Hourly(
    val dt: Int,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Int,
    val clouds: Int,
    val visibility: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val wind_gust: Double,
    val weather: List<Weather>,
    val pop: Double,
)
@Serializable
data class Temp(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double,
)
@Serializable
data class FeelsLike(
    val day: Double,
    val night: Double,
    val eve: Double,
    val morn: Double,
)

@Serializable
data class Daily(
    val dt: Int,
    val sunrise: Int,
    val sunset: Int,
    val moonrise: Int,
    val moonset: Int,
    val moon_phase: Double,
    val summary: String,
    val temp: Temp,
    val feels_like: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val wind_speed: Double,
    val wind_deg: Int,
    val wind_gust: Double,
    val weather: List<Weather>,
    val clouds: Int,
    val pop: Double,
    val rain: Double,
    val uvi: Double,
)
@Serializable
data class Alerts (
    val sender_name: String,
    val event: String,
    val start: Int,
    val end: Int,
    val description: String,
    val tags: List<String>,
)
