package com.example.notespote.presentation.views

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespote.ui.theme.*
@Composable
fun SignupView() {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleSection(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Registra tu cuenta",
            color = TextWhite.copy(alpha = 0.7f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        //Campo del formulario de inicio de sesión
        RegistrationTextField(value = username, onValueChange = { username = it }, label = "Usuario", leadingIcon = Icons.Default.Person)
        Spacer(modifier = Modifier.height(16.dp))
        RegistrationTextField(value = email, onValueChange = { email = it }, label = "Correo electrónico", leadingIcon = Icons.Default.Email, keyboardType = KeyboardType.Email)
        Spacer(modifier = Modifier.height(16.dp))
        PasswordRegistrationField(value = password, onValueChange = { password = it }, label = "Contraseña")
        Spacer(modifier = Modifier.height(16.dp))
        PasswordRegistrationField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = "Confirma tu contraseña")

        Spacer(modifier = Modifier.height(40.dp))

        // Botón
        PrimaryButton(text = "Regístrate", onClick = { println("Botón Sign Up presionado") })

        Spacer(modifier = Modifier.weight(1f))

        // Enlace de Login
        BottomLoginLink(
            modifier = Modifier.padding(bottom = 32.dp),
            onLoginClick = { println("Navegar a Login") }
        )
    }
}


@Composable
private fun RegistrationTextField(
    value: String, onValueChange: (String) -> Unit, label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector, keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, color = TextWhite.copy(alpha = 0.5f)) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryColor, unfocusedBorderColor = TextWhite.copy(alpha = 0.5f),
            cursorColor = PrimaryColor, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite
        ),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = "Icono de $label", tint = TextWhite.copy(alpha = 0.7f)) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth()
    )
}



@Composable
private fun PasswordRegistrationField(value: String, onValueChange: (String) -> Unit, label: String) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label, color = TextWhite.copy(alpha = 0.5f)) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryColor, unfocusedBorderColor = TextWhite.copy(alpha = 0.5f),
            cursorColor = PrimaryColor, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite
        ),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Candado", tint = TextWhite.copy(alpha = 0.7f)) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Info else Icons.Filled.Info
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = "Mostrar/Ocultar", tint = TextWhite.copy(alpha = 0.7f))
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun BottomLoginLink(modifier: Modifier = Modifier, onLoginClick: () -> Unit) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "¿Ya tienes cuenta?", color = TextWhite.copy(alpha = 0.7f), fontSize = 16.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Inicia Sesión",
            color = PrimaryColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable(onClick = onLoginClick)
        )
    }
}




@Preview(showBackground = true, name = "Signup View Dark")
@Composable
fun SignupViewPreview() {
    NoteSpoteTheme { SignupView() }
}