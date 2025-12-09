package com.example.notespote.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notespote.data.local.entities.TipoVisibilidad
import com.example.notespote.presentation.components.cards.NoteCard
import com.example.notespote.presentation.components.cards.NoteCardData
import com.example.notespote.presentation.navigation.Routes
import com.example.notespote.presentation.theme.OutfitFamily
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.viewModel.ApunteViewModel
import com.example.notespote.viewModel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FolderDetailView(
    folderId: String,
    onBackClick: () -> Unit,
    onNoteClick: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    apunteViewModel: ApunteViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()

    // Determinar el nombre de la carpeta y las notas a mostrar
    val folderInfo = when (folderId) {
        Routes.FolderDetail.RECIENTES -> "Recientes" to uiState.recentNotes
        Routes.FolderDetail.FAVORITOS -> "Favoritos" to uiState.favoriteNotes
        Routes.FolderDetail.TODOS -> "Todos los archivos" to uiState.allNotes
        Routes.FolderDetail.PAPELERA -> "Papelera" to uiState.deletedNotes
        else -> {
            // Es una carpeta del usuario
            val carpeta = uiState.recentFolders.find { it.id == folderId }
            val folderNotes = uiState.allNotes.filter { it.idCarpeta == folderId }
            (carpeta?.nombreCarpeta ?: "Carpeta") to folderNotes
        }
    }
    val (folderName, notesInFolder) = folderInfo

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RichBlack)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Atrás",
                tint = Color.White,
                modifier = Modifier.clickable { onBackClick() }
            )
            Text(
                folderName,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                fontFamily = OutfitFamily,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (notesInFolder.isEmpty()) {
            Text(
                "No hay apuntes en esta carpeta",
                color = Color.Gray,
                fontSize = 16.sp,
                fontFamily = OutfitFamily,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(notesInFolder) { apunte ->
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(Date(apunte.fechaCreacion))

                    NoteCard(
                        note = NoteCardData(
                            title = apunte.titulo,
                            description = apunte.descripcion ?: "Sin descripción",
                            tags = emptyList(),
                            subject = uiState.getMateriaName(apunte.idMateria),
                            date = formattedDate,
                            isPublic = apunte.tipoVisibilidad == TipoVisibilidad.PUBLICO
                        ),
                        onClick = { onNoteClick(apunte.id) },
                        onFavoriteClick = {
                            apunteViewModel.toggleFavorito(apunte.id)
                        },
                        isFavorito = apunte.isFavorito
                    )
                }
            }
        }
    }
}
