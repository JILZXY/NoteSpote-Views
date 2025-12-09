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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notespote.domain.model.Carpeta
import com.example.notespote.presentation.components.cards.FolderCard
import com.example.notespote.presentation.components.cards.FolderCardData
import com.example.notespote.presentation.components.dialogs.DeleteFolderDialog
import com.example.notespote.presentation.theme.Celeste
import com.example.notespote.presentation.theme.OutfitFamily
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.viewModel.CarpetaViewModel
import com.example.notespote.viewModel.HomeViewModel

@Composable
fun AllFoldersView(
    onBackClick: () -> Unit,
    onFolderClick: (String) -> Unit,
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
        FolderCardData("Todos los archivos", Color(0xFFFD99FF), Icons.Default.Notes)
    )

    // Convertir carpetas del usuario a pares (Carpeta, FolderCardData)
    val userFolders = uiState.recentFolders.map { carpeta ->
        val colorHex = carpeta.colorCarpeta?.removePrefix("#") ?: "FFB347"
        val color = try {
            Color(android.graphics.Color.parseColor("#$colorHex"))
        } catch (e: Exception) {
            Color(0xFFFFB347)
        }
        carpeta to FolderCardData(carpeta.nombreCarpeta, color, null)
    }

    // Combinar carpetas: primero predeterminadas, luego del usuario
    val allFolders = defaultFolders.map { null to it } + userFolders

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
                "Todas las carpetas",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                fontFamily = OutfitFamily,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(allFolders) { (carpeta, folderData) ->
                FolderCard(
                    folder = folderData,
                    onClick = {
                        // Determinar el ID de la carpeta
                        val folderId = when (folderData.title) {
                            "Recientes" -> com.example.notespote.presentation.navigation.Routes.FolderDetail.RECIENTES
                            "Favoritos" -> com.example.notespote.presentation.navigation.Routes.FolderDetail.FAVORITOS
                            "Todos los archivos" -> com.example.notespote.presentation.navigation.Routes.FolderDetail.TODOS
                            else -> carpeta?.id ?: return@FolderCard
                        }
                        onFolderClick(folderId)
                    },
                    onRename = carpeta?.let { { folderToUpdate = it } },
                    onDelete = carpeta?.let { { folderToDelete = it } }
                )
            }
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
