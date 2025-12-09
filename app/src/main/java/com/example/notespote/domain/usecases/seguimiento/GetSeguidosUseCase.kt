// domain/usecases/seguimiento/GetSeguidosUseCase.kt
package com.example.notespote.domain.usecases.seguimiento

import com.example.notespote.domain.model.Usuario
import com.example.notespote.domain.repository.SeguimientoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSeguidosUseCase @Inject constructor(
    private val seguimientoRepository: SeguimientoRepository
) {
    operator fun invoke(userId: String): Flow<Result<List<Usuario>>> {
        return seguimientoRepository.getSeguidos(userId)
    }
}