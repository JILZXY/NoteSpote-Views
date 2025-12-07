package com.example.notespot.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notespot.presentation.components.buttons.FloatingActionButtons
import com.example.notespote.domain.model.Carpeta
import com.example.notespote.presentation.components.cards.FolderCard
import com.example.notespote.presentation.components.cards.FolderCardData
import com.example.notespote.presentation.components.cards.WelcomeCard
import com.example.notespote.presentation.components.dialogs.DeleteFolderDialog
import com.example.notespote.presentation.theme.Celeste
import com.example.notespote.presentation.theme.OutfitFamily
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.presentation.theme.SyneMonoFamily
import com.example.notespote.presentation.theme.UrbanistFamily
import com.example.notespote.presentation.views.UpdateFolderView
import com.example.notespote.viewModel.CarpetaViewModel
import com.example.notespote.viewModel.HomeViewModel

@Composable
fun HomeView(
    onProfileClick: () -> Unit,
    onAddNoteClick: () -> Unit,
    onCreateFolderClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSeeAllFoldersClick: () -> Unit,
    viewModel: HomeViewModel,
    carpetaViewModel: CarpetaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var folderToDelete by remember { mutableStateOf<Carpeta?>(null) }
    var folderToUpdate by remember { mutableStateOf<Carpeta?>(null) }

    // Carpetas predeterminadas
    val defaultFolders = listOf(
        FolderCardData("Recientes", Color(0xFF97DECC), Icons.Default.History),
        FolderCardData("Favoritos", Color(0xFFFFF347), Icons.Default.Star),
        FolderCardData("Todos los archivos", Color(0xFFFD99FF), Icons.Default.Folder)
    )

    // Convertir carpetas del usuario a pares (Carpeta, FolderCardData) (solo las primeras 5 para HomeView)
    val userFolders = uiState.recentFolders.take(5).map { carpeta ->
        val colorHex = carpeta.colorCarpeta?.removePrefix("#") ?: "FFB347"
        val color = try {
            Color(android.graphics.Color.parseColor("#$colorHex"))
        } catch (e: Exception) {
            Color(0xFFFFB347)
        }
        carpeta to FolderCardData(carpeta.nombreCarpeta, color, null)
    }

    // Combinar carpetas: primero predeterminadas, luego del usuario (máximo 5)
    val allFolders = defaultFolders.map { null to it } + userFolders

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RichBlack),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Celeste)
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RichBlack)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 60.dp, bottom = 80.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.clickable { onProfileClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Celeste),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.userName.firstOrNull()?.toString() ?: "U",
                                fontFamily = UrbanistFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = RichBlack
                            )
                        }

                        Column(
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            Text(
                                text = "Hola, ${uiState.userName}",
                                fontFamily = UrbanistFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 27.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Vamos a explorar tus apuntes",
                                fontFamily = SyneMonoFamily,
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Mostrar WelcomeCard solo si no hay contenido
                if (!uiState.hasContent) {
                    WelcomeCard(
                        onAddNoteClick = onAddNoteClick,
                        onCreateFolderClick = onCreateFolderClick
                    )
                    Spacer(modifier = Modifier.height(45.dp))
                }

                Text(
                    text = "Escritorio",
                    fontFamily = UrbanistFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = Color.White
                )

                if (!uiState.hasContent) {
                    // Vista vacía
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "Tu escritorio está vacío. Agrega una nota o una carpeta para iniciar.",
                            fontFamily = UrbanistFamily,
                            fontSize = 18.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Vista con contenido - Carpetas
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Carpetas",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = OutfitFamily
                        )
                        TextButton(onClick = onSeeAllFoldersClick) {
                            Text(
                                "Ver todas",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontFamily = OutfitFamily
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(allFolders) { (carpeta, folderData) ->
                            FolderCard(
                                folder = folderData,
                                onClick = { /* TODO: onClick */ },
                                onRename = carpeta?.let { { folderToUpdate = it } },
                                onDelete = carpeta?.let { { folderToDelete = it } }
                            )
                        }
                    }
                }
            }

            FloatingActionButtons(
                onAddNoteClick = onAddNoteClick,
                onCreateFolderClick = onCreateFolderClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 100.dp)
            )
        }
    }

    // Diálogo de eliminar
    folderToDelete?.let { carpeta ->
        DeleteFolderDialog(
            folderName = carpeta.nombreCarpeta,
            onConfirm = {
                carpetaViewModel.deleteCarpeta(carpeta.id)
                folderToDelete = null
            },
            onDismiss = { folderToDelete = null }
        )
    }

    // Diálogo de actualizar carpeta
    folderToUpdate?.let { carpeta ->
        UpdateFolderView(
            carpeta = carpeta,
            onUpdateFolder = { updatedCarpeta ->
                carpetaViewModel.updateCarpeta(updatedCarpeta)
                folderToUpdate = null
            },
            onDismiss = { folderToUpdate = null }
        )
    }
}
