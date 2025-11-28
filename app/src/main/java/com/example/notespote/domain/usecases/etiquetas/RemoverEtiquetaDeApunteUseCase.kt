package com.example.notespote.domain.usecases.etiquetas

import com.example.notespote.domain.repository.EtiquetaRepository
import javax.inject.Inject

class RemoverEtiquetaDeApunteUseCase @Inject constructor(
    private val etiquetaRepository: EtiquetaRepository
) {
    suspend operator fun invoke(apunteId: String, etiquetaId: String): Result<Unit> {
        return etiquetaRepository.removerEtiquetaDeApunte(apunteId, etiquetaId)
    }
}