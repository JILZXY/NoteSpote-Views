package com.example.notespote.domain.usecases.etiquetas

import com.example.notespote.domain.model.Etiqueta
import com.example.notespote.domain.repository.EtiquetaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEtiquetaByIdUseCase @Inject constructor(
    private val repository: EtiquetaRepository
) {
    operator fun invoke(etiquetaId: String): Flow<Result<Etiqueta>> {
        return repository.getEtiquetaById(etiquetaId)
    }
}