package com.example.avitotechweathertraineetask.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ResponseFromGeoRequest(
    val id: Int,
    val name: String,
    val url: String,
    val kind: String,
    val country: Country,
    val district: District,
    val sub_district: Sub_district,
)

@Serializable
data class Country(
    val code: String,
    val name: String,
    val nameP: String,
)
@Serializable
data class District(
    val name: String,
    val nameP: String,
)
@Serializable
data class Sub_district(
    val name: String,
    val nameP: String,
)