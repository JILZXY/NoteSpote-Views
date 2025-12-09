package com.example.notespote.domain.repository

import com.example.notespote.domain.model.SesionUsuario
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val sesionActual: Flow<SesionUsuario?>
    val estaAutenticado: Flow<Boolean>

    suspend fun login(email: String, password: String): Result<SesionUsuario>
    suspend fun register(
        email: String,
        password: String,
        nombreUsuario: String,
        nombre: String?,
        apellido: String?
    ): Result<SesionUsuario>
    suspend fun loginWithGoogle(): Result<SesionUsuario>
    suspend fun logout(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun cambiarPassword(
        passwordActual: String,
        passwordNuevo: String
    ): Result<Unit>
    suspend fun verificarEmail(): Result<Unit>
    fun getCurrentUserId(): String?
    suspend fun forceTokenRefresh(): Result<Unit>
}