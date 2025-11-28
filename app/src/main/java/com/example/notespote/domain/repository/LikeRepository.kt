package com.example.notespote.domain.repository

import com.example.notespote.domain.model.Like
import kotlinx.coroutines.flow.Flow

interface LikeRepository {
    fun getLikesByUser(userId: String): Flow<Result<List<Like>>>
    fun getLikesByApunte(apunteId: String): Flow<Result<List<Like>>>
    fun hasLiked(userId: String, apunteId: String): Flow<Result<Boolean>>
    suspend fun getLikesCount(apunteId: String): Result<Int>
    suspend fun addLike(userId: String, apunteId: String): Result<Unit>
    suspend fun removeLike(userId: String, apunteId: String): Result<Unit>
    suspend fun toggleLike(userId: String, apunteId: String): Result<Unit>
    suspend fun syncLikes(): Result<Unit>
}