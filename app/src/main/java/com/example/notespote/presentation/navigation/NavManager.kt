package com.example.notespote.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notespot.presentation.navigation.Routes
import com.example.notespot.presentation.views.LoginView
import com.example.notespot.presentation.views.RegisterView
import com.example.notespote.presentation.views.HomeView
import com.example.notespote.presentation.views.*
import com.example.notespote.presentation.views.LoadView
import com.example.notespote.presentation.views.PreloadView

@Composable
fun NavManager() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Load.route
    ) {
        composable(Routes.Load.route) {
            LoadView (
                onNavigateToPreload = {
                    navController.navigate(Routes.Preload.route) {
                        popUpTo(Routes.Load.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Preload.route) {
            PreloadView (
                onNavigateToLogin = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Preload.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route) {
                        popUpTo(Routes.Preload.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Login.route) {
            LoginView (
                onLoginClick = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Routes.Register.route)
                }
            )
        }

        composable(Routes.Register.route) {
            RegisterView (
                onRegisterClick = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Register.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.Home.route) {
            HomeView (
                onAddNoteClick = {

                },
                onCreateFolderClick = {

                },
                onProfileClick = {

                }
            )
        }
    }
}
