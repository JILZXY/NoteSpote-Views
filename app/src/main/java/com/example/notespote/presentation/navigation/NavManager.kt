package com.example.notespote.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notespot.presentation.views.LoginView
import com.example.notespot.presentation.views.RegisterView
import com.example.notespote.presentation.views.AccountDataView
import com.example.notespote.presentation.views.EditMyProfileView
import com.example.notespote.presentation.views.EditProfileView
import com.example.notespote.presentation.views.LoadView

import com.example.notespote.presentation.views.MainScreen
import com.example.notespote.presentation.views.MyProfileView
import com.example.notespote.presentation.views.NotificationsView
import com.example.notespote.presentation.views.PreloadView
import com.example.notespote.presentation.views.ProfileView

import com.example.notespote.presentation.views.UserProfileView

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
            MainScreen(navController = navController)
        }

        composable(Routes.Profile.route) {
            ProfileView(
                onSignOutClick = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                },
                onAccountDataClick = { navController.navigate(Routes.AccountData.route) },
                onMyProfileClick = { navController.navigate(Routes.MyProfile.route) },
                onEditProfileImageClick = { navController.navigate(Routes.EditProfile.route) }
            )
        }

        composable(Routes.MyProfile.route) {
            MyProfileView(
                onBackClick = { navController.popBackStack() },
                onEditProfileClick = { navController.navigate(Routes.EditMyProfile.route) }
            )
        }

        composable(Routes.Notifications.route) {
            NotificationsView()
        }

        composable(Routes.EditProfile.route) {
            EditProfileView(onBackClick = { navController.popBackStack() })
        }

        composable(Routes.EditMyProfile.route) {
            EditMyProfileView(onBackClick = { navController.popBackStack() })
        }

        composable(Routes.AccountData.route) {
            AccountDataView(onBackClick = { navController.popBackStack() })
        }

        composable(Routes.UserProfile.route) {
            UserProfileView(onBackClick = { navController.popBackStack() })
        }
    }
}
