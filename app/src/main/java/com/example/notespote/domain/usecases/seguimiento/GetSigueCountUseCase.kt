// domain/usecases/seguimiento/GetSigueCountUseCase.kt
package com.example.notespote.domain.usecases.seguimiento

import com.example.notespote.domain.repository.SeguimientoRepository
import javax.inject.Inject

class GetSigueCountUseCase @Inject constructor(
    private val seguimientoRepository: SeguimientoRepository
) {
    suspend operator fun invoke(userId: String): Result<Int> {
        return seguimientoRepository.getSigueCount(userId)
    }
}