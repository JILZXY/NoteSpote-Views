package com.example.notespote.domain.usecases.etiquetas

import com.example.notespote.domain.repository.EtiquetaRepository
import javax.inject.Inject

class AgregarEtiquetaAApunteUseCase @Inject constructor(
    private val etiquetaRepository: EtiquetaRepository
) {
    suspend operator fun invoke(apunteId: String, nombreEtiqueta: String): Result<Unit> {
        if (nombreEtiqueta.isBlank()) {
            return Result.failure(Exception("El nombre de la etiqueta no puede estar vacío"))
        }

        // Obtener o crear la etiqueta
        val etiquetaResult = etiquetaRepository.getOrCreateEtiqueta(nombreEtiqueta)

        return if (etiquetaResult.isSuccess) {
            val etiquetaId = etiquetaResult.getOrNull()!!
            etiquetaRepository.agregarEtiquetaAApunte(apunteId, etiquetaId)
        } else {
            etiquetaResult.map { }
        }
    }
}