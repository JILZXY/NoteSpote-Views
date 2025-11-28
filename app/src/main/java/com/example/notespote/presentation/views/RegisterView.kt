package com.example.notespot.presentation.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespot.presentation.components.buttons.PrimaryButton
import com.example.notespote.presentation.components.inputs.EmailInput
import com.example.notespote.presentation.components.inputs.PasswordInput
import com.example.notespote.presentation.components.inputs.UserInput
import com.example.notespote.presentation.theme.Celeste
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.presentation.theme.SyneMonoFamily
import com.example.notespote.presentation.theme.UrbanistFamily
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegisterView(
    onRegisterSuccess: () -> Unit, // Renombrado para mayor claridad
    onLoginClick: () -> Unit
) {
    // Estados para los campos de entrada
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Obtenemos el contexto para mostrar mensajes (Toast)
    val context = LocalContext.current
    // Obtenemos la instancia de Firebase Auth
    val auth = remember { FirebaseAuth.getInstance() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RichBlack)
            .padding(horizontal = 32.dp)
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "¡Hola!",
            fontFamily = UrbanistFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 72.sp,
            color = Color.White
        )

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFFFFB347))) { append("B") }
                withStyle(style = SpanStyle(color = Color(0xFFFF9E9E))) { append("i") }
                withStyle(style = SpanStyle(color = Color(0xFFFFF599))) { append("e") }
                withStyle(style = SpanStyle(color = Color(0xFF91F48F))) { append("n") }
                withStyle(style = SpanStyle(color = Color(0xFF9EFFFF))) { append("v") }
                withStyle(style = SpanStyle(color = Color(0xFFFD99FF))) { append("e") }
                withStyle(style = SpanStyle(color = Color(0xFFB69CFF))) { append("n") }
                withStyle(style = SpanStyle(color = Color(0xFF624AF2))) { append("i") }
                withStyle(style = SpanStyle(color = Color(0xFFFCDDEC))) { append("d") }
                withStyle(style = SpanStyle(color = Color.White)) { append("o") }
            },
            fontFamily = UrbanistFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 72.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Registra tu cuenta",
            fontFamily = SyneMonoFamily,
            fontSize = 20.sp,
            color = Celeste
        )

        Spacer(modifier = Modifier.height(40.dp))

        UserInput(value = username, onValueChange = { username = it }, placeholder = "Usuario")
        Spacer(modifier = Modifier.height(20.dp))
        EmailInput(value = email, onValueChange = { email = it }, placeholder = "Correo electrónico")
        Spacer(modifier = Modifier.height(20.dp))
        PasswordInput(value = password, onValueChange = { password = it }, placeholder = "Contraseña")
        Spacer(modifier = Modifier.height(20.dp))
        PasswordInput(value = confirmPassword, onValueChange = { confirmPassword = it }, placeholder = "Confirma tu contraseña")
        Spacer(modifier = Modifier.height(40.dp))

        PrimaryButton(
            text = "Regístrate",
            onClick = {
                // 1. Validaciones básicas de los campos
                if (username.isBlank() || email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@PrimaryButton
                }
                if (password != confirmPassword) {
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    return@PrimaryButton
                }

                // 2. Llamada a Firebase para crear el usuario
                auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Registro exitoso
                            Toast.makeText(context, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                            onRegisterSuccess() // Llama a la función para navegar a otra pantalla
                        } else {
                            // Si falla, muestra el mensaje de error de Firebase
                            val errorMessage = task.exception?.message ?: "Error desconocido."
                            Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿Ya tienes una cuenta?  ",
                fontFamily = UrbanistFamily,
                fontSize = 14.sp,
                color = Color.White
            )
            Text(
                text = "Inicia Sesión",
                fontFamily = UrbanistFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Celeste,
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
    }
}