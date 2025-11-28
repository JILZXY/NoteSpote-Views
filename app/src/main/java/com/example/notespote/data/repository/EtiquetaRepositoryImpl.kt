package com.example.notespote.data.repository

import android.util.Log
import com.example.notespote.data.local.dao.EtiquetaApunteDao
import com.example.notespote.data.local.dao.EtiquetaDao
import com.example.notespote.data.local.entities.EtiquetaApunteEntity
import com.example.notespote.data.local.entities.EtiquetaEntity
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.mapper.EtiquetaMapper
import com.example.notespote.data.network.NetworkMonitor
import com.example.notespote.domain.model.Etiqueta
import com.example.notespote.domain.repository.EtiquetaRepository
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
class EtiquetaRepositoryImpl @Inject constructor(
    private val etiquetaDao: EtiquetaDao,
    private val etiquetaApunteDao: EtiquetaApunteDao,
    private val firestore: FirebaseFirestore,
    private val networkMonitor: NetworkMonitor,
    private val etiquetaMapper: EtiquetaMapper
) : EtiquetaRepository {

    override fun getAllEtiquetas(): Flow<Result<List<Etiqueta>>> {
        return etiquetaDao.getAllEtiquetas()
            .map { entities ->
                Result.success(entities.map { etiquetaMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override fun getEtiquetasByApunte(apunteId: String): Flow<Result<List<Etiqueta>>> {
        return etiquetaApunteDao.getEtiquetasDetalleByApunte(apunteId)
            .map { entities ->
                Result.success(entities.map { etiquetaMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override fun getTopEtiquetas(limit: Int): Flow<Result<List<Etiqueta>>> {
        return etiquetaDao.getTopEtiquetas(limit)
            .map { entities ->
                Result.success(entities.map { etiquetaMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override fun searchEtiquetas(query: String, limit: Int): Flow<Result<List<Etiqueta>>> {
        return etiquetaDao.searchEtiquetas(query, limit)
            .map { entities ->
                Result.success(entities.map { etiquetaMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override suspend fun createEtiqueta(nombreEtiqueta: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Verificar si ya existe
            val existente = etiquetaDao.getEtiquetaByNombre(nombreEtiqueta)
            if (existente != null) {
                return@withContext Result.success(existente.idEtiqueta)
            }

            val etiquetaId = UUID.randomUUID().toString()
            val entity = EtiquetaEntity(
                idEtiqueta = etiquetaId,
                nombreEtiqueta = nombreEtiqueta,
                vecesUsada = 0,
                syncStatus = SyncStatus.PENDING_UPLOAD
            )

            etiquetaDao.insert(entity)

            if (networkMonitor.isCurrentlyConnected()) {
                firestore.collection("etiquetas")
                    .document(etiquetaId)
                    .set(
                        mapOf(
                            "id" to etiquetaId,
                            "nombreEtiqueta" to nombreEtiqueta,
                            "vecesUsada" to 0
                        )
                    )
                    .await()

                etiquetaDao.updateSyncStatus(etiquetaId, SyncStatus.SYNCED)
            }

            Result.success(etiquetaId)
        } catch (e: Exception) {
            Log.e("EtiquetaRepository", "Error creating etiqueta", e)
            Result.failure(e)
        }
    }

    override suspend fun agregarEtiquetaAApunte(apunteId: String, etiquetaId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Verificar si ya existe la relación
            if (etiquetaApunteDao.exists(apunteId, etiquetaId)) {
                return@withContext Result.success(Unit)
            }

            val etiquetaApunteId = UUID.randomUUID().toString()
            val entity = EtiquetaApunteEntity(
                idEtiquetaApunte = etiquetaApunteId,
                idApunte = apunteId,
                idEtiqueta = etiquetaId,
                syncStatus = SyncStatus.PENDING_UPLOAD,
                isDeleted = false
            )

            etiquetaApunteDao.insert(entity)

            // Incrementar contador de uso
            etiquetaDao.incrementarUso(etiquetaId)

            if (networkMonitor.isCurrentlyConnected()) {
                val etiqueta = etiquetaDao.getEtiquetaById(etiquetaId)
                if (etiqueta != null) {
                    firestore.collection("apuntes")
                        .document(apunteId)
                        .collection("etiquetas")
                        .document(etiquetaId)
                        .set(
                            mapOf(
                                "nombreEtiqueta" to etiqueta.nombreEtiqueta,
                                "isDeleted" to false
                            )
                        )
                        .await()

                    // Actualizar contador en Firestore
                    firestore.collection("etiquetas")
                        .document(etiquetaId)
                        .update("vecesUsada", com.google.firebase.firestore.FieldValue.increment(1))
                        .await()
                }

                etiquetaApunteDao.updateSyncStatus(etiquetaApunteId, SyncStatus.SYNCED)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("EtiquetaRepository", "Error agregando etiqueta a apunte", e)
            Result.failure(e)
        }
    }

    override suspend fun removerEtiquetaDeApunte(apunteId: String, etiquetaId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            etiquetaApunteDao.markAsDeletedByApunteAndEtiqueta(apunteId, etiquetaId)

            // Decrementar contador de uso
            etiquetaDao.decrementarUso(etiquetaId)

            if (networkMonitor.isCurrentlyConnected()) {
                firestore.collection("apuntes")
                    .document(apunteId)
                    .collection("etiquetas")
                    .document(etiquetaId)
                    .update("isDeleted", true)
                    .await()

                // Actualizar contador en Firestore
                firestore.collection("etiquetas")
                    .document(etiquetaId)
                    .update("vecesUsada", com.google.firebase.firestore.FieldValue.increment(-1))
                    .await()

                etiquetaApunteDao.deleteByApunteAndEtiqueta(apunteId, etiquetaId)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("EtiquetaRepository", "Error removiendo etiqueta de apunte", e)
            Result.failure(e)
        }
    }

    override suspend fun getOrCreateEtiqueta(nombreEtiqueta: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val existente = etiquetaDao.getEtiquetaByNombre(nombreEtiqueta)
            if (existente != null) {
                Result.success(existente.idEtiqueta)
            } else {
                createEtiqueta(nombreEtiqueta)
            }
        } catch (e: Exception) {
            Log.e("EtiquetaRepository", "Error getting or creating etiqueta", e)
            Result.failure(e)
        }
    }

    override suspend fun syncEtiquetas(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!networkMonitor.isCurrentlyConnected()) {
                return@withContext Result.failure(Exception("Sin conexión a internet"))
            }

            val pendingEtiquetas = etiquetaDao.getPendingSyncEtiquetas()
            pendingEtiquetas.forEach { etiqueta ->
                val dto = mapOf(
                    "id" to etiqueta.idEtiqueta,
                    "nombreEtiqueta" to etiqueta.nombreEtiqueta,
                    "vecesUsada" to etiqueta.vecesUsada
                )

                firestore.collection("etiquetas")
                    .document(etiqueta.idEtiqueta)
                    .set(dto)
                    .await()

                etiquetaDao.updateSyncStatus(etiqueta.idEtiqueta, SyncStatus.SYNCED)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("EtiquetaRepository", "Error syncing etiquetas", e)
            Result.failure(e)
        }
    }
}