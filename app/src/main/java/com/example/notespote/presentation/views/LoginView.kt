package com.example.notespote.presentation.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notespote.ui.theme.NoteSpoteTheme

@Composable
fun LoginView() {
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Use Surface to correctly apply background color from the theme
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Center content vertically
        ) {
            // Spacer to push content from the top, adjust as needed
            Spacer(modifier = Modifier.weight(0.5f))

            TitleSection(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Ingresa tus datos",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = emailOrUsername, onValueChange = { emailOrUsername = it },
                label = { Text("Usuario o correo electrónico") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = "Correo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(value = password, onValueChange = { password = it })

            Spacer(modifier = Modifier.height(40.dp))

            PrimaryButton(text = "Inicia Sesión", onClick = { println("Botón Login presionado") })

            Spacer(modifier = Modifier.weight(1f))

            BottomRegistrationLink(
                modifier = Modifier.padding(bottom = 32.dp),
                onRegisterClick = { println("Navegar a Sign Up") }
            )
        }
    }
}

@Composable
private fun PasswordField(value: String, onValueChange: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text("Contraseña") },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = "Candado") },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            // Correct icons for visibility toggle
            val image = if (passwordVisible) {
                Icons.Filled.Info
            } else {
                Icons.Filled.Info
            }
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = "Mostrar/Ocultar Contraseña")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun BottomRegistrationLink(modifier: Modifier = Modifier, onRegisterClick: () -> Unit) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "¿No tienes cuenta?", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Regístrate",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.clickable(onClick = onRegisterClick)
        )
    }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(text = text, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun TitleSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "NoteSpote",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Captura tus ideas",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}


