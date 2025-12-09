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
import com.example.notespote.domain.model.Carpeta
import com.example.notespote.presentation.components.cards.NoteCard
import com.example.notespote.presentation.components.cards.NoteCardData
import com.example.notespote.presentation.components.search.FolderSearchResultItem
import com.example.notespote.presentation.theme.OutfitFamily
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.viewModel.SearchViewModel
import com.example.notespote.viewModel.states.SearchFilter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilters by remember { mutableStateOf(false) }

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
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
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
                    DropdownMenuItem(
                        text = { Text("Solo Carpetas", fontFamily = OutfitFamily) },
                        onClick = {
                            viewModel.toggleFilter(SearchFilter.CARPETAS)
                            showFilters = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Solo Apuntes", fontFamily = OutfitFamily) },
                        onClick = {
                            viewModel.toggleFilter(SearchFilter.APUNTES)
                            showFilters = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Solo con imágenes", fontFamily = OutfitFamily) },
                        onClick = {
                            viewModel.toggleFilter(SearchFilter.SOLO_IMAGENES)
                            showFilters = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Solo con Post-its", fontFamily = OutfitFamily) },
                        onClick = {
                            viewModel.toggleFilter(SearchFilter.SOLO_POSTITS)
                            showFilters = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Chips - Mostrar filtros activos
        if (uiState.selectedFilters.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.selectedFilters.toList()) { filter ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                            .clickable { viewModel.toggleFilter(filter) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = when (filter) {
                                SearchFilter.CARPETAS -> "Carpetas"
                                SearchFilter.APUNTES -> "Apuntes"
                                SearchFilter.SOLO_IMAGENES -> "Con imágenes"
                                SearchFilter.SOLO_POSTITS -> "Con Post-its"
                            },
                            color = Color.White,
                            fontSize = 12.sp,
                            fontFamily = OutfitFamily
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Results
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Resultados",
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

        // Mostrar error si existe
        uiState.error?.let { error ->
            Text(
                text = "Error: $error",
                color = Color.Red,
                fontSize = 14.sp,
                fontFamily = OutfitFamily
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Lista de resultados
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Mostrar carpetas
            items(uiState.carpetas) { carpeta ->
                FolderSearchResultItem(
                    name = carpeta.nombreCarpeta,
                    color = parseColor(carpeta.colorCarpeta)
                )
            }

            // Mostrar apuntes
            items(uiState.apuntes) { apunte ->
                NoteCard(note = apunte.toNoteCardData())
            }

            // Mensaje si no hay resultados
            if (!uiState.isLoading &&
                uiState.carpetas.isEmpty() &&
                uiState.apuntes.isEmpty() &&
                uiState.searchQuery.isNotBlank()) {
                item {
                    Text(
                        text = "No se encontraron resultados para '${uiState.searchQuery}'",
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

// Función auxiliar para convertir hex string a Color
private fun parseColor(colorString: String?): Color {
    return try {
        if (colorString.isNullOrBlank()) {
            Color(0xFF90EE90) // Color por defecto
        } else {
            val cleanColor = colorString.removePrefix("#")
            Color(android.graphics.Color.parseColor("#$cleanColor"))
        }
    } catch (e: Exception) {
        Color(0xFF90EE90)
    }
}

// Función auxiliar para convertir Apunte a NoteCardData
private fun Apunte.toNoteCardData(): NoteCardData {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = dateFormat.format(Date(this.fechaCreacion))

    return NoteCardData(
        title = this.titulo,
        description = this.descripcion ?: "Sin descripción",
        tags = emptyList(), // Las etiquetas se pueden cargar después si es necesario
        date = date,
        isPublic = this.tipoVisibilidad == TipoVisibilidad.PUBLICO,
        subject = this.idMateria ?: "Sin materia"
    )
}
