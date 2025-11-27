package com.example.notespote.data.repository

import android.net.Uri
import com.example.notespote.data.model.EstadisticasUsuario
import com.example.notespote.data.model.PerfilUsuario
import com.example.notespote.data.model.Usuario
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {
    fun getUsuarioById(userId: String): Flow<Result<Usuario>>
    fun getUsuarioActual(): Flow<Result<Usuario?>>
    fun getPerfilUsuario(userId: String): Flow<Result<PerfilUsuario>>
    fun searchUsuarios(query: String): Flow<Result<List<Usuario>>>
    suspend fun updateUsuario(usuario: Usuario): Result<Unit>
    suspend fun updateFotoPerfil(userId: String, uri: Uri): Result<String>
    suspend fun updateBiografia(userId: String, biografia: String): Result<Unit>
    suspend fun getEstadisticas(userId: String): Result<EstadisticasUsuario>
    suspend fun updateUltimaConexion(userId: String): Result<Unit>
    suspend fun syncUsuario(): Result<Unit>
}