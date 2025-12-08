package com.example.notespote.presentation.views

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.notespote.presentation.theme.OutfitFamily
import com.example.notespote.viewModel.EditProfileViewModel

@Composable
fun ProfileView(
    onSignOutClick: () -> Unit,
    onAccountDataClick: () -> Unit,
    onMyProfileClick: () -> Unit,
    onEditProfileImageClick: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(start = 32.dp, end = 32.dp, top = 80.dp, bottom = 32.dp)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Foto de perfil
                Box {
                    if (uiState.fotoPerfilUri != null || uiState.fotoPerfilUrl != null) {
                        AsyncImage(
                            model = uiState.fotoPerfilUri ?: uiState.fotoPerfilUrl,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color(0xFF80DEEA), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                                .border(2.dp, Color(0xFF80DEEA), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Sin foto",
                                tint = Color.White,
                                modifier = Modifier.size(75.dp)
                            )
                        }
                    }

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
                    text = uiState.nombreUsuario.ifBlank { "Usuario" },
                    fontFamily = OutfitFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )

                if (uiState.nombre.isNotBlank() || uiState.apellido.isNotBlank()) {
                    Text(
                        text = "${uiState.nombre} ${uiState.apellido}".trim(),
                        fontFamily = OutfitFamily,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = Icons.Outlined.Person, contentDescription = null, tint = Color.White)
            Text(text = text, color = Color.White, fontFamily = OutfitFamily)
        }
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
    }
}
