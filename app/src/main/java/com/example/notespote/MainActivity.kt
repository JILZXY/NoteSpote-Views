package com.example.notespote

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notespot.presentation.theme.NoteSpotTheme
import com.example.notespote.presentation.navigation.*
import com.example.notespote.presentation.theme.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Inicializar Firebase explícitamente
        FirebaseApp.initializeApp(this)

        // 2. Habilitar logs de Firestore (para debug)
        FirebaseFirestore.setLoggingEnabled(true)

        // 3. Verificar que Firebase esté conectado (logs de debug)
        Log.e("MainActivity", "Firebase inicializado")
        Log.e("MainActivity", "Auth User: ${FirebaseAuth.getInstance().currentUser?.uid ?: "NO AUTENTICADO"}")
        Log.e("MainActivity", "Firestore: ${FirebaseFirestore.getInstance()}")
        Log.e("MainActivity", "Storage: ${FirebaseStorage.getInstance()}")
        enableEdgeToEdge()
        setContent {
            NoteSpotTheme {
                NavManager()
            }
        }
    }
}
