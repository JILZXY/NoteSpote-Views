// domain/usecases/seguimiento/IsSiguiendoUseCase.kt
package com.example.notespote.domain.usecases.seguimiento

import com.example.notespote.domain.repository.AuthRepository
import com.example.notespote.domain.repository.SeguimientoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class IsSiguiendoUseCase @Inject constructor(
    private val seguimientoRepository: SeguimientoRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(seguidoId: String): Flow<Result<Boolean>> {
        return flow {
            val userId = authRepository.getCurrentUserId()
            if (userId != null) {
                seguimientoRepository.isSiguiendo(userId, seguidoId).collect { result ->
                    emit(result)
                }
            } else {
                emit(Result.success(false))
            }
        }
    }
}