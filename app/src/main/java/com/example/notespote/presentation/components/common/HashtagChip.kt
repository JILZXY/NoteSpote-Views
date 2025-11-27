package com.example.notespote.presentation.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.presentation.theme.OutfitFamily

@Composable
fun HashtagChip(text: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(width = 1.dp, color = color, shape = RoundedCornerShape(15.dp))
            .background(color = color.copy(alpha = 0.2f), shape = RoundedCornerShape(15.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "# $text",
            style = TextStyle(
                fontSize = 8.sp,
                fontFamily = OutfitFamily,
                fontWeight = FontWeight(500),
                color = color
            )
        )
    }
}
