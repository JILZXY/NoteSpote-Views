package com.example.notespote.domain.repository

import android.net.Uri
import com.example.notespote.data.model.Apunte
import com.example.notespote.data.model.ApunteDetallado
import kotlinx.coroutines.flow.Flow

interface ApunteRepository {
    fun getApuntesByUser(userId: String): Flow<Result<List<Apunte>>>
    fun getApuntesByFolder(folderId: String): Flow<Result<List<Apunte>>>
    fun getApunteById(id: String): Flow<Result<ApunteDetallado>>
    suspend fun createApunte(apunte: Apunte, archivos: List<Uri>): Result<String>
    suspend fun updateApunte(apunte: Apunte): Result<Unit>
    suspend fun deleteApunte(id: String): Result<Unit>
    suspend fun guardarApunte(apunteId: String): Result<Unit>
    suspend fun syncApuntes(): Result<Unit>
}