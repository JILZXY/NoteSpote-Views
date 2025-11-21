package com.example.notespot.presentation.navigation

sealed class Routes(val route: String) {
    object Load : Routes("load")
    object Preload : Routes("preload")
    object Login : Routes("login")
    object Register : Routes("register")
    object Home : Routes("home")
}