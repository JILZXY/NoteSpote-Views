package com.example.notespote

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NoteSpoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inicializar Firebase al iniciar la app
        FirebaseApp.initializeApp(this)

        android.util.Log.d("NoteSpotApp", "App iniciada - Firebase configurado")
    }
}
