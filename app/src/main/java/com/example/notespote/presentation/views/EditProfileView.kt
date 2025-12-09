package com.example.notespote.presentation.views

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.R
import com.example.notespote.presentation.theme.OutfitFamily
import com.example.notespote.presentation.theme.RichBlack

@Composable
fun EditProfileView(onBackClick: () -> Unit) {
    var username by remember { mutableStateOf("Naimur") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RichBlack)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Atrás",
                tint = Color.White,
                modifier = Modifier.clickable { onBackClick() }
            )
            Text(
                "Editar Perfil",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                fontFamily = OutfitFamily,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Profile Image
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.mascot_notespot), // Placeholder
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFF80DEEA), CircleShape),
                    contentScale = ContentScale.Crop
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar foto",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-8).dp, y = (-8).dp)
                        .background(Color(0xFF6200EE), CircleShape)
                        .padding(8.dp)
                        .clickable { /* TODO: Handle image picking */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Username Field
        Text("Username", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = OutfitFamily)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.White, RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            textStyle = TextStyle(fontFamily = OutfitFamily, color = Color.White)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Save Button
        Button(
            onClick = { /* TODO: Save changes */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
        ) {
            Text("Guardar cambios", color = Color.Black, fontWeight = FontWeight.Bold, fontFamily = OutfitFamily)
        }
    }
}
