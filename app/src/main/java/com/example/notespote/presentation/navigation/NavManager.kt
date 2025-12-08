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

        composable(
            route = Routes.NoteContent.route,
            arguments = listOf(androidx.navigation.navArgument("apunteId") {
                type = androidx.navigation.NavType.StringType
            })
        ) { backStackEntry ->
            val apunteId = backStackEntry.arguments?.getString("apunteId") ?: run {
                android.util.Log.e("NavManager", "apunteId is null in NoteContent route")
                return@composable
            }
            android.util.Log.d("NavManager", "Opening note with apunteId: $apunteId")
            val apunteViewModel: ApunteViewModel = hiltViewModel()

            // File pickers
            val imagePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                contract = androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents()
            ) { uris ->
                if (uris.isNotEmpty()) {
                    android.util.Log.d("NavManager", "Selected ${uris.size} images")
                    apunteViewModel.addArchivos(apunteId, uris)
                }
            }

            val filePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                contract = androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents()
            ) { uris ->
                if (uris.isNotEmpty()) {
                    android.util.Log.d("NavManager", "Selected ${uris.size} files")
                    apunteViewModel.addArchivos(apunteId, uris)
                }
            }

            // Set the apunteId to trigger the flow
            LaunchedEffect(apunteId) {
                apunteViewModel.setApunteId(apunteId)
            }

            val apunteDetallado by apunteViewModel.apunteDetallado.collectAsState()
            val noteBlocks by apunteViewModel.noteBlocks.collectAsState()

            apunteDetallado?.let { detalle: com.example.notespote.domain.model.ApunteDetallado ->
                val context = androidx.compose.ui.platform.LocalContext.current

                com.example.notespote.presentation.views.BlockBasedNoteContentView(
                    apunteDetallado = detalle,
                    noteBlocks = noteBlocks,
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { titulo ->
                        // Update apunte with new title
                        val updatedApunte = detalle.apunte.copy(titulo = titulo)
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
                    onAddTextBlock = {
                        apunteViewModel.addTextBlock()
                    },
                    onAddPostipBlock = {
                        apunteViewModel.addPostipBlock()
                    },
                    onUploadFile = {
                        // Lanzar selector de archivos (PDF, DOCX, TXT, etc.)
                        filePickerLauncher.launch("*/*")
                    },
                    onAddImage = {
                        // Lanzar selector de imágenes
                        imagePickerLauncher.launch("image/*")
                    },
                    onDrawClick = { /* TODO: Implementar modo dibujo */ },
                    onBlockContentChange = { blockId, newContent ->
                        apunteViewModel.updateBlock(blockId, newContent)
                    },
                    onBlockDelete = { blockId ->
                        apunteViewModel.deleteBlock(blockId)
                    },
                    onOpenFile = { rutaLocal ->
                        try {
                            val file = java.io.File(rutaLocal)
                            val uri = androidx.core.content.FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file
                            )

                            val mimeType = context.contentResolver.getType(uri)
                                ?: when (file.extension.lowercase()) {
                                    "pdf" -> "application/pdf"
                                    "jpg", "jpeg" -> "image/jpeg"
                                    "png" -> "image/png"
                                    "gif" -> "image/gif"
                                    "doc" -> "application/msword"
                                    "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                                    "txt" -> "text/plain"
                                    else -> "application/octet-stream"
                                }

                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, mimeType)
                                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                            }

                            try {
                                context.startActivity(intent)
                                android.util.Log.d("NavManager", "Opened file: $rutaLocal with type: $mimeType")
                            } catch (e: android.content.ActivityNotFoundException) {
                                android.util.Log.e("NavManager", "No app found to open file type: $mimeType")
                                android.widget.Toast.makeText(
                                    context,
                                    "No hay aplicación para abrir este tipo de archivo",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("NavManager", "Error opening file: $rutaLocal", e)
                            android.widget.Toast.makeText(
                                context,
                                "Error al abrir el archivo",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
        }
    }
}
