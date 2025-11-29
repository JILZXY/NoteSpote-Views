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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.presentation.components.cards.FolderCard
import com.example.notespote.presentation.components.cards.FolderCardData
import com.example.notespote.presentation.theme.OutfitFamily
import com.example.notespote.presentation.theme.RichBlack

@Composable
fun AllFoldersView(onBackClick: () -> Unit, onFolderClick: () -> Unit) {
    val allFolders = listOf(
        FolderCardData("Recientes", Color(0xFF97DECC), Icons.Default.History),
        FolderCardData("Favoritos", Color(0xFFFFF347), Icons.Default.Star),
        FolderCardData("Todos los archivos", Color(0xFFFD99FF), Icons.Default.Folder),
        FolderCardData("Universidad", Color(0xFF81D4FA), Icons.Default.Folder),
        FolderCardData("Trabajo", Color(0xFF4DB6AC), Icons.Default.Folder),
        FolderCardData("Personal", Color(0xFFDCE775), Icons.Default.Folder)
    )

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
            items(allFolders) { folder ->
                FolderCard(folder = folder)
            }
        }
    }
}
