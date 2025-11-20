package com.example.notespot.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notespot.presentation.views.LoginView
import com.example.notespot.presentation.views.*


@Composable
fun NavManager() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        composable(Routes.Login.route) {
            LoginView(
                onLoginClick = {

                },
                onRegisterClick = {
                    navController.navigate(Routes.Register.route)
                }
            )
        }

        composable(Routes.Register.route) {
            RegisterView (
                onRegisterClick = {

                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}