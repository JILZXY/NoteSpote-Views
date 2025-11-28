package com.example.notespote.domain.usecases.folders

import com.example.notespote.domain.model.CarpetaContenido
import com.example.notespote.domain.repository.CarpetaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCarpetaConContenidoUseCase @Inject constructor(
    private val carpetaRepository: CarpetaRepository
) {
    operator fun invoke(carpetaId: String): Flow<Result<CarpetaContenido>> {
        return carpetaRepository.getCarpetaConContenido(carpetaId)
    }
}