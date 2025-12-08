package com.example.notespote.presentation.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.domain.model.Apunte
import com.example.notespote.presentation.theme.OutfitFamily
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SimpleNoteCard(
    apunte: Apunte,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = try {
        dateFormat.format(Date(apunte.fechaCreacion))
    } catch (e: Exception) {
        "Fecha desconocida"
    }

    Box(
        modifier = Modifier
            .width(160.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF2C2C2C))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Título
                Text(
                    text = apunte.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = OutfitFamily,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Contenido preview
                if (!apunte.contenido.isNullOrBlank()) {
                    Text(
                        text = apunte.contenido,
                        fontSize = 12.sp,
                        fontFamily = OutfitFamily,
                        color = Color.Gray,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Indicador de imagen
                if (apunte.tieneImagenes) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Tiene imágenes",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Contiene imágenes",
                            fontSize = 10.sp,
                            fontFamily = OutfitFamily,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Fecha
            Text(
                text = formattedDate,
                fontSize = 10.sp,
                fontFamily = OutfitFamily,
                color = Color.Gray
            )
        }
    }
}
