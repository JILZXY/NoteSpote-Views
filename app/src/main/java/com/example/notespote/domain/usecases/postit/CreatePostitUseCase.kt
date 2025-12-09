package com.example.notespote.domain.usecases.postits

import com.example.notespote.domain.model.Postit
import com.example.notespote.domain.repository.PostitRepository
import javax.inject.Inject

class CreatePostitUseCase @Inject constructor(
    private val postitRepository: PostitRepository
) {
    suspend operator fun invoke(
        idApunte: String,
        titulo: String?,
        contenido: String?,
        color: String,
        posicionX: Int,
        posicionY: Int,
        ancho: Int,
        alto: Int
    ): Result<String> {
        val postit = Postit(
            idApunte = idApunte,
            titulo = titulo,
            contenido = contenido,
            color = color,
            posicionX = posicionX,
            posicionY = posicionY,
            ancho = ancho,
            alto = alto
        )

        return postitRepository.createPostit(postit)
    }
}