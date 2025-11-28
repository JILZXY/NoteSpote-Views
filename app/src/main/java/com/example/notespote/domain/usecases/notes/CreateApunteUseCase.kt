package com.example.notespote.domain.usecases.notes

import android.net.Uri
import com.example.notespote.data.local.entities.TipoVisibilidad
import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.repository.ApunteRepository
import com.example.notespote.domain.repository.AuthRepository
import javax.inject.Inject

class CreateApunteUseCase @Inject constructor(
    private val apunteRepository: ApunteRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        titulo: String,
        contenido: String?,
        idCarpeta: String?,
        idMateria: String?,
        tipoVisibilidad: TipoVisibilidad,
        archivos: List<Uri>
    ): Result<String> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("Usuario no autenticado"))

        if (titulo.isBlank()) {
            return Result.failure(Exception("El título no puede estar vacío"))
        }

        val apunte = Apunte(
            idUsuario = userId,
            titulo = titulo,
            contenido = contenido,
            idCarpeta = idCarpeta,
            idMateria = idMateria,
            tipoVisibilidad = tipoVisibilidad,
            tieneImagenes = archivos.isNotEmpty()
        )

        return apunteRepository.createApunte(apunte, archivos)
    }
}
