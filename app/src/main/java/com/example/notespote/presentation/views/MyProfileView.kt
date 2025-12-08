package com.example.notespote.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.notespote.R
import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.model.Carpeta
import com.example.notespote.presentation.components.cards.NoteCard
import com.example.notespote.presentation.components.cards.NoteCardData
import com.example.notespote.presentation.theme.OutfitFamily
import com.example.notespote.viewModel.ApunteViewModel
import com.example.notespote.viewModel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyProfileView(
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    apunteViewModel: ApunteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Todo", "Notas", "Carpetas")
    var apunteToDelete by remember { mutableStateOf<Apunte?>(null) }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    val user = uiState.user

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp)
            .padding(top = 80.dp, bottom = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Atrás",
                tint = Color.White,
                modifier = Modifier.clickable { onBackClick() }
            )
            Text("@${user?.nombreUsuario ?: "Usuario"}", color = Color.White, fontFamily = OutfitFamily)
            Spacer(modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto de perfil
            if (user?.fotoPerfil != null) {
                AsyncImage(
                    model = user.fotoPerfil,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFF80DEEA), CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .border(2.dp, Color(0xFF80DEEA), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Sin foto",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))


            val totalNotes = uiState.publicNotes.size + uiState.privateNotes.size
            ProfileStat(totalNotes.toString(), "Notas")
            Spacer(modifier = Modifier.width(32.dp))
            ProfileStat(uiState.followerCount.toString(), "Seguidores")
            Spacer(modifier = Modifier.width(32.dp))
            ProfileStat(uiState.followingCount.toString(), "Seguidos")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            user?.nombreCompleto ?: "Usuario",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            fontFamily = OutfitFamily
        )
        Text(
            user?.biografia ?: "Sin biografía",
            color = Color.Gray,
            fontSize = 12.sp,
            fontFamily = OutfitFamily
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onEditProfileClick() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
        ) {
            Text("Editar Perfil", color = Color.White, fontFamily = OutfitFamily)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Black,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    height = 2.dp,
                    color = Color(0xFFD0ADF0)
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            title,
                            color = if (selectedTabIndex == index) Color.White else Color.Gray,
                            fontFamily = OutfitFamily
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Content based on tab
        when (selectedTabIndex) {
            0 -> TodoTab(
                uiState.publicFolders,
                uiState.publicNotes,
                uiState.privateNotes,
                onDeleteNote = { apunteToDelete = it }
            )
            1 -> NotasTab(
                uiState.publicNotes,
                uiState.privateNotes,
                onDeleteNote = { apunteToDelete = it }
            )
            2 -> CarpetasTab(uiState.publicFolders)
        }
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
private fun TodoTab(
    publicFolders: List<Carpeta>,
    publicNotes: List<Apunte>,
    privateNotes: List<Apunte>,
    onDeleteNote: (Apunte) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (publicFolders.isNotEmpty() || publicNotes.isNotEmpty()) {
            item {
                Text(
                    "Públicas",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = OutfitFamily
                )
            }
            items(publicFolders) { folder ->
                FolderItem(folder = folder)
            }
            items(publicNotes) { note ->
                NoteCard(
                    note = note.toNoteCardData(),
                    onLongClick = { onDeleteNote(note) }
                )
            }
        }

        if (privateNotes.isNotEmpty()) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Text(
                    "Privadas",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = OutfitFamily
                )
            }
            items(privateNotes) { note ->
                NoteCard(
                    note = note.toNoteCardData(),
                    onLongClick = { onDeleteNote(note) }
                )
            }
        }

        if (publicFolders.isEmpty() && publicNotes.isEmpty() && privateNotes.isEmpty()) {
            item {
                Text(
                    "No tienes contenido aún",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontFamily = OutfitFamily,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun NotasTab(
    publicNotes: List<Apunte>,
    privateNotes: List<Apunte>,
    onDeleteNote: (Apunte) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (publicNotes.isNotEmpty()) {
            item {
                Text(
                    "Públicas",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = OutfitFamily
                )
            }
            items(publicNotes) { note ->
                NoteCard(
                    note = note.toNoteCardData(),
                    onLongClick = { onDeleteNote(note) }
                )
            }
        }

        if (privateNotes.isNotEmpty()) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Text(
                    "Privadas",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = OutfitFamily
                )
            }
            items(privateNotes) { note ->
                NoteCard(
                    note = note.toNoteCardData(),
                    onLongClick = { onDeleteNote(note) }
                )
            }
        }

        if (publicNotes.isEmpty() && privateNotes.isEmpty()) {
            item {
                Text(
                    "No tienes notas aún",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontFamily = OutfitFamily,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun CarpetasTab(publicFolders: List<Carpeta>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (publicFolders.isNotEmpty()) {
            item {
                Text(
                    "Públicas",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = OutfitFamily
                )
            }
            items(publicFolders) { folder ->
                FolderItem(folder = folder)
            }
        } else {
            item {
                Text(
                    "No tienes carpetas aún",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontFamily = OutfitFamily,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            fontFamily = OutfitFamily
        )
        Text(
            label,
            color = Color.Gray,
            fontSize = 11.sp,
            fontFamily = OutfitFamily
        )
    }
}

@Composable
private fun FolderItem(folder: Carpeta) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Default.Folder,
            contentDescription = null,
            tint = parseColorFromHex(folder.colorCarpeta),
            modifier = Modifier.size(32.dp)
        )
        Text(folder.nombreCarpeta, color = Color.White, fontFamily = OutfitFamily)
    }
}

private fun parseColorFromHex(colorString: String?): Color {
    return try {
        if (colorString.isNullOrBlank()) {
            Color(0xFF90EE90)
        } else {
            val cleanColor = colorString.removePrefix("#")
            Color(android.graphics.Color.parseColor("#$cleanColor"))
        }
    } catch (e: Exception) {
        Color(0xFF90EE90)
    }
}

private fun Apunte.toNoteCardData(): NoteCardData {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = dateFormat.format(Date(this.fechaCreacion))

    return NoteCardData(
        title = this.titulo,
        description = "Sin descripción",
        tags = emptyList(),
        date = date,
        isPublic = this.tipoVisibilidad == com.example.notespote.data.local.entities.TipoVisibilidad.PUBLICO,
        subject = this.idMateria ?: "Sin materia"
    )
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
