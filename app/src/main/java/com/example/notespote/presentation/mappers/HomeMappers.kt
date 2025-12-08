package com.example.notespote.presentation.mappers

import androidx.compose.ui.graphics.Color
import com.example.notespote.R
import com.example.notespote.data.local.entities.TipoVisibilidad
import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.model.ApunteDetallado
import com.example.notespote.domain.model.Carpeta
import com.example.notespote.presentation.components.cards.FolderCardData
import com.example.notespote.presentation.components.cards.NoteCardData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

fun Apunte.toNoteCardData(): NoteCardData {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = Date(fechaCreacion)
    return NoteCardData(
        title = titulo,
        description = "Sin descripción",
        tags = emptyList(), // Tags are loaded in detail view
        subject = idMateria ?: "Sin materia",
        date = sdf.format(date),
        isPublic = tipoVisibilidad == TipoVisibilidad.PUBLICO
    )
}

fun ApunteDetallado.toNoteCardData(): NoteCardData {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = Date(apunte.fechaCreacion)
    return NoteCardData(
        title = apunte.titulo,
        description = "Sin descripción",
        tags = etiquetas.map { it.nombreEtiqueta to Color(
            Random.nextInt(256),
            Random.nextInt(256),
            Random.nextInt(256)
        ) },
        subject = apunte.idMateria ?: "Sin materia",
        date = sdf.format(date),
        isPublic = apunte.tipoVisibilidad == TipoVisibilidad.PUBLICO
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
