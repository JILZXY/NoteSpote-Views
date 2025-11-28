package com.example.notespote.domain.repository

import com.example.notespote.domain.model.Notificacion
import kotlinx.coroutines.flow.Flow

interface NotificacionRepository {
    fun getNotificaciones(userId: String): Flow<Result<List<Notificacion>>>
    fun getNotificacionesNoLeidas(userId: String): Flow<Result<List<Notificacion>>>
    suspend fun getCountNoLeidas(userId: String): Result<Int>
    suspend fun marcarComoLeida(notificacionId: String): Result<Unit>
    suspend fun marcarTodasComoLeidas(userId: String): Result<Unit>
    suspend fun deleteNotificacion(notificacionId: String): Result<Unit>
    suspend fun createNotificacion(notificacion: Notificacion): Result<Unit>
}