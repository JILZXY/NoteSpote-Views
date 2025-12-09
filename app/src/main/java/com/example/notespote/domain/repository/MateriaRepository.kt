package com.example.notespote.domain.repository

import com.example.notespote.domain.model.Materia
import kotlinx.coroutines.flow.Flow

interface MateriaRepository {
    fun getAllMaterias(): Flow<Result<List<Materia>>>
    fun getMateriaById(materiaId: String): Flow<Result<Materia>>
    fun getMateriasByCategoria(categoria: String): Flow<Result<List<Materia>>>
    fun searchMaterias(query: String): Flow<Result<List<Materia>>>
    suspend fun createMateria(materia: Materia): Result<String>
    suspend fun updateMateria(materia: Materia): Result<Unit>
    suspend fun deleteMateria(materiaId: String): Result<Unit>
    suspend fun syncMaterias(): Result<Unit>
}