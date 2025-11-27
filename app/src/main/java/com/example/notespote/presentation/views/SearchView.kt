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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.R
import com.example.notespote.presentation.components.cards.NoteCard
import com.example.notespote.presentation.components.cards.NoteCardData
import com.example.notespote.presentation.components.search.FolderSearchResultItem
import com.example.notespote.presentation.theme.OutfitFamily
import com.example.notespote.presentation.theme.RichBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView() {
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    val filterChips = listOf("Carpetas", "Apuntes", "Biología", "Matemáticas", "Física")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RichBlack)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // You can reuse the user profile header from HomeView if you want
            Text("Buscador", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = OutfitFamily)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar and Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar apuntes, carpetas...", color = Color.Gray) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .border(1.dp, Color.White, RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Box {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filtros",
                    tint = Color.White,
                    modifier = Modifier.clickable { showFilters = true }
                )
                DropdownMenu(
                    expanded = showFilters,
                    onDismissRequest = { showFilters = false },
                    modifier = Modifier.background(Color.White, RoundedCornerShape(12.dp))
                ) {
                    Text("Filtros", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(12.dp), fontFamily = OutfitFamily)
                    DropdownMenuItem(text = { Text("Tipo de archivo", fontFamily = OutfitFamily) }, onClick = { /*TODO*/ })
                    DropdownMenuItem(text = { Text("Fecha de creación", fontFamily = OutfitFamily) }, onClick = { /*TODO*/ })
                    DropdownMenuItem(text = { Text("Materia", fontFamily = OutfitFamily) }, onClick = { /*TODO*/ })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filterChips) { chipText ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(chipText, color = Color.White, fontSize = 12.sp, fontFamily = OutfitFamily)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Results
        Text("Resultados", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = OutfitFamily)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { FolderSearchResultItem("Apuntes de Física", Color(0xFF90EE90)) }
            item { FolderSearchResultItem("Cálculo Integral UII", Color(0xFFF19CFF)) }
            item {
                NoteCard(
                    note = NoteCardData(
                        title = "My Homework",
                        description = "this is the text this is the ...",
                        tags = listOf("Ejercicios" to Color(0xFFB39DDB), "Cálculo integral" to Color(0xFFB39DDB)),
                        date = "Ayer",
                        isPublic = true,
                        subject = "Matemáticas",
                        imageResId = R.drawable.mascot_notespot // Placeholder
                    )
                )
            }
            item {
                NoteCard(
                    note = NoteCardData(
                        title = "My Homework",
                        description = "this is the text this is the ...",
                        tags = listOf("Apunte" to Color(0xFF81D4FA)),
                        date = "Ayer",
                        isPublic = false,
                        subject = "Notas",
                        imageResId = R.drawable.mascot_notespot // Placeholder
                    )
                )
            }
        }
    }
}
