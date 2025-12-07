package com.example.notespote.presentation.mappers

import androidx.compose.ui.graphics.Color
import com.example.notespote.R
import com.example.notespote.data.local.entities.TipoVisibilidad
import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.model.Carpeta
import com.example.notespote.presentation.components.cards.FolderCardData
import com.example.notespote.presentation.components.cards.NoteCardData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Apunte.toNoteCardData(): NoteCardData {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = Date(fechaCreacion)
    return NoteCardData(
        title = titulo,
        description = contenido ?: "",
        tags = emptyList(), // We'll add tag support later
        subject = idMateria ?: "Sin materia",
        date = sdf.format(date),
        isPublic = tipoVisibilidad == TipoVisibilidad.PUBLICO,
        imageResId = if (tieneImagenes) R.drawable.mascot_notespot else R.drawable.mascot_notespot
    )
}

fun Carpeta.toFolderCardData(): FolderCardData {
    val color = try {
        Color(android.graphics.Color.parseColor(colorCarpeta))
    } catch (e: Exception) {
        Color.Gray
    }
    return FolderCardData(
        title = nombreCarpeta,
        color = color,
        overlayIcon = null
    )
}
