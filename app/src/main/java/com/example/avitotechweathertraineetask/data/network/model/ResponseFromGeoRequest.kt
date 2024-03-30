package com.example.avitotechweathertraineetask.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseFromGeoRequest(
    @SerialName("name") val name: String,
    @SerialName("local_names") val localNames: Sities,
    @SerialName("lat") val lat: Double,
    @SerialName("lon") val lon: Double,
    @SerialName("country") val country: String,
    @SerialName("state") val state: String
)

@Serializable
data class Sities(
    val cn: String,
    val en: String,
    val es: String,
    val ru: String,
)
