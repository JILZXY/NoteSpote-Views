package com.example.notespote.domain.usecases.folders

import com.example.notespote.domain.model.Carpeta
import com.example.notespote.domain.repository.AuthRepository
import com.example.notespote.domain.repository.CarpetaRepository
import javax.inject.Inject

class CreateCarpetaUseCase @Inject constructor(
    private val carpetaRepository: CarpetaRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        nombreCarpeta: String,
        colorCarpeta: String?,
        descripcion: String?,
        idCarpetaPadre: String?,
        idMateria: String?
    ): Result<String> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("Usuario no autenticado"))

        if (nombreCarpeta.isBlank()) {
            return Result.failure(Exception("El nombre de la carpeta no puede estar vacío"))
        }

        val carpeta = Carpeta(
            idUsuario = userId,
            nombreCarpeta = nombreCarpeta,
            colorCarpeta = colorCarpeta,
            descripcion = descripcion,
            idCarpetaPadre = idCarpetaPadre,
            idMateria = idMateria
        )

        return carpetaRepository.createCarpeta(carpeta)
    }
}