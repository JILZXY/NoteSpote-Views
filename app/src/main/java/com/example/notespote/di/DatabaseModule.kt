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
    fun provideApunteDao(database: NoteSpotDatabase) = database.apunteDao()

    @Provides
    fun provideArchivoDao(database: NoteSpotDatabase) = database.archivoAdjuntoDao()

    // ... otros DAOs
}