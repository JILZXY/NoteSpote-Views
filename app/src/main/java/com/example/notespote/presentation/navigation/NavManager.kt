package com.example.notespote.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notespot.presentation.views.LoginView
import com.example.notespot.presentation.views.RegisterView
import com.example.notespote.presentation.views.AccountDataView
import com.example.notespote.presentation.views.AllFoldersView
import com.example.notespote.presentation.views.EditMyProfileView
import com.example.notespote.presentation.views.EditProfileView
import com.example.notespote.presentation.views.FolderDetailView
import com.example.notespote.presentation.views.LoadView
import com.example.notespote.presentation.views.MainScreen
import com.example.notespote.presentation.views.MyProfileView
import com.example.notespote.presentation.views.NoteContentView
import com.example.notespote.presentation.views.NotificationsView
import com.example.notespote.presentation.views.PreloadView
import com.example.notespote.presentation.views.ProfileView
import com.example.notespote.presentation.views.UserProfileView
import com.example.notespote.viewModel.ApunteViewModel
import com.example.notespote.viewModel.HomeViewModel

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
                onRegisterSuccess = {
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

        composable(Routes.AllFolders.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            AllFoldersView(
                onBackClick = { navController.popBackStack() },
                onFolderClick = { navController.navigate(Routes.FolderDetail.route) },
                viewModel = homeViewModel
            )
        }

        composable(Routes.FolderDetail.route) {
            FolderDetailView(onBackClick = { navController.popBackStack() })
        }

        composable(Routes.NoteContent.route) { backStackEntry ->
            val apunteId = backStackEntry.arguments?.getString("apunteId") ?: return@composable
            val apunteViewModel: ApunteViewModel = hiltViewModel()

            // Set the apunteId to trigger the flow
            LaunchedEffect(apunteId) {
                apunteViewModel.setApunteId(apunteId)
            }

            val apunteDetallado by apunteViewModel.apunteDetallado.collectAsState()

            apunteDetallado?.let { detalle: com.example.notespote.domain.model.ApunteDetallado ->
                NoteContentView(
                    apunteDetallado = detalle,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { /* User clicked edit button */ },
                    onSaveClick = { titulo, contenido, tags ->
                        // Update apunte with new title and content
                        val updatedApunte = detalle.apunte.copy(
                            titulo = titulo,
                            contenido = contenido
                        )
                        apunteViewModel.updateApunte(updatedApunte)
                    },
                    onAddTag = { tagParam ->
                        // Si empieza con "REMOVE:", es una eliminación
                        if (tagParam.startsWith("REMOVE:")) {
                            val etiquetaId = tagParam.removePrefix("REMOVE:")
                            apunteViewModel.removerEtiqueta(apunteId, etiquetaId)
                        } else {
                            // Es una nueva etiqueta a agregar
                            apunteViewModel.agregarEtiqueta(apunteId, tagParam)
                        }
                    },
                    onAddText = { /* TODO: Implementar agregar texto */ },
                    onUploadFile = { /* TODO: Implementar subir archivo */ },
                    onAddImage = { /* TODO: Implementar agregar imagen */ },
                    onDrawClick = { /* TODO: Implementar modo dibujo */ }
                )
            }
        }
    }
}
