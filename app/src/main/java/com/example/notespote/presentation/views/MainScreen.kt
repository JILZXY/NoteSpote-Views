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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.notespote.presentation.components.buttons.FloatingActionButtons
import com.example.notespote.presentation.components.navigation.BottomNavigationBar
import com.example.notespote.presentation.navigation.BottomNavItem
import com.example.notespote.presentation.navigation.Routes

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
                    HomeViewFilled(
                        onProfileClick = { navController.navigate(Routes.Profile.route) },
                        onNotificationsClick = { navController.navigate(Routes.Notifications.route) },
                        onAddNoteClick = { showNoteDialog = true },
                        onCreateFolderClick = { showFolderDialog = true }
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
            onCreateNote = { note ->
                // Here you would typically handle the created note, e.g., pass to a ViewModel
                showNoteDialog = false
            }
        )
    }

    if (showFolderDialog) {
        NewFolderView(
            onDismiss = { showFolderDialog = false },
            onCreateFolder = { folder ->
                // Here you would typically handle the created folder, e.g., pass to a ViewModel
                showFolderDialog = false
            }
        )
    }
}
