// data/repository/SeguimientoRepositoryImpl.kt
package com.example.notespote.data.repository

import android.util.Log
import com.example.notespote.data.local.dao.SeguimientoDao
import com.example.notespote.data.local.dao.UsuarioDao
import com.example.notespote.data.local.entities.SeguimientoEntity
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.mapper.SeguimientoMapper
import com.example.notespote.data.mapper.UsuarioMapper
import com.example.notespote.data.network.NetworkMonitor
import com.example.notespote.domain.model.Seguimiento
import com.example.notespote.domain.model.Usuario
import com.example.notespote.domain.repository.SeguimientoRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeguimientoRepositoryImpl @Inject constructor(
    private val seguimientoDao: SeguimientoDao,
    private val usuarioDao: UsuarioDao,
    private val firestore: FirebaseFirestore,
    private val networkMonitor: NetworkMonitor,
    private val seguimientoMapper: SeguimientoMapper,
    private val usuarioMapper: UsuarioMapper
) : SeguimientoRepository {

    override fun getSeguidos(userId: String): Flow<Result<List<Usuario>>> {
        return seguimientoDao.getSeguidosDetalle(userId)
            .map { entities ->
                Result.success(entities.map { usuarioMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override fun getSeguidores(userId: String): Flow<Result<List<Usuario>>> {
        return seguimientoDao.getSeguidoresDetalle(userId)
            .map { entities ->
                Result.success(entities.map { usuarioMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override fun isSiguiendo(seguidorId: String, seguidoId: String): Flow<Result<Boolean>> {
        return kotlinx.coroutines.flow.flow {
            val isSiguiendo = seguimientoDao.isSiguiendo(seguidorId, seguidoId)
            emit(Result.success(isSiguiendo))
        }.catch { e ->
            emit(Result.failure(e))
        }
    }

    override suspend fun getSigueCount(userId: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val count = seguimientoDao.getSigueCount(userId)
            Result.success(count)
        } catch (e: Exception) {
            Log.e("SeguimientoRepository", "Error getting sigue count", e)
            Result.failure(e)
        }
    }

    override suspend fun getSeguidoresCount(userId: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val count = seguimientoDao.getSeguidoresCount(userId)
            Result.success(count)
        } catch (e: Exception) {
            Log.e("SeguimientoRepository", "Error getting seguidores count", e)
            Result.failure(e)
        }
    }

    override suspend fun seguir(seguidorId: String, seguidoId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Validar que no se siga a sí mismo
            if (seguidorId == seguidoId) {
                return@withContext Result.failure(Exception("No puedes seguirte a ti mismo"))
            }

            // Verificar si ya existe el seguimiento
            if (seguimientoDao.isSiguiendo(seguidorId, seguidoId)) {
                return@withContext Result.success(Unit)
            }

            val seguimientoId = UUID.randomUUID().toString()
            val entity = SeguimientoEntity(
                idSeguimiento = seguimientoId,
                idSeguidor = seguidorId,
                idSeguido = seguidoId,
                fechaSeguimiento = System.currentTimeMillis(),
                syncStatus = SyncStatus.PENDING_UPLOAD,
                isDeleted = false
            )

            seguimientoDao.insert(entity)

            // Actualizar contadores localmente
            val seguidor = usuarioDao.getUsuarioById(seguidorId)
            if (seguidor != null) {
                usuarioDao.updateTotalSeguidos(seguidorId, seguidor.totalSeguidos + 1)
            }

            val seguido = usuarioDao.getUsuarioById(seguidoId)
            if (seguido != null) {
                usuarioDao.updateTotalSeguidores(seguidoId, seguido.totalSeguidores + 1)
            }

            if (networkMonitor.isCurrentlyConnected()) {
                val dto = seguimientoMapper.entityToDto(entity)
                firestore.collection("seguimientos")
                    .document(seguimientoId)
                    .set(dto)
                    .await()

                // Actualizar contadores en Firestore
                firestore.collection("users")
                    .document(seguidorId)
                    .update("totalSeguidos", com.google.firebase.firestore.FieldValue.increment(1))
                    .await()

                firestore.collection("users")
                    .document(seguidoId)
                    .update("totalSeguidores", com.google.firebase.firestore.FieldValue.increment(1))
                    .await()

                seguimientoDao.updateSyncStatus(seguimientoId, SyncStatus.SYNCED)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SeguimientoRepository", "Error siguiendo usuario", e)
            Result.failure(e)
        }
    }

    override suspend fun dejarDeSeguir(seguidorId: String, seguidoId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            seguimientoDao.markAsDeletedByUsuarios(seguidorId, seguidoId)

            // Actualizar contadores localmente
            val seguidor = usuarioDao.getUsuarioById(seguidorId)
            if (seguidor != null && seguidor.totalSeguidos > 0) {
                usuarioDao.updateTotalSeguidos(seguidorId, seguidor.totalSeguidos - 1)
            }

            val seguido = usuarioDao.getUsuarioById(seguidoId)
            if (seguido != null && seguido.totalSeguidores > 0) {
                usuarioDao.updateTotalSeguidores(seguidoId, seguido.totalSeguidores - 1)
            }

            if (networkMonitor.isCurrentlyConnected()) {
                val seguimiento = seguimientoDao.getSeguimiento(seguidorId, seguidoId)
                if (seguimiento != null) {
                    firestore.collection("seguimientos")
                        .document(seguimiento.idSeguimiento)
                        .update("isDeleted", true)
                        .await()

                    // Actualizar contadores en Firestore
                    firestore.collection("users")
                        .document(seguidorId)
                        .update("totalSeguidos", com.google.firebase.firestore.FieldValue.increment(-1))
                        .await()

                    firestore.collection("users")
                        .document(seguidoId)
                        .update("totalSeguidores", com.google.firebase.firestore.FieldValue.increment(-1))
                        .await()

                    seguimientoDao.deleteByUsuarios(seguidorId, seguidoId)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SeguimientoRepository", "Error dejando de seguir usuario", e)
            Result.failure(e)
        }
    }

    override suspend fun toggleSeguir(seguidorId: String, seguidoId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val isSiguiendo = seguimientoDao.isSiguiendo(seguidorId, seguidoId)

            if (isSiguiendo) {
                dejarDeSeguir(seguidorId, seguidoId)
            } else {
                seguir(seguidorId, seguidoId)
            }
        } catch (e: Exception) {
            Log.e("SeguimientoRepository", "Error toggling seguir", e)
            Result.failure(e)
        }
    }

    override suspend fun syncSeguimientos(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!networkMonitor.isCurrentlyConnected()) {
                return@withContext Result.failure(Exception("Sin conexión a internet"))
            }

            val pendingSeguimientos = seguimientoDao.getPendingSyncSeguimientos()
            pendingSeguimientos.forEach { seguimiento ->
                when (seguimiento.syncStatus) {
                    SyncStatus.PENDING_UPLOAD -> {
                        val dto = seguimientoMapper.entityToDto(seguimiento)
                        firestore.collection("seguimientos")
                            .document(seguimiento.idSeguimiento)
                            .set(dto)
                            .await()

                        seguimientoDao.updateSyncStatus(seguimiento.idSeguimiento, SyncStatus.SYNCED)
                    }
                    SyncStatus.PENDING_DELETE -> {
                        firestore.collection("seguimientos")
                            .document(seguimiento.idSeguimiento)
                            .update("isDeleted", true)
                            .await()

                        seguimientoDao.delete(seguimiento.idSeguimiento)
                    }
                    else -> {}
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SeguimientoRepository", "Error syncing seguimientos", e)
            Result.failure(e)
        }
    }
}