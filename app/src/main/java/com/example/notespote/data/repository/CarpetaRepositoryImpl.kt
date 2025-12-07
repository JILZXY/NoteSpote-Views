package com.example.notespote.data.repository

import android.util.Log
import com.example.notespote.data.local.dao.ApunteDao
import com.example.notespote.data.local.dao.CarpetaDao
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.mapper.ApunteMapper
import com.example.notespote.data.mapper.CarpetaMapper
import com.example.notespote.data.network.NetworkMonitor
import com.example.notespote.domain.model.Carpeta
import com.example.notespote.domain.model.CarpetaContenido
import com.example.notespote.domain.repository.CarpetaRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CarpetaRepositoryImpl @Inject constructor(
    private val carpetaDao: CarpetaDao,
    private val apunteDao: ApunteDao,
    private val firestore: FirebaseFirestore,
    private val networkMonitor: NetworkMonitor,
    private val carpetaMapper: CarpetaMapper,
    private val apunteMapper: ApunteMapper
) : CarpetaRepository {

    override fun getCarpetasByUser(userId: String): Flow<Result<List<Carpeta>>> {
        return carpetaDao.getCarpetasByUser(userId)
            .map { entities ->
                Result.success(entities.map { carpetaMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override fun getCarpetasRaiz(userId: String): Flow<Result<List<Carpeta>>> {
        return carpetaDao.getCarpetasRaiz(userId)
            .map { entities ->
                Result.success(entities.map { carpetaMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override fun getSubcarpetas(carpetaPadreId: String): Flow<Result<List<Carpeta>>> {
        return carpetaDao.getSubcarpetas(carpetaPadreId)
            .map { entities ->
                Result.success(entities.map { carpetaMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override fun getCarpetaById(carpetaId: String): Flow<Result<Carpeta>> {
        return kotlinx.coroutines.flow.flow {
            val entity = carpetaDao.getCarpetaById(carpetaId)
            if (entity != null) {
                emit(Result.success(carpetaMapper.toDomain(entity)))
            } else {
                emit(Result.failure(Exception("Carpeta no encontrada")))
            }
        }.catch { e ->
            emit(Result.failure(e))
        }
    }

    override fun getCarpetaConContenido(carpetaId: String): Flow<Result<CarpetaContenido>> {
        return kotlinx.coroutines.flow.flow {
            val carpetaEntity = carpetaDao.getCarpetaById(carpetaId)
            if (carpetaEntity != null) {
                val subcarpetas = carpetaDao.getSubcarpetas(carpetaId).first()
                val apuntes = apunteDao.getApuntesByFolder(carpetaId).first()

                val contenido = CarpetaContenido(
                    carpeta = carpetaMapper.toDomain(carpetaEntity),
                    subcarpetas = subcarpetas.map { carpetaMapper.toDomain(it) },
                    apuntes = apuntes.map { apunteMapper.toDomain(it) }
                )
                emit(Result.success(contenido))
            } else {
                emit(Result.failure(Exception("Carpeta no encontrada")))
            }
        }.catch { e ->
            emit(Result.failure(e))
        }
    }

    override fun getCarpetasByMateria(materiaId: String): Flow<Result<List<Carpeta>>> {
        return carpetaDao.getCarpetasByMateria(materiaId)
            .map { entities ->
                Result.success(entities.map { carpetaMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override suspend fun createCarpeta(carpeta: Carpeta): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("CarpetaRepository", "createCarpeta started for: ${carpeta.nombreCarpeta}")
            val carpetaId = UUID.randomUUID().toString()
            val entity = carpetaMapper.toEntity(carpeta).copy(
                idCarpeta = carpetaId,
                syncStatus = SyncStatus.PENDING_UPLOAD
            )

            Log.d("CarpetaRepository", "Inserting carpeta entity into database")
            carpetaDao.insert(entity)
            Log.d("CarpetaRepository", "Carpeta inserted successfully with ID: $carpetaId")

            if (networkMonitor.isCurrentlyConnected()) {
                Log.d("CarpetaRepository", "Network connected, syncing carpeta")
                syncCarpeta(carpetaId)
                Log.d("CarpetaRepository", "Carpeta synced successfully")
            } else {
                Log.d("CarpetaRepository", "Network not connected, skipping sync")
            }

            Log.d("CarpetaRepository", "Returning success result")
            Result.success(carpetaId)
        } catch (e: Exception) {
            Log.e("CarpetaRepository", "Error creating carpeta", e)
            Result.failure(e)
        }
    }

    override suspend fun updateCarpeta(carpeta: Carpeta): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = carpetaMapper.toEntity(carpeta).copy(
                syncStatus = SyncStatus.PENDING_UPDATE
            )

            carpetaDao.update(entity)

            if (networkMonitor.isCurrentlyConnected()) {
                syncCarpeta(carpeta.id)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CarpetaRepository", "Error updating carpeta", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteCarpeta(carpetaId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            carpetaDao.markAsDeleted(carpetaId)

            if (networkMonitor.isCurrentlyConnected()) {
                firestore.collection("carpetas")
                    .document(carpetaId)
                    .update("isDeleted", true)
                    .await()

                carpetaDao.delete(carpetaId)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CarpetaRepository", "Error deleting carpeta", e)
            Result.failure(e)
        }
    }

    override suspend fun moverCarpeta(carpetaId: String, nuevoPadreId: String?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val carpeta = carpetaDao.getCarpetaById(carpetaId)
                ?: return@withContext Result.failure(Exception("Carpeta no encontrada"))

            carpetaDao.update(
                carpeta.copy(
                    idCarpetaPadre = nuevoPadreId,
                    syncStatus = SyncStatus.PENDING_UPDATE
                )
            )

            if (networkMonitor.isCurrentlyConnected()) {
                firestore.collection("carpetas")
                    .document(carpetaId)
                    .update("idCarpetaPadre", nuevoPadreId)
                    .await()

                carpetaDao.updateSyncStatus(carpetaId, SyncStatus.SYNCED)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CarpetaRepository", "Error moviendo carpeta", e)
            Result.failure(e)
        }
    }

    override suspend fun reordenarCarpetas(carpetas: List<Pair<String, Int>>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            carpetas.forEach { (carpetaId, orden) ->
                carpetaDao.updateOrden(carpetaId, orden)

                if (networkMonitor.isCurrentlyConnected()) {
                    firestore.collection("carpetas")
                        .document(carpetaId)
                        .update("orden", orden)
                        .await()
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CarpetaRepository", "Error reordenando carpetas", e)
            Result.failure(e)
        }
    }

    override suspend fun syncCarpetas(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!networkMonitor.isCurrentlyConnected()) {
                return@withContext Result.failure(Exception("Sin conexión a internet"))
            }

            val pendingCarpetas = carpetaDao.getPendingSyncCarpetas()
            pendingCarpetas.forEach { carpeta ->
                syncCarpeta(carpeta.idCarpeta)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CarpetaRepository", "Error syncing carpetas", e)
            Result.failure(e)
        }
    }

    private suspend fun syncCarpeta(carpetaId: String) {
        try {
            val carpeta = carpetaDao.getCarpetaById(carpetaId) ?: return

            when (carpeta.syncStatus) {
                SyncStatus.PENDING_UPLOAD, SyncStatus.PENDING_UPDATE -> {
                    val dto = carpetaMapper.entityToDto(carpeta)
                    firestore.collection("carpetas")
                        .document(carpetaId)
                        .set(dto)
                        .await()

                    carpetaDao.updateSyncStatus(carpetaId, SyncStatus.SYNCED)
                }
                SyncStatus.PENDING_DELETE -> {
                    firestore.collection("carpetas")
                        .document(carpetaId)
                        .update("isDeleted", true)
                        .await()

                    carpetaDao.delete(carpetaId)
                }
                else -> {}
            }
        } catch (e: Exception) {
            Log.e("CarpetaRepository", "Error syncing carpeta $carpetaId", e)
        }
    }
}