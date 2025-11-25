package com.example.notespote.presentation.navManager

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notespote.presentation.views.Home

@Composable
fun NavManager (){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home"){
        composable("home"){
            Home(navController)
        }
    }
}