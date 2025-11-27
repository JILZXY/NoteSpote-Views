package com.example.notespote.domain.repository

import com.example.notespote.domain.model.Seguimiento
import com.example.notespote.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

interface SeguimientoRepository {
    fun getSeguidos(userId: String): Flow<Result<List<Usuario>>>
    fun getSeguidores(userId: String): Flow<Result<List<Usuario>>>
    fun isSiguiendo(seguidorId: String, seguidoId: String): Flow<Result<Boolean>>
    suspend fun getSigueCount(userId: String): Result<Int>
    suspend fun getSeguidoresCount(userId: String): Result<Int>
    suspend fun seguir(seguidorId: String, seguidoId: String): Result<Unit>
    suspend fun dejarDeSeguir(seguidorId: String, seguidoId: String): Result<Unit>
    suspend fun toggleSeguir(seguidorId: String, seguidoId: String): Result<Unit>
    suspend fun syncSeguimientos(): Result<Unit>
}