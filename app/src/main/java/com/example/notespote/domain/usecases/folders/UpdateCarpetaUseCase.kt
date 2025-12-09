package com.example.notespote.domain.usecases.folders

import com.example.notespote.domain.model.Carpeta
import com.example.notespote.domain.repository.CarpetaRepository
import javax.inject.Inject

class UpdateCarpetaUseCase @Inject constructor(
    private val carpetaRepository: CarpetaRepository
) {
    suspend operator fun invoke(carpeta: Carpeta): Result<Unit> {
        if (carpeta.nombreCarpeta.isBlank()) {
            return Result.failure(Exception("El nombre de la carpeta no puede estar vacío"))
        }

        return carpetaRepository.updateCarpeta(carpeta)
    }
}