package com.example.notespote.data.repository

import android.util.Log
import com.example.notespote.data.local.dao.ApunteDao
import com.example.notespote.data.local.dao.LikeDao
import com.example.notespote.data.local.entities.LikeEntity
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.mapper.LikeMapper
import com.example.notespote.data.network.NetworkMonitor
import com.example.notespote.domain.model.Like
import com.example.notespote.domain.repository.LikeRepository
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
class LikeRepositoryImpl @Inject constructor(
    private val likeDao: LikeDao,
    private val apunteDao: ApunteDao,
    private val firestore: FirebaseFirestore,
    private val networkMonitor: NetworkMonitor,
    private val likeMapper: LikeMapper
) : LikeRepository {

    override fun getLikesByUser(userId: String): Flow<Result<List<Like>>> {
        return likeDao.getLikesByUser(userId)
            .map { entities ->
                Result.success(entities.map { likeMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override fun getLikesByApunte(apunteId: String): Flow<Result<List<Like>>> {
        return likeDao.getLikesByApunte(apunteId)
            .map { entities ->
                Result.success(entities.map { likeMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override fun hasLiked(userId: String, apunteId: String): Flow<Result<Boolean>> {
        return kotlinx.coroutines.flow.flow {
            val hasLiked = likeDao.hasLiked(userId, apunteId)
            emit(Result.success(hasLiked))
        }.catch { e ->
            emit(Result.failure(e))
        }
    }

    override suspend fun getLikesCount(apunteId: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val count = likeDao.getLikesCount(apunteId)
            Result.success(count)
        } catch (e: Exception) {
            Log.e("LikeRepository", "Error getting likes count", e)
            Result.failure(e)
        }
    }

    override suspend fun addLike(userId: String, apunteId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val likeId = UUID.randomUUID().toString()
            val entity = LikeEntity(
                idLike = likeId,
                idUsuario = userId,
                idApunte = apunteId,
                fechaLike = System.currentTimeMillis(),
                syncStatus = SyncStatus.PENDING_UPLOAD,
                isDeleted = false
            )

            likeDao.insert(entity)

            // Actualizar contador en el apunte
            val apunte = apunteDao.getApunteById(apunteId)
            if (apunte != null) {
                apunteDao.update(
                    apunte.copy(
                        totalLikes = apunte.totalLikes + 1
                    )
                )
            }

            if (networkMonitor.isCurrentlyConnected()) {
                val dto = likeMapper.entityToDto(entity)
                firestore.collection("likes")
                    .document(likeId)
                    .set(dto)
                    .await()

                // Actualizar contador en Firestore
                firestore.collection("apuntes")
                    .document(apunteId)
                    .update("totalLikes", com.google.firebase.firestore.FieldValue.increment(1))
                    .await()

                likeDao.updateSyncStatus(likeId, SyncStatus.SYNCED)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("LikeRepository", "Error adding like", e)
            Result.failure(e)
        }
    }

    override suspend fun removeLike(userId: String, apunteId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            likeDao.markAsDeletedByUserAndApunte(userId, apunteId)

            // Actualizar contador en el apunte
            val apunte = apunteDao.getApunteById(apunteId)
            if (apunte != null && apunte.totalLikes > 0) {
                apunteDao.update(
                    apunte.copy(
                        totalLikes = apunte.totalLikes - 1
                    )
                )
            }

            if (networkMonitor.isCurrentlyConnected()) {
                val like = likeDao.getLike(userId, apunteId)
                if (like != null) {
                    firestore.collection("likes")
                        .document(like.idLike)
                        .update("isDeleted", true)
                        .await()

                    // Actualizar contador en Firestore
                    firestore.collection("apuntes")
                        .document(apunteId)
                        .update("totalLikes", com.google.firebase.firestore.FieldValue.increment(-1))
                        .await()

                    likeDao.deleteByUserAndApunte(userId, apunteId)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("LikeRepository", "Error removing like", e)
            Result.failure(e)
        }
    }

    override suspend fun toggleLike(userId: String, apunteId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val hasLiked = likeDao.hasLiked(userId, apunteId)

            if (hasLiked) {
                removeLike(userId, apunteId)
            } else {
                addLike(userId, apunteId)
            }
        } catch (e: Exception) {
            Log.e("LikeRepository", "Error toggling like", e)
            Result.failure(e)
        }
    }

    override suspend fun syncLikes(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!networkMonitor.isCurrentlyConnected()) {
                return@withContext Result.failure(Exception("Sin conexión a internet"))
            }

            val pendingLikes = likeDao.getPendingSyncLikes()
            pendingLikes.forEach { like ->
                when (like.syncStatus) {
                    SyncStatus.PENDING_UPLOAD -> {
                        val dto = likeMapper.entityToDto(like)
                        firestore.collection("likes")
                            .document(like.idLike)
                            .set(dto)
                            .await()

                        likeDao.updateSyncStatus(like.idLike, SyncStatus.SYNCED)
                    }
                    SyncStatus.PENDING_DELETE -> {
                        firestore.collection("likes")
                            .document(like.idLike)
                            .update("isDeleted", true)
                            .await()

                        likeDao.delete(like.idLike)
                    }
                    else -> {}
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("LikeRepository", "Error syncing likes", e)
            Result.failure(e)
        }
    }
}