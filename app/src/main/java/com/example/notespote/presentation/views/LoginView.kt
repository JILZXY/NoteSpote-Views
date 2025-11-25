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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notespot.presentation.components.buttons.PrimaryButton
import com.example.notespote.presentation.components.inputs.EmailInput
import com.example.notespote.presentation.components.inputs.PasswordInput
import com.example.notespote.presentation.theme.Celeste
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.presentation.theme.SyneMonoFamily
import com.example.notespote.presentation.theme.UrbanistFamily

@Composable
fun LoginView(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                withStyle(style = SpanStyle(color = Color(0xFFFFB347))) {
                    append("B")
                }
                withStyle(style = SpanStyle(color = Color(0xFFFF9E9E))) {
                    append("i")
                }
                withStyle(style = SpanStyle(color = Color(0xFFFFF599))) {
                    append("e")
                }
                withStyle(style = SpanStyle(color = Color(0xFF91F48F))) {
                    append("n")
                }
                withStyle(style = SpanStyle(color = Color(0xFF9EFFFF))) {
                    append("v")
                }
                withStyle(style = SpanStyle(color = Color(0xFFFD99FF))) {
                    append("e")
                }
                withStyle(style = SpanStyle(color = Color(0xFFB69CFF))) {
                    append("n")
                }
                withStyle(style = SpanStyle(color = Color(0xFF624AF2))) {
                    append("i")
                }
                withStyle(style = SpanStyle(color = Color(0xFFFCDDEC))) {
                    append("d")
                }
                withStyle(style = SpanStyle(color = Color.White)) {
                    append("o")
                }
            },
            fontFamily = UrbanistFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 72.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ingresa tus datos",
            fontFamily = SyneMonoFamily,
            fontSize = 14.sp,
            color = Celeste
        )

        Spacer(modifier = Modifier.height(40.dp))

        EmailInput(
            value = email,
            onValueChange = { email = it },
            placeholder = "Usuario o correo electrónico"
        )

        Spacer(modifier = Modifier.height(20.dp))

        PasswordInput(
            value = password,
            onValueChange = { password = it },
            placeholder = "Contraseña"
        )

        Spacer(modifier = Modifier.height(40.dp))

        PrimaryButton(
            text = "Inicia Sesión",
            onClick = {
                onLoginClick()
            }
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿No tienes una cuenta?  ",
                fontFamily = UrbanistFamily,
                fontSize = 14.sp,
                color = Color.White
            )
            Text(
                text = "Regístrate",
                fontFamily = UrbanistFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Celeste,
                modifier = Modifier.clickable { onRegisterClick() }
            )
        }
    }
}