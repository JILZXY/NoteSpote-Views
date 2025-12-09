// domain/usecases/seguimiento/GetMisSeguidosUseCase.kt
package com.example.notespote.domain.usecases.seguimiento

import com.example.notespote.domain.model.Usuario
import com.example.notespote.domain.repository.AuthRepository
import com.example.notespote.domain.repository.SeguimientoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetMisSeguidosUseCase @Inject constructor(
    private val seguimientoRepository: SeguimientoRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Result<List<Usuario>>> {
        return flow {
            val userId = authRepository.getCurrentUserId()
            if (userId != null) {
                seguimientoRepository.getSeguidos(userId).collect { result ->
                    emit(result)
                }
            } else {
                emit(Result.failure(Exception("Usuario no autenticado")))
            }
        }
    }
}