package com.example.notespote.presentation.components.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.R
import com.example.notespote.presentation.theme.OutfitFamily

data class FolderCardData(
    val title: String,
    val color: Color,
    val overlayIcon: ImageVector? = null // Optional overlay icon
)

@Composable
fun FolderCard(folder: FolderCardData) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(width = 130.dp, height = 100.dp)
                .shadow(elevation = 4.dp, spotColor = Color(0x40000000), ambientColor = Color(0x40000000), clip = false),
            contentAlignment = Alignment.Center
        ) {
            // Base folder shape from drawable
            Icon(
                painter = painterResource(id = R.drawable.ic_folder),
                contentDescription = folder.title,
                tint = folder.color,
                modifier = Modifier.matchParentSize()
            )
            // Overlay icon if it exists
            if (folder.overlayIcon != null) {
                Icon(
                    imageVector = folder.overlayIcon,
                    contentDescription = null, // Decorative
                    tint = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(folder.title, color = Color.White, fontSize = 12.sp, fontFamily = OutfitFamily, fontWeight = FontWeight.SemiBold)
    }
}
