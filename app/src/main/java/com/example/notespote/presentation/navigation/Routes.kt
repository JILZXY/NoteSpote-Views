package com.example.notespote.presentation.navigation

sealed class Routes(val route: String) {
    object Load : Routes("load")
    object Preload : Routes("preload")
    object Login : Routes("login")
    object Register : Routes("register")
    object Home : Routes("home")
    object Profile : Routes("profile")
    object Notifications : Routes("notifications")
    object MyProfile : Routes("my_profile")
    object EditProfile : Routes("edit_profile")
    object AccountData : Routes("account_data")
    object EditMyProfile : Routes("edit_my_profile")
    object UserProfile : Routes("user_profile")
    object AllFolders : Routes("all_folders")
    object FolderDetail : Routes("folder_detail/{folderId}") {
        fun createRoute(folderId: String) = "folder_detail/$folderId"

        // IDs especiales para carpetas fijas
        const val RECIENTES = "recientes"
        const val FAVORITOS = "favoritos"
        const val TODOS = "todos"
        const val PAPELERA = "papelera"
    }
    object NoteContent : Routes("note_content/{apunteId}") {
        fun createRoute(apunteId: String) = "note_content/$apunteId"
    }
}
