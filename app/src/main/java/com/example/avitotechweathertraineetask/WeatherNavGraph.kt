package com.example.avitotechweathertraineetask

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.avitotechweathertraineetask.presentation.FirstScreen
import com.example.avitotechweathertraineetask.presentation.FirstViewModel

@Composable
fun WeatherNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(navController = navController, startDestination = ROUTE.FIRST.name) {
        composable(ROUTE.FIRST.name) {
            FirstScreen()
        }
    }
}


enum class ROUTE {
    FIRST,
    SECOND,
}