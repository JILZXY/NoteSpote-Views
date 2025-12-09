package com.example.notespote.presentation.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.presentation.theme.OutfitFamily

@Composable
fun TagChip(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    hasBorder: Boolean = false
) {
    val chipModifier = if (hasBorder) {
        modifier
            .border(width = 1.dp, color = color, shape = RoundedCornerShape(15.dp))
            .background(color = color.copy(alpha = 0.2f), shape = RoundedCornerShape(15.dp))
    } else {
        modifier.background(color = color, shape = RoundedCornerShape(15.dp))
    }

    val textColor = if (hasBorder) color else Color.Black

    Row(
        modifier = chipModifier.padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
        }
        Text(
            text = if(hasBorder) "# $text" else text,
            style = TextStyle(
                fontSize = 8.sp,
                fontFamily = OutfitFamily,
                fontWeight = FontWeight(500),
                color = textColor
            )
        )
    }
}
