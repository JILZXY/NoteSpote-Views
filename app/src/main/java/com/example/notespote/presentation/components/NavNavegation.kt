package com.example.notespote.presentation.components

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun NavNavegation (){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home"){}
}