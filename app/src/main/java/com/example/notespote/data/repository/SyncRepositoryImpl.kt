// data/repository/SyncRepositoryImpl.kt
package com.example.notespote.data.repository

import android.util.Log
import com.example.notespote.data.local.NoteSpotDatabase
import com.example.notespote.data.network.NetworkMonitor
import com.example.notespote.domain.model.EstadoSincronizacion
import com.example.notespote.domain.model.ErrorSincronizacion
import com.example.notespote.domain.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val database: NoteSpotDatabase,
    private val apunteRepository: ApunteRepository,
    private val carpetaRepository: CarpetaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val likeRepository: LikeRepository,
    private val seguimientoRepository: SeguimientoRepository,
    private val etiquetaRepository: EtiquetaRepository,
    private val networkMonitor: NetworkMonitor
) : SyncRepository {

    private val _estadoSincronizacion = MutableStateFlow(EstadoSincronizacion())
    override val estadoSincronizacion: Flow<EstadoSincronizacion> = _estadoSincronizacion.asStateFlow()

    override suspend fun syncAll(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!networkMonitor.isCurrentlyConnected()) {
                return@withContext Result.failure(Exception("Sin conexión a internet"))
            }

            updateEstado(sincronizando = true)
            val errores = mutableListOf<ErrorSincronizacion>()
            var itemsSincronizados = 0

            // Sincronizar en orden de dependencias
            try {
                usuarioRepository.syncUsuario()
                itemsSincronizados++
            } catch (e: Exception) {
                Log.e("SyncRepository", "Error syncing usuarios", e)
                errores.add(ErrorSincronizacion("Usuario", e.message ?: "Error desconocido"))
            }

            try {
                carpetaRepository.syncCarpetas()
                itemsSincronizados++
            } catch (e: Exception) {
                Log.e("SyncRepository", "Error syncing carpetas", e)
                errores.add(ErrorSincronizacion("Carpetas", e.message ?: "Error desconocido"))
            }

            try {
                apunteRepository.syncApuntes()
                itemsSincronizados++
            } catch (e: Exception) {
                Log.e("SyncRepository", "Error syncing apuntes", e)
                errores.add(ErrorSincronizacion("Apuntes", e.message ?: "Error desconocido"))
            }

            try {
                etiquetaRepository.syncEtiquetas()
                itemsSincronizados++
            } catch (e: Exception) {
                Log.e("SyncRepository", "Error syncing etiquetas", e)
                errores.add(ErrorSincronizacion("Etiquetas", e.message ?: "Error desconocido"))
            }

            try {
                likeRepository.syncLikes()
                itemsSincronizados++
            } catch (e: Exception) {
                Log.e("SyncRepository", "Error syncing likes", e)
                errores.add(ErrorSincronizacion("Likes", e.message ?: "Error desconocido"))
            }

            try {
                seguimientoRepository.syncSeguimientos()
                itemsSincronizados++
            } catch (e: Exception) {
                Log.e("SyncRepository", "Error syncing seguimientos", e)
                errores.add(ErrorSincronizacion("Seguimientos", e.message ?: "Error desconocido"))
            }

            updateEstado(
                sincronizando = false,
                ultimaSincronizacion = System.currentTimeMillis(),
                itemsSincronizados = itemsSincronizados,
                errores = errores
            )

            if (errores.isEmpty()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Sincronización completada con ${errores.size} errores"))
            }
        } catch (e: Exception) {
            Log.e("SyncRepository", "Error en syncAll", e)
            updateEstado(
                sincronizando = false,
                errores = listOf(ErrorSincronizacion("General", e.message ?: "Error desconocido"))
            )
            Result.failure(e)
        }
    }

    override suspend fun syncApuntes(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            apunteRepository.syncApuntes()
        } catch (e: Exception) {
            Log.e("SyncRepository", "Error syncing apuntes", e)
            Result.failure(e)
        }
    }

    override suspend fun syncCarpetas(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            carpetaRepository.syncCarpetas()
        } catch (e: Exception) {
            Log.e("SyncRepository", "Error syncing carpetas", e)
            Result.failure(e)
        }
    }

    override suspend fun syncMaterias(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // TODO: Implementar cuando tengas MateriaRepository
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SyncRepository", "Error syncing materias", e)
            Result.failure(e)
        }
    }

    override suspend fun syncUsuarios(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            usuarioRepository.syncUsuario()
        } catch (e: Exception) {
            Log.e("SyncRepository", "Error syncing usuarios", e)
            Result.failure(e)
        }
    }

    override suspend fun syncLikes(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            likeRepository.syncLikes()
        } catch (e: Exception) {
            Log.e("SyncRepository", "Error syncing likes", e)
            Result.failure(e)
        }
    }

    override suspend fun syncSeguimientos(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            seguimientoRepository.syncSeguimientos()
        } catch (e: Exception) {
            Log.e("SyncRepository", "Error syncing seguimientos", e)
            Result.failure(e)
        }
    }

    override suspend fun syncEtiquetas(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            etiquetaRepository.syncEtiquetas()
        } catch (e: Exception) {
            Log.e("SyncRepository", "Error syncing etiquetas", e)
            Result.failure(e)
        }
    }

    override suspend fun getPendingSyncCount(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val apuntesPending = database.apunteDao().getPendingSyncApuntes().size
            val carpetasPending = database.carpetaDao().getPendingSyncCarpetas().size
            val likesPending = database.likeDao().getPendingSyncLikes().size
            val seguimientosPending = database.seguimientoDao().getPendingSyncSeguimientos().size

            val total = apuntesPending + carpetasPending + likesPending + seguimientosPending

            updateEstado(itemsPendientes = total)

            Result.success(total)
        } catch (e: Exception) {
            Log.e("SyncRepository", "Error getting pending sync count", e)
            Result.failure(e)
        }
    }

    override suspend fun clearSyncErrors(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            updateEstado(errores = emptyList())
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SyncRepository", "Error clearing sync errors", e)
            Result.failure(e)
        }
    }

    private fun updateEstado(
        sincronizando: Boolean? = null,
        ultimaSincronizacion: Long? = null,
        itemsPendientes: Int? = null,
        itemsSincronizados: Int? = null,
        errores: List<ErrorSincronizacion>? = null
    ) {
        val estadoActual = _estadoSincronizacion.value
        _estadoSincronizacion.value = estadoActual.copy(
            sincronizando = sincronizando ?: estadoActual.sincronizando,
            ultimaSincronizacion = ultimaSincronizacion ?: estadoActual.ultimaSincronizacion,
            itemsPendientes = itemsPendientes ?: estadoActual.itemsPendientes,
            itemsSincronizados = itemsSincronizados ?: estadoActual.itemsSincronizados,
            errores = errores ?: estadoActual.errores
        )
    }
}