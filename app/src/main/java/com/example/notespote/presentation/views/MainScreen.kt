package com.example.notespote.presentation.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.notespot.presentation.components.buttons.FloatingActionButtons
import com.example.notespote.domain.model.Folder
import com.example.notespote.domain.model.Note
import com.example.notespote.presentation.components.navigation.BottomNavigationBar
import com.example.notespote.presentation.navigation.BottomNavItem
import com.example.notespote.presentation.navigation.Routes
import com.example.notespote.viewModel.ApunteViewModel
import com.example.notespote.viewModel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()

    val homeViewModel: HomeViewModel = hiltViewModel()
    val apunteViewModel: ApunteViewModel = hiltViewModel()

    val navigationItems = listOf(
        BottomNavItem.Search,
        BottomNavItem.Home,
        BottomNavItem.Community
    )

    var showNoteDialog by remember { mutableStateOf(false) }
    var showFolderDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = bottomNavController, items = navigationItems)
        },
        floatingActionButton = {
            if (currentRoute == BottomNavItem.Home.route) {
                FloatingActionButtons(
                    onAddNoteClick = { showNoteDialog = true },
                    onCreateFolderClick = { showFolderDialog = true }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(navController = bottomNavController, startDestination = BottomNavItem.Home.route) {
                composable(BottomNavItem.Home.route) {
                    com.example.notespot.presentation.views.HomeView(
                        onProfileClick = { navController.navigate(Routes.MyProfile.route) },
                        onNotificationsClick = { navController.navigate(Routes.Notifications.route) },
                        onAddNoteClick = { showNoteDialog = true },
                        onCreateFolderClick = { showFolderDialog = true },
                        onSeeAllFoldersClick = { navController.navigate(Routes.AllFolders.route) },
                        onNoteClick = { apunteId ->
                            navController.navigate(Routes.NoteContent.createRoute(apunteId))
                        },
                        viewModel = homeViewModel
                    )
                }
                composable(BottomNavItem.Search.route) {
                    SearchView()
                }
                composable(BottomNavItem.Community.route) {
                    CommunityView(onAuthorClick = { navController.navigate(Routes.UserProfile.route) })
                }
            }
        }
    }

    if (showNoteDialog) {
        NewNoteView(
            onDismiss = { showNoteDialog = false },
            onCreateNote = { note: Note ->
                // Crear el apunte usando el ViewModel
                android.util.Log.d("MainScreen", "Creating note: title=${note.title}, subject=${note.subject}")

                // Convertir visibilidad a TipoVisibilidad
                val tipoVisibilidad = if (note.isPublic)
                    com.example.notespote.data.local.entities.TipoVisibilidad.PUBLICO
                else
                    com.example.notespote.data.local.entities.TipoVisibilidad.PRIVADO

                // Crear apunte (sin carpeta ni materia por ahora, se pueden agregar después)
                apunteViewModel.createApunte(
                    titulo = note.title,
                    contenido = note.description,
                    idCarpeta = null,
                    idMateria = null,
                    tipoVisibilidad = tipoVisibilidad,
                    archivos = emptyList()
                )

                // Esperar un momento y recargar los datos
                scope.launch {
                    delay(300)
                    homeViewModel.refresh()
                }

                showNoteDialog = false
            }
        )
    }

    if (showFolderDialog) {
        NewFolderView(
            onDismiss = { showFolderDialog = false },
            onCreateFolder = { folder: Folder ->
                // Crear la carpeta usando el ViewModel
                android.util.Log.d("MainScreen", "Creating folder: title=${folder.title}, color=${folder.color}")
                homeViewModel.createFolder(folder.title, folder.color)
                showFolderDialog = false
            }
        )
    }
}
