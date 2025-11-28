// domain/usecases/seguimiento/DejarDeSeguirUseCase.kt
package com.example.notespote.domain.usecases.seguimiento

import com.example.notespote.domain.repository.AuthRepository
import com.example.notespote.domain.repository.SeguimientoRepository
import javax.inject.Inject

class DejarDeSeguirUseCase @Inject constructor(
    private val seguimientoRepository: SeguimientoRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(seguidoId: String): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("Usuario no autenticado"))

        return seguimientoRepository.dejarDeSeguir(userId, seguidoId)
    }
}