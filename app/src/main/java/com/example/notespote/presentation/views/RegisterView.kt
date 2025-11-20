package com.example.notespot.presentation.views

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespot.presentation.components.buttons.PrimaryButton
import com.example.notespot.presentation.components.inputs.EmailInput
import com.example.notespot.presentation.components.inputs.PasswordInput
import com.example.notespot.presentation.components.inputs.UserInput
import com.example.notespote.presentation.theme.Celeste
import com.example.notespote.presentation.theme.MajorelleBlue
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.presentation.theme.YellowOrange


@Composable
fun RegisterView(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RichBlack)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡Hola!",
            style = MaterialTheme.typography.displayLarge,
            color = Celeste
        )

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = YellowOrange)) {
                    append("Bien")
                }
                withStyle(style = SpanStyle(color = MajorelleBlue)) {
                    append("veni")
                }
                withStyle(style = SpanStyle(color = Celeste)) {
                    append("do")
                }
            },
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Registra tu cuenta",
            style = MaterialTheme.typography.bodyLarge,
            color = Celeste
        )

        Spacer(modifier = Modifier.height(32.dp))

        UserInput(
            value = username,
            onValueChange = { username = it },
            placeholder = "Usuario"
        )

        Spacer(modifier = Modifier.height(16.dp))

        EmailInput(
            value = email,
            onValueChange = { email = it },
            placeholder = "Correo electrónico"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordInput(
            value = password,
            onValueChange = { password = it },
            placeholder = "Contraseña"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordInput(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "Confirma tu contraseña"
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Regístrate",
            onClick = onRegisterClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Have an account ? ",
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                color = Celeste
            )
            Text(
                text = "Log In",
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Celeste,
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
    }
}