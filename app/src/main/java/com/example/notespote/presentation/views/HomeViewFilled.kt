package com.example.notespote.presentation.views

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.R
import com.example.notespote.presentation.components.cards.FolderCard
import com.example.notespote.presentation.components.cards.FolderCardData
import com.example.notespote.presentation.components.cards.NoteCard
import com.example.notespote.presentation.components.cards.NoteCardData
import com.example.notespote.presentation.theme.Celeste
import com.example.notespote.presentation.theme.OutfitFamily
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.presentation.theme.SyneMonoFamily

@Composable
fun HomeViewFilled(
    userName: String = "Naimur",
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAddNoteClick: () -> Unit,
    onCreateFolderClick: () -> Unit,
    onSeeAllFoldersClick: () -> Unit,
    onFolderClick: () -> Unit
) {
    val recentFolders = listOf(
        FolderCardData("Recientes", Color(0xFF97DECC), Icons.Default.History),
        FolderCardData("Favoritos", Color(0xFFFFF347), Icons.Default.Star),
        FolderCardData("Todos los archivos", Color(0xFFFD99FF), Icons.Default.Folder)
    )

    val recentNotes = listOf(
        NoteCardData("My Homework", "this is the text this is the ...", listOf("Ejercicios" to Color(0xFFB39DDB), "Cálculo integral" to Color(0xFFB39DDB)), "Matemáticas", "Ayer", true, R.drawable.mascot_notespot),
        NoteCardData("My Homework", "this is the text this is the ...", listOf("Apunte" to Color(0xFF81D4FA)), "Notas", "Ayer", false, R.drawable.mascot_notespot),
        NoteCardData("My Homework", "this is the text this is the ...", listOf("Ejercicios" to Color(0xFFB39DDB)), "Biología", "Ayer", false, R.drawable.mascot_notespot)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RichBlack)
            .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
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
                    Text(text = userName.first().toString(), fontFamily = OutfitFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = RichBlack)
                }

                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(text = "Hola, $userName", fontFamily = OutfitFamily, fontWeight = FontWeight.Bold, fontSize = 27.sp, color = Color.White)
                    Text(text = "Vamos a explorar tus apuntes", fontFamily = SyneMonoFamily, fontSize = 13.sp, color = Color.Gray)
                }
            }

            IconButton(onClick = onNotificationsClick) {
                Icon(imageVector = Icons.Outlined.Notifications, contentDescription = "Notificaciones", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Escritorio", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold, fontFamily = OutfitFamily)

        Spacer(modifier = Modifier.height(24.dp))

        // Folders Section
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Carpetas", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = OutfitFamily)
            TextButton(onClick = { onSeeAllFoldersClick() }) {
                Text("Ver todas", color = Color.Gray, fontSize = 12.sp, fontFamily = OutfitFamily)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(recentFolders) { folder ->
                Box(modifier = Modifier.clickable { onFolderClick() }) {
                    FolderCard(folder = folder)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Notes Section
        Text("Notas recientes", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = OutfitFamily)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(recentNotes) { note ->
                NoteCard(note = note)
            }
        }
    }
}
