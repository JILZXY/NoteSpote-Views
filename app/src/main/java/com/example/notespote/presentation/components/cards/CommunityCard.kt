package com.example.notespote.presentation.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.notespote.presentation.components.common.HashtagChip
import com.example.notespote.presentation.components.common.InfoChip
import com.example.notespote.presentation.theme.OutfitFamily

data class CommunityCardData(
    val subject: String,
    val noteImageResId: Int,
    val title: String,
    val description: String,
    val tags: List<Pair<String, Color>>,
    val authorName: String,
    val authorImageResId: Int,
    val date: String
)

@Composable
fun CommunityCard(card: CommunityCardData, onAuthorClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF333333))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFFFFB347))
                Text(card.subject, color = Color.White, fontWeight = FontWeight.Bold, fontFamily = OutfitFamily)
            }

            // Note Image
            Image(
                painter = painterResource(id = card.noteImageResId),
                contentDescription = "Contenido del apunte",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            // Content
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(card.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = OutfitFamily)
                Text(card.description, color = Color.Gray, fontSize = 12.sp, fontFamily = OutfitFamily)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    card.tags.forEach { (tag, color) ->
                        HashtagChip(text = tag, color = color)
                    }
                }
            }

            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickable { onAuthorClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = card.authorImageResId),
                        contentDescription = "Autor",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(card.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = OutfitFamily)
                        InfoChip(text = card.date, color = Color(0xFFFFB347), icon = Icons.Default.CalendarToday)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Me gusta", tint = Color.Red)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.ThumbUp, contentDescription = "Descargar", tint = Color.White)
                }
            }
        }
    }
}
