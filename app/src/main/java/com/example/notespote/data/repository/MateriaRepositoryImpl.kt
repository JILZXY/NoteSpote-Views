package com.example.notespote.data.repository

import com.example.notespote.domain.model.Materia
import com.example.notespote.domain.repository.MateriaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MateriaRepositoryImpl @Inject constructor() : MateriaRepository {
    override fun getAllMaterias(): Flow<Result<List<Materia>>> = flow {
        // TODO: Implement
    }

    override fun getMateriaById(materiaId: String): Flow<Result<Materia>> = flow {
        // TODO: Implement
    }

    override fun getMateriasByCategoria(categoria: String): Flow<Result<List<Materia>>> = flow {
        // TODO: Implement
    }

    override fun searchMaterias(query: String): Flow<Result<List<Materia>>> = flow {
        // TODO: Implement
    }

    override suspend fun createMateria(materia: Materia): Result<String> {
        // TODO: Implement
        return Result.success("")
    }

    override suspend fun updateMateria(materia: Materia): Result<Unit> {
        // TODO: Implement
        return Result.success(Unit)
    }

    override suspend fun deleteMateria(materiaId: String): Result<Unit> {
        // TODO: Implement
        return Result.success(Unit)
    }

    override suspend fun syncMaterias(): Result<Unit> {
        // TODO: Implement
        return Result.success(Unit)
    }
}
