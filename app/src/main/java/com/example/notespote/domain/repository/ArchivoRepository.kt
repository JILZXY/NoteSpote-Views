package com.example.notespote.domain.repository

import android.net.Uri
import com.example.notespote.domain.model.ArchivoAdjunto
import kotlinx.coroutines.flow.Flow

interface ArchivoRepository {
    fun getArchivosByApunte(apunteId: String): Flow<Result<List<ArchivoAdjunto>>>
    suspend fun getArchivoById(archivoId: String): Result<ArchivoAdjunto>
    suspend fun uploadArchivo(
        apunteId: String,
        uri: Uri,
        nombreArchivo: String
    ): Result<String>
    suspend fun descargarArchivo(archivoId: String): Result<String>
    suspend fun deleteArchivo(archivoId: String): Result<Unit>
    suspend fun getArchivoUri(archivoId: String): Result<Uri>
    suspend fun syncArchivos(): Result<Unit>
}