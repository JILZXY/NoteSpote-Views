package com.example.notespote.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


val PrimaryColor = Color(0xFF56E8A8)
val SecondaryColor = Color(0xFFFFCC00)
val BackgroundDark = Color(0xFF000000)
val TextWhite = Color(0xFFFFFFFF)


private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    background = BackgroundDark,
    surface = BackgroundDark,
    onPrimary = Color.Black,
    onBackground = TextWhite,
    onSurface = TextWhite
)



@Composable
fun NoteSpoteTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}