package com.example.notespote.data.repository

import android.util.Log
import com.example.notespote.data.local.dao.PostitDao
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.mapper.PostitMapper
import com.example.notespote.data.network.NetworkMonitor
import com.example.notespote.domain.model.Postit
import com.example.notespote.domain.repository.PostitRepository
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
class PostitRepositoryImpl @Inject constructor(
    private val postitDao: PostitDao,
    private val firestore: FirebaseFirestore,
    private val networkMonitor: NetworkMonitor,
    private val postitMapper: PostitMapper
) : PostitRepository {

    override fun getPostitsByApunte(apunteId: String): Flow<Result<List<Postit>>> {
        return postitDao.getPostitsByApunte(apunteId)
            .map { entities ->
                Result.success(entities.map { postitMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override suspend fun getPostitById(postitId: String): Result<Postit> = withContext(Dispatchers.IO) {
        try {
            val entity = postitDao.getPostitById(postitId)
            if (entity != null) {
                Result.success(postitMapper.toDomain(entity))
            } else {
                Result.failure(Exception("Post-it no encontrado"))
            }
        } catch (e: Exception) {
            Log.e("PostitRepository", "Error getting postit", e)
            Result.failure(e)
        }
    }

    override suspend fun createPostit(postit: Postit): Result<String> = withContext(Dispatchers.IO) {
        try {
            val postitId = UUID.randomUUID().toString()
            val entity = postitMapper.toEntity(postit).copy(
                idPostit = postitId,
                syncStatus = SyncStatus.PENDING_UPLOAD
            )

            postitDao.insert(entity)

            if (networkMonitor.isCurrentlyConnected()) {
                syncPostit(postitId, postit.idApunte)
            }

            Result.success(postitId)
        } catch (e: Exception) {
            Log.e("PostitRepository", "Error creating postit", e)
            Result.failure(e)
        }
    }

    override suspend fun updatePostit(postit: Postit): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = postitMapper.toEntity(postit).copy(
                syncStatus = SyncStatus.PENDING_UPDATE
            )

            postitDao.update(entity)

            if (networkMonitor.isCurrentlyConnected()) {
                syncPostit(postit.id, postit.idApunte)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PostitRepository", "Error updating postit", e)
            Result.failure(e)
        }
    }

    override suspend fun updatePosicion(postitId: String, x: Int, y: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postitDao.updatePosicion(postitId, x, y)

            val postit = postitDao.getPostitById(postitId)
            if (postit != null && networkMonitor.isCurrentlyConnected()) {
                firestore.collection("apuntes")
                    .document(postit.idApunte)
                    .collection("postits")
                    .document(postitId)
                    .update(
                        mapOf(
                            "posicionX" to x,
                            "posicionY" to y
                        )
                    )
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PostitRepository", "Error updating posicion", e)
            Result.failure(e)
        }
    }

    override suspend fun updateTamano(postitId: String, ancho: Int, alto: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postitDao.updateTamano(postitId, ancho, alto)

            val postit = postitDao.getPostitById(postitId)
            if (postit != null && networkMonitor.isCurrentlyConnected()) {
                firestore.collection("apuntes")
                    .document(postit.idApunte)
                    .collection("postits")
                    .document(postitId)
                    .update(
                        mapOf(
                            "ancho" to ancho,
                            "alto" to alto
                        )
                    )
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PostitRepository", "Error updating tamano", e)
            Result.failure(e)
        }
    }

    override suspend fun updateOrdenZ(postitId: String, ordenZ: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postitDao.updateOrdenZ(postitId, ordenZ)

            val postit = postitDao.getPostitById(postitId)
            if (postit != null && networkMonitor.isCurrentlyConnected()) {
                firestore.collection("apuntes")
                    .document(postit.idApunte)
                    .collection("postits")
                    .document(postitId)
                    .update("ordenZ", ordenZ)
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PostitRepository", "Error updating ordenZ", e)
            Result.failure(e)
        }
    }

    override suspend fun deletePostit(postitId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val postit = postitDao.getPostitById(postitId)
            postitDao.markAsDeleted(postitId)

            if (postit != null && networkMonitor.isCurrentlyConnected()) {
                firestore.collection("apuntes")
                    .document(postit.idApunte)
                    .collection("postits")
                    .document(postitId)
                    .update("isDeleted", true)
                    .await()

                postitDao.delete(postitId)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PostitRepository", "Error deleting postit", e)
            Result.failure(e)
        }
    }

    override suspend fun syncPostits(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!networkMonitor.isCurrentlyConnected()) {
                return@withContext Result.failure(Exception("Sin conexión a internet"))
            }

            val pendingPostits = postitDao.getPendingSyncPostits()
            pendingPostits.forEach { postit ->
                syncPostit(postit.idPostit, postit.idApunte)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("PostitRepository", "Error syncing postits", e)
            Result.failure(e)
        }
    }

    private suspend fun syncPostit(postitId: String, apunteId: String) {
        try {
            val postit = postitDao.getPostitById(postitId) ?: return

            when (postit.syncStatus) {
                SyncStatus.PENDING_UPLOAD, SyncStatus.PENDING_UPDATE -> {
                    val dto = postitMapper.entityToDto(postit)
                    firestore.collection("apuntes")
                        .document(apunteId)
                        .collection("postits")
                        .document(postitId)
                        .set(dto)
                        .await()

                    postitDao.updateSyncStatus(postitId, SyncStatus.SYNCED)
                }
                SyncStatus.PENDING_DELETE -> {
                    firestore.collection("apuntes")
                        .document(apunteId)
                        .collection("postits")
                        .document(postitId)
                        .update("isDeleted", true)
                        .await()

                    postitDao.delete(postitId)
                }
                else -> {}
            }
        } catch (e: Exception) {
            Log.e("PostitRepository", "Error syncing postit $postitId", e)
        }
    }
}