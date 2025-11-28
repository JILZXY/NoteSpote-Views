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
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
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
import com.example.notespote.presentation.theme.OutfitFamily

@Composable
fun ProfileView(
    userName: String = "Naimur",
    onSignOutClick: () -> Unit,
    onAccountDataClick: () -> Unit,
    onMyProfileClick: () -> Unit,
    onEditProfileImageClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.mascot_notespot), // Placeholder image
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(150.dp)
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
                        .clickable { onEditProfileImageClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = userName,
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(48.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ProfileOption(text = "Datos de la cuenta", onClick = onAccountDataClick)
                ProfileOption(text = "Mi Perfil", onClick = onMyProfileClick)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Sign Out",
                color = Color(0xFFFF5800),
                fontFamily = OutfitFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSignOutClick() }
            )
        }
    }
}

@Composable
private fun ProfileOption(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, Color.White, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(imageVector = Icons.Outlined.Person, contentDescription = null, tint = Color.White)
            Text(text = text, color = Color.White, fontFamily = OutfitFamily)
        }
        Icon(imageVector = Icons.Default.ArrowForwardIos, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
    }
}
