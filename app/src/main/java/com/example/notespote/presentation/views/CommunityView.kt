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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notespote.R
import com.example.notespote.data.local.entities.TipoVisibilidad
import com.example.notespote.domain.model.Apunte
import com.example.notespote.presentation.components.cards.CommunityCard
import com.example.notespote.presentation.components.cards.CommunityCardData
import com.example.notespote.presentation.theme.OutfitFamily
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.viewModel.CommunityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityView(
    onAuthorClick: () -> Unit,
    onNoteClick: (String) -> Unit = {},
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilters by remember { mutableStateOf(false) }
    val filterChips = listOf("Todos", "Apuntes", "Biología", "Matemáticas", "Física")

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
            Text("Comunidad", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = OutfitFamily)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar and Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Buscar en la comunidad...", color = Color.Gray) },
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

        Spacer(modifier = Modifier.height(16.dp))

        // Results Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Apuntes de la comunidad",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                fontFamily = OutfitFamily
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error message
        uiState.error?.let { error ->
            Text(
                text = "Error: $error",
                color = Color.Red,
                fontSize = 14.sp,
                fontFamily = OutfitFamily
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Community Posts
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(uiState.apuntes) { apunte ->
                Box(
                    modifier = Modifier.clickable { onNoteClick(apunte.id) }
                ) {
                    CommunityCard(
                        card = apunte.toCommunityCardData(),
                        onAuthorClick = onAuthorClick
                    )
                }
            }

            // No results message
            if (!uiState.isLoading && uiState.apuntes.isEmpty() && uiState.searchQuery.isNotBlank()) {
                item {
                    Text(
                        text = "No se encontraron apuntes públicos para '${uiState.searchQuery}'",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontFamily = OutfitFamily,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Empty state
            if (!uiState.isLoading && uiState.apuntes.isEmpty() && uiState.searchQuery.isBlank()) {
                item {
                    Text(
                        text = "No hay apuntes públicos disponibles",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontFamily = OutfitFamily,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

// Función auxiliar para convertir Apunte a CommunityCardData
private fun Apunte.toCommunityCardData(): CommunityCardData {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = dateFormat.format(Date(this.fechaCreacion))

    return CommunityCardData(
        subject = this.idMateria ?: "Sin materia",
        noteImageResId = R.drawable.mascot_notespot, // Placeholder
        title = this.titulo,
        description = this.contenido?.take(100) ?: "Sin descripción",
        tags = emptyList(), // TODO: Cargar etiquetas reales
        authorName = "Usuario", // TODO: Cargar nombre de usuario real
        authorImageResId = R.drawable.mascot_notespot, // Placeholder
        date = date
    )
}
