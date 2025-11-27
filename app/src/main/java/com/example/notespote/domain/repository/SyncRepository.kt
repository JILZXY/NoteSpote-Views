package com.example.notespote.domain.repository

import com.example.notespote.domain.model.EstadoSincronizacion
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    val estadoSincronizacion: Flow<EstadoSincronizacion>

    suspend fun syncAll(): Result<Unit>
    suspend fun syncApuntes(): Result<Unit>
    suspend fun syncCarpetas(): Result<Unit>
    suspend fun syncMaterias(): Result<Unit>
    suspend fun syncUsuarios(): Result<Unit>
    suspend fun syncLikes(): Result<Unit>
    suspend fun syncSeguimientos(): Result<Unit>
    suspend fun syncEtiquetas(): Result<Unit>
    suspend fun getPendingSyncCount(): Result<Int>
    suspend fun clearSyncErrors(): Result<Unit>
}