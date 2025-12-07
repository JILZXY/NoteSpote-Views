package com.example.notespote.domain.repository

import com.example.notespote.domain.model.Etiqueta
import kotlinx.coroutines.flow.Flow

interface EtiquetaRepository {
    fun getAllEtiquetas(): Flow<Result<List<Etiqueta>>>
    fun getEtiquetaById(etiquetaId: String): Flow<Result<Etiqueta>>
    fun getEtiquetasByApunte(apunteId: String): Flow<Result<List<Etiqueta>>>
    fun getTopEtiquetas(limit: Int): Flow<Result<List<Etiqueta>>>
    fun searchEtiquetas(query: String, limit: Int): Flow<Result<List<Etiqueta>>>
    suspend fun createEtiqueta(nombreEtiqueta: String): Result<String>
    suspend fun agregarEtiquetaAApunte(apunteId: String, etiquetaId: String): Result<Unit>
    suspend fun removerEtiquetaDeApunte(apunteId: String, etiquetaId: String): Result<Unit>
    suspend fun getOrCreateEtiqueta(nombreEtiqueta: String): Result<String>
    suspend fun syncEtiquetas(): Result<Unit>
}