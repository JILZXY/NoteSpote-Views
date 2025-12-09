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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
// IMPORTANTE: Nuevas importaciones necesarias
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
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

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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

            // CORRECCIÓN 1: Intentar subir siempre en segundo plano, protegido contra cancelaciones
            repositoryScope.launch {
                withContext(NonCancellable) {
                    // Verificamos la red DENTRO del proceso seguro
                    if (networkMonitor.isCurrentlyConnected()) {
                        Log.d("CarpetaRepository", "Network connected, launching sync job for carpeta $carpetaId")
                        syncCarpeta(carpetaId)
                    } else {
                        Log.d("CarpetaRepository", "Network not connected, scheduled for later sync")
                    }
                }
            }

            Log.d("CarpetaRepository", "Returning success result for carpeta $carpetaId")
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

            repositoryScope.launch {
                withContext(NonCancellable) {
                    if (networkMonitor.isCurrentlyConnected()) {
                        syncCarpeta(carpeta.id)
                    }
                }
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

            repositoryScope.launch {
                withContext(NonCancellable) {
                    if (networkMonitor.isCurrentlyConnected()) {
                        try {
                            firestore.collection("carpetas")
                                .document(carpetaId)
                                .update("isDeleted", true)
                                .await()

                            // Only delete locally after successful remote deletion
                            carpetaDao.delete(carpetaId)
                        } catch (e: Exception) {
                            Log.e("CarpetaRepository", "Error deleting carpeta from Firestore, will retry later.", e)
                        }
                    }
                }
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

            repositoryScope.launch {
                withContext(NonCancellable) {
                    if (networkMonitor.isCurrentlyConnected()) {
                        syncCarpeta(carpetaId)
                    }
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CarpetaRepository", "Error moviendo carpeta", e)
            Result.failure(e)
        }
    }

    override suspend fun reordenarCarpetas(carpetas: List<Pair<String, Int>>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val batch = firestore.batch()
            carpetas.forEach { (carpetaId, orden) ->
                carpetaDao.updateOrden(carpetaId, orden)
                if (networkMonitor.isCurrentlyConnected()) {
                    val docRef = firestore.collection("carpetas").document(carpetaId)
                    batch.update(docRef, "orden", orden)
                }
            }

            if (networkMonitor.isCurrentlyConnected()) {
                repositoryScope.launch {
                    try {
                        // Protegemos el commit del batch también
                        withContext(NonCancellable) {
                            batch.commit().await()
                        }
                    } catch (e: Exception) {
                        Log.e("CarpetaRepository", "Error reordenando carpetas in Firestore", e)
                        // Mark them as pending update to retry later
                        carpetas.forEach { (carpetaId, _) ->
                            carpetaDao.updateSyncStatus(carpetaId, SyncStatus.PENDING_UPDATE)
                        }
                    }
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
            Log.d("CarpetaRepository", "Found ${pendingCarpetas.size} pending carpetas to sync")

            pendingCarpetas.forEach { carpeta ->
                // CORRECCIÓN 2: Usar NonCancellable para evitar JobCancellationException
                // si el usuario cambia de pantalla mientras se sincroniza
                withContext(NonCancellable) {
                    syncCarpeta(carpeta.idCarpeta)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CarpetaRepository", "Error syncing carpetas", e)
            Result.failure(e)
        }
    }

    private suspend fun syncCarpeta(carpetaId: String) {
        try {
            val carpeta = carpetaDao.getCarpetaById(carpetaId) ?: run {
                Log.e("CarpetaRepository", "Carpeta $carpetaId not found in local database")
                return
            }

            Log.d("CarpetaRepository", "Syncing carpeta $carpetaId with status ${carpeta.syncStatus}")

            // CORRECCIÓN 3: Timeout de 15 segundos para evitar que se quede congelado
            withTimeout(15_000L) {
                when (carpeta.syncStatus) {
                    SyncStatus.PENDING_UPLOAD, SyncStatus.PENDING_UPDATE -> {
                        val dto = carpetaMapper.entityToDto(carpeta)
                        Log.d("CarpetaRepository", "Uploading carpeta to Firestore: ${dto}")

                        firestore.collection("carpetas")
                            .document(carpetaId)
                            .set(dto)
                            .await()

                        Log.d("CarpetaRepository", "✅ Carpeta $carpetaId synced to Firestore successfully")
                        carpetaDao.updateSyncStatus(carpetaId, SyncStatus.SYNCED)
                    }
                    SyncStatus.PENDING_DELETE -> {
                        Log.d("CarpetaRepository", "Deleting carpeta from Firestore")
                        firestore.collection("carpetas")
                            .document(carpetaId)
                            .update("isDeleted", true)
                            .await()

                        carpetaDao.delete(carpetaId)
                        Log.d("CarpetaRepository", "✅ Carpeta $carpetaId deleted from Firestore")
                    }
                    else -> {
                        Log.d("CarpetaRepository", "Carpeta $carpetaId already synced, skipping")
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.w("CarpetaRepository", "⚠️ Timeout syncing carpeta $carpetaId - se reintentará luego")
            // No cambiamos el estado, se queda pendiente para el próximo intento
        } catch (e: Exception) {
            Log.e("CarpetaRepository", "❌ Error syncing carpeta $carpetaId: ${e.message}", e)
            // No lanzar la excepción para que el bucle de syncCarpetas continúe con las siguientes
        }
    }
}