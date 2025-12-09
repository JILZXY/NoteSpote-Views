package com.example.notespote.di

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// di/FirebaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance("databasenote")

        // ✅ Habilitar logs para debugging
        FirebaseFirestore.setLoggingEnabled(true)

        // ✅ Configuración adicional (opcional, para testing)
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)  // Cache local
            .build()
        firestore.firestoreSettings = settings

        Log.d("FirebaseModule", "✅ Firestore inicializado: $firestore")

        return firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}