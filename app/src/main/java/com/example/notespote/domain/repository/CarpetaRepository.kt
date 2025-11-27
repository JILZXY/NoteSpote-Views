package com.example.notespote.domain.repository

import com.example.notespote.domain.model.Carpeta
import com.example.notespote.domain.model.CarpetaContenido
import kotlinx.coroutines.flow.Flow

interface CarpetaRepository {
    fun getCarpetasByUser(userId: String): Flow<Result<List<Carpeta>>>
    fun getCarpetasRaiz(userId: String): Flow<Result<List<Carpeta>>>
    fun getSubcarpetas(carpetaPadreId: String): Flow<Result<List<Carpeta>>>
    fun getCarpetaById(carpetaId: String): Flow<Result<Carpeta>>
    fun getCarpetaConContenido(carpetaId: String): Flow<Result<CarpetaContenido>>
    fun getCarpetasByMateria(materiaId: String): Flow<Result<List<Carpeta>>>
    suspend fun createCarpeta(carpeta: Carpeta): Result<String>
    suspend fun updateCarpeta(carpeta: Carpeta): Result<Unit>
    suspend fun deleteCarpeta(carpetaId: String): Result<Unit>
    suspend fun moverCarpeta(carpetaId: String, nuevoPadreId: String?): Result<Unit>
    suspend fun reordenarCarpetas(carpetas: List<Pair<String, Int>>): Result<Unit>
    suspend fun syncCarpetas(): Result<Unit>
}