package com.example.notespote.di

import com.example.notespote.data.local.FileManager
import com.example.notespote.data.mapper.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Los Mappers y FileManager ya tienen @Inject y @Singleton,
    // por lo que Hilt puede crearlos automáticamente.
    // Este módulo está aquí por si necesitamos agregar providers adicionales en el futuro.
}
