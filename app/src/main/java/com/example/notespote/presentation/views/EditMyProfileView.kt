package com.example.notespote.presentation.views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.notespote.presentation.theme.OutfitFamily
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.viewModel.EditProfileViewModel

@Composable
fun EditMyProfileView(
    onBackClick: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val passwordState by viewModel.passwordState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onProfilePhotoSelected(it) }
    }

    // Mostrar mensajes de éxito/error
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(RichBlack)
                .padding(horizontal = 16.dp)
                .padding(top = 80.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState())
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

            // Foto de perfil
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(120.dp)
            ) {
                // Imagen actual o placeholder
                if (uiState.fotoPerfilUri != null || uiState.fotoPerfilUrl != null) {
                    AsyncImage(
                        model = uiState.fotoPerfilUri ?: uiState.fotoPerfilUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFF80DEEA), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                            .border(2.dp, Color(0xFF80DEEA), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Sin foto",
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                // Botón de cámara
                IconButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(40.dp)
                        .background(Color(0xFF8BC34A), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Cambiar foto",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Nombre
            ProfileTextField(
                label = "Nombre",
                value = uiState.nombre,
                onValueChange = { viewModel.onNombreChange(it) },
                placeholder = "Tu nombre"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Apellido
            ProfileTextField(
                label = "Apellido",
                value = uiState.apellido,
                onValueChange = { viewModel.onApellidoChange(it) },
                placeholder = "Tu apellido"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Username
            ProfileTextField(
                label = "Nombre de usuario",
                value = uiState.nombreUsuario,
                onValueChange = { viewModel.onNombreUsuarioChange(it) },
                placeholder = "nombreusuario"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            ProfileTextField(
                label = "Correo electrónico",
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                placeholder = "correo@ejemplo.com"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bio
            Text("Biografía", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = OutfitFamily)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = uiState.biografia,
                onValueChange = { viewModel.onBiografiaChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
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
                textStyle = TextStyle(fontFamily = OutfitFamily, color = Color.White),
                placeholder = { Text("Cuéntanos sobre ti", color = Color.Gray, fontFamily = OutfitFamily) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de cambiar contraseña
            OutlinedButton(
                onClick = { viewModel.showPasswordDialog() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
            ) {
                Text("Cambiar contraseña", fontFamily = OutfitFamily)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = { viewModel.saveProfile() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        "Guardar cambios",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontFamily = OutfitFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Diálogo de cambiar contraseña
    if (uiState.showPasswordDialog) {
        ChangePasswordDialog(
            currentPassword = passwordState.currentPassword,
            newPassword = passwordState.newPassword,
            confirmPassword = passwordState.confirmPassword,
            isLoading = passwordState.isLoading,
            error = passwordState.error,
            onCurrentPasswordChange = { viewModel.onCurrentPasswordChange(it) },
            onNewPasswordChange = { viewModel.onNewPasswordChange(it) },
            onConfirmPasswordChange = { viewModel.onConfirmPasswordChange(it) },
            onDismiss = { viewModel.hidePasswordDialog() },
            onConfirm = { viewModel.changePassword() }
        )
    }
}

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontFamily = OutfitFamily)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
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
            textStyle = TextStyle(fontFamily = OutfitFamily, color = Color.White),
            placeholder = { Text(placeholder, color = Color.Gray, fontFamily = OutfitFamily) }
        )
    }
}

@Composable
private fun ChangePasswordDialog(
    currentPassword: String,
    newPassword: String,
    confirmPassword: String,
    isLoading: Boolean,
    error: String?,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Cambiar contraseña", fontFamily = OutfitFamily, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                if (error != null) {
                    Text(error, color = Color.Red, fontSize = 12.sp, fontFamily = OutfitFamily)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                TextField(
                    value = currentPassword,
                    onValueChange = onCurrentPasswordChange,
                    label = { Text("Contraseña actual", fontFamily = OutfitFamily) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = newPassword,
                    onValueChange = onNewPasswordChange,
                    label = { Text("Nueva contraseña", fontFamily = OutfitFamily) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = { Text("Confirmar contraseña", fontFamily = OutfitFamily) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text("Cambiar", fontFamily = OutfitFamily)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", fontFamily = OutfitFamily)
            }
        }
    )
}
