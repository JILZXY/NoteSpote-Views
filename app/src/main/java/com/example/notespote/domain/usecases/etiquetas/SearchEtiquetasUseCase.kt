package com.example.notespote.domain.usecases.etiquetas

import com.example.notespote.domain.model.Etiqueta
import com.example.notespote.domain.repository.EtiquetaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchEtiquetasUseCase @Inject constructor(
    private val etiquetaRepository: EtiquetaRepository
) {
    operator fun invoke(query: String, limit: Int = 10): Flow<Result<List<Etiqueta>>> {
        return etiquetaRepository.searchEtiquetas(query, limit)
    }
}