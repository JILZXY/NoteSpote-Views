package com.example.notespote.di

import android.content.Context
import androidx.room.Room
import com.example.notespote.data.local.NoteSpotDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NoteSpotDatabase {
        return Room.databaseBuilder(
            context,
            NoteSpotDatabase::class.java,
            "notespot_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUsuarioDao(database: NoteSpotDatabase) = database.usuarioDao()

    @Provides
    fun provideMateriaDao(database: NoteSpotDatabase) = database.materiaDao()

    @Provides
    fun provideCarpetaDao(database: NoteSpotDatabase) = database.carpetaDao()

    @Provides
    fun provideApunteDao(database: NoteSpotDatabase) = database.apunteDao()

    @Provides
    fun providePostitDao(database: NoteSpotDatabase) = database.postitDao()

    @Provides
    fun provideArchivoAdjuntoDao(database: NoteSpotDatabase) = database.archivoAdjuntoDao()

    @Provides
    fun provideLikeDao(database: NoteSpotDatabase) = database.likeDao()

    @Provides
    fun provideEtiquetaDao(database: NoteSpotDatabase) = database.etiquetaDao()

    @Provides
    fun provideEtiquetaApunteDao(database: NoteSpotDatabase) = database.etiquetaApunteDao()

    @Provides
    fun provideSeguimientoDao(database: NoteSpotDatabase) = database.seguimientoDao()
}