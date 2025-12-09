package com.example.notespot.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.example.notespote.presentation.theme.Celeste
import com.example.notespote.presentation.theme.RichBlack
import com.example.notespote.presentation.theme.Typography

private val DarkColorScheme = darkColorScheme(
    primary = Celeste,
    background = RichBlack,
    surface = RichBlack,
    onPrimary = RichBlack,
    onBackground = Celeste,
    onSurface = Celeste
)

@Composable
fun NoteSpotTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}