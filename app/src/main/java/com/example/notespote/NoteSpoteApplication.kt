package com.example.notespote

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp
import com.example.notespote.BuildConfig

@HiltAndroidApp
class NoteSpoteApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inicializar Firebase al iniciar la app
        FirebaseApp.initializeApp(this)

        // Inicializar App Check con el proveedor de depuración
        if (BuildConfig.DEBUG) {
            val firebaseAppCheck = FirebaseAppCheck.getInstance()
            firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        }

        android.util.Log.d("NoteSpotApp", "App iniciada - Firebase configurado y App Check (Debug) instalado")
    }
}
