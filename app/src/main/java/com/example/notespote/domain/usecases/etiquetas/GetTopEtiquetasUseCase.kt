package com.example.notespote.domain.usecases.etiquetas

import com.example.notespote.domain.model.Etiqueta
import com.example.notespote.domain.repository.EtiquetaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopEtiquetasUseCase @Inject constructor(
    private val etiquetaRepository: EtiquetaRepository
) {
    operator fun invoke(limit: Int = 20): Flow<Result<List<Etiqueta>>> {
        return etiquetaRepository.getTopEtiquetas(limit)
    }
}