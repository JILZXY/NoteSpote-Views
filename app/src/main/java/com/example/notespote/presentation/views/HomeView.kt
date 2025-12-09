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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.notespote.R
import com.example.notespote.data.local.entities.TipoVisibilidad
import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.model.Carpeta
import com.example.notespote.presentation.components.cards.FolderCard
import com.example.notespote.presentation.components.cards.FolderCardData
import com.example.notespote.presentation.components.cards.NoteCard
import com.example.notespote.presentation.components.cards.NoteCardData
import com.example.notespote.presentation.components.cards.WelcomeCard
import com.example.notespote.presentation.components.dialogs.DeleteFolderDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.notespote.presentation.theme.Celeste
import com.example.notespote.presentation.theme.OutfitFamily
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.presentation.theme.SyneMonoFamily
import com.example.notespote.presentation.theme.UrbanistFamily
import com.example.notespote.presentation.views.UpdateFolderView
import com.example.notespote.viewModel.ApunteViewModel
import com.example.notespote.viewModel.CarpetaViewModel
import com.example.notespote.viewModel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeView(
    onProfileClick: () -> Unit,
    onAddNoteClick: () -> Unit,
    onCreateFolderClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSeeAllFoldersClick: () -> Unit,
    onNoteClick: (String) -> Unit,
    viewModel: HomeViewModel,
    carpetaViewModel: CarpetaViewModel = hiltViewModel(),
    apunteViewModel: ApunteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val currentUser = FirebaseAuth.getInstance().currentUser

    if (currentUser == null) {
        // Redirigir a login
        Text("Usuario no autenticado", color = Color.Red)
        return
    }

    LaunchedEffect(Unit) {

         val userId = FirebaseAuth.getInstance().currentUser?.uid
         if (userId != null) {
             carpetaViewModel.loadCarpetasRaiz(userId)
             apunteViewModel.loadApuntesByUser(userId)
         }
    }

    var folderToDelete by remember { mutableStateOf<Carpeta?>(null) }
    var folderToUpdate by remember { mutableStateOf<Carpeta?>(null) }
    var apunteToDelete by remember { mutableStateOf<Apunte?>(null) }

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
                    .padding(top = 100.dp, bottom = 80.dp)
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
                        if (uiState.userProfilePhoto != null) {
                            coil.compose.AsyncImage(
                                model = uiState.userProfilePhoto,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
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

                    // Sección de Apuntes Recientes
                    if (uiState.recentNotes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            "Apuntes Recientes",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = OutfitFamily
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            uiState.recentNotes.forEach { apunte ->
                                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                val formattedDate = try {
                                    dateFormat.format(Date(apunte.fechaCreacion))
                                } catch (e: Exception) {
                                    "Fecha desconocida"
                                }

                                NoteCard(
                                    note = NoteCardData(
                                        title = apunte.titulo,
                                        description = "Sin descripción",
                                        tags = emptyList(), // Las etiquetas se pueden agregar después
                                        subject = apunte.idMateria ?: "Sin materia",
                                        date = formattedDate,
                                        isPublic = apunte.tipoVisibilidad == TipoVisibilidad.PUBLICO
                                    ),
                                    onClick = { onNoteClick(apunte.id) },
                                    onLongClick = { apunteToDelete = apunte }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de eliminar carpeta
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

    // Diálogo de eliminar apunte
    apunteToDelete?.let { apunte ->
        DeleteNoteDialog(
            noteTitle = apunte.titulo,
            onConfirm = {
                apunteViewModel.deleteApunte(apunte.id)
                apunteToDelete = null
            },
            onDismiss = { apunteToDelete = null }
        )
    }
}

@Composable
private fun DeleteNoteDialog(
    noteTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Eliminar apunte",
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Text(
                "¿Estás seguro de que deseas eliminar \"$noteTitle\"? Esta acción no se puede deshacer.",
                fontFamily = OutfitFamily,
                color = Color.White
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Eliminar", fontFamily = OutfitFamily, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", fontFamily = OutfitFamily, color = Color.White)
            }
        },
        containerColor = Color(0xFF1E1E1E)
    )
}
