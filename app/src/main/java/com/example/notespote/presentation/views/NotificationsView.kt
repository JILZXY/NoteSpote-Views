package com.example.notespote.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.presentation.theme.OutfitFamily
import com.example.notespote.presentation.theme.RichBlack

@Composable
fun NotificationsView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RichBlack)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notificaciones",
                tint = Color.White,
                modifier = Modifier.height(100.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "No hay notificaciones nuevas",
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Cuando alguien interactúe con tus apuntes, lo verás aquí.",
                fontFamily = OutfitFamily,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
