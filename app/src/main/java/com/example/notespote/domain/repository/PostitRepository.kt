package com.example.notespote.domain.repository

import com.example.notespote.domain.model.Postit
import kotlinx.coroutines.flow.Flow

interface PostitRepository {
    fun getPostitsByApunte(apunteId: String): Flow<Result<List<Postit>>>
    suspend fun getPostitById(postitId: String): Result<Postit>
    suspend fun createPostit(postit: Postit): Result<String>
    suspend fun updatePostit(postit: Postit): Result<Unit>
    suspend fun updatePosicion(postitId: String, x: Int, y: Int): Result<Unit>
    suspend fun updateTamano(postitId: String, ancho: Int, alto: Int): Result<Unit>
    suspend fun updateOrdenZ(postitId: String, ordenZ: Int): Result<Unit>
    suspend fun deletePostit(postitId: String): Result<Unit>
    suspend fun syncPostits(): Result<Unit>
}