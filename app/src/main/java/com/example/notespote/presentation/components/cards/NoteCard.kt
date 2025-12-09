package com.example.notespote.presentation.components.cards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.presentation.components.common.HashtagChip
import com.example.notespote.presentation.components.common.InfoChip
import com.example.notespote.presentation.theme.OutfitFamily

data class NoteCardData(
    val title: String,
    val description: String,
    val tags: List<Pair<String, Color>>,
    val subject: String,
    val date: String,
    val isPublic: Boolean
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: NoteCardData,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onFavoriteClick: (() -> Unit)? = null,
    isFavorito: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp)
            .let { modifier ->
                if (onClick != null || onLongClick != null) {
                    modifier.combinedClickable(
                        onClick = { onClick?.invoke() },
                        onLongClick = { onLongClick?.invoke() }
                    )
                } else {
                    modifier
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Column for Text Content
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            // Top Row: Title, Hashtags, Favorite, More Icon
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(note.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = OutfitFamily)
                note.tags.forEach { (tag, color) ->
                    HashtagChip(text = tag, color = color)
                }
                Spacer(modifier = Modifier.weight(1f))
                if (onFavoriteClick != null) {
                    IconButton(
                        onClick = { onFavoriteClick.invoke() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                            tint = if (isFavorito) Color(0xFFFF6B6B) else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Icon(Icons.Default.MoreVert, contentDescription = "Más opciones", tint = Color.White, modifier = Modifier.size(16.dp))
            }
            // Description
            Text(note.description, color = Color.Gray, fontSize = 12.sp, fontFamily = OutfitFamily)

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Row: Subject, Date, Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Subject
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFFFFB347), modifier = Modifier.size(12.dp))
                    Text(note.subject, fontSize = 10.sp, color = Color(0xFFFFB347), fontFamily = OutfitFamily)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Date and Status Chips
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoChip(
                        text = note.date,
                        color = Color(0xFFFFB347),
                        icon = Icons.Default.CalendarToday
                    )
                    InfoChip(
                        text = if (note.isPublic) "Público" else "Privado",
                        color = if (note.isPublic) Color(0xFF90EE90) else Color.Gray,
                        icon = null // No icon for status
                    )
                }
            }
        }
    }
}
