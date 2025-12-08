package com.example.notespote.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.R
import com.example.notespote.presentation.components.cards.NoteCard
import com.example.notespote.presentation.components.cards.NoteCardData
import com.example.notespote.presentation.theme.OutfitFamily

@Composable
fun UserProfileView(onBackClick: () -> Unit) {
    var selectedTabIndex by remember { mutableStateOf(0) } // Default to "Todo"
    val tabs = listOf("Todo", "Notas", "Carpetas")

    val publicNotes = listOf(
        NoteCardData("My Homework", "this is the text this is the ...", listOf("Ejercicios" to Color(0xFFB39DDB), "Cálculo integral" to Color(0xFFB39DDB)), "Matemáticas", "Ayer", true)
    )
    val publicFolders = listOf(
        FolderItemData("Apuntes de Física", Color(0xFF81D4FA)),
        FolderItemData("Cálculo Integral UII", Color(0xFF4DB6AC))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
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
            Text("@Naimurue", color = Color.White, fontFamily = OutfitFamily)
            Spacer(modifier = Modifier.size(24.dp)) // For alignment
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Info
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.mascot_notespot),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFF80DEEA), CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            ProfileStat("124", "Notas")
            ProfileStat("67", "Seguidores")
            ProfileStat("45", "Seguidos")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Naimur", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = OutfitFamily)
        Text("Compartiendo el conocimineto", color = Color.Gray, fontSize = 12.sp, fontFamily = OutfitFamily)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
        ) {
            Text("Seguir", color = Color.Black, fontFamily = OutfitFamily, fontWeight = FontWeight.Bold)
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
                    text = { Text(title, color = if(selectedTabIndex == index) Color.White else Color.Gray, fontFamily = OutfitFamily) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Content based on tab
        when (selectedTabIndex) {
            0 -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(publicFolders) { folder ->
                        FolderItem(data = folder)
                    }
                    items(publicNotes) { note ->
                        NoteCard(note = note)
                    }
                }
            }
            1 -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(publicNotes) { note ->
                        NoteCard(note = note)
                    }
                }
            }
            2 -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(publicFolders) { folder ->
                        FolderItem(data = folder)
                    }
                }
            }
        }
    }
}

data class FolderItemData(val name: String, val color: Color)

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = OutfitFamily)
        Text(label, color = Color.Gray, fontSize = 12.sp, fontFamily = OutfitFamily)
    }
}

@Composable
private fun FolderItem(data: FolderItemData) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(Icons.Default.Folder, contentDescription = null, tint = data.color, modifier = Modifier.size(32.dp))
        Text(data.name, color = Color.White, fontFamily = OutfitFamily)
    }
}
