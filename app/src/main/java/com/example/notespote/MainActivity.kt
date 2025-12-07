package com.example.notespote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notespot.presentation.theme.NoteSpotTheme
import com.example.notespote.presentation.navigation.*
import com.example.notespote.presentation.theme.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteSpotTheme {
                NavManager()
            }
        }
    }
}
