package com.example.notespote.domain.usecases.folders

import com.example.notespote.domain.model.Carpeta
import com.example.notespote.domain.repository.CarpetaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchCarpetasUseCase @Inject constructor(
    private val carpetaRepository: CarpetaRepository
) {
    operator fun invoke(userId: String, query: String): Flow<Result<List<Carpeta>>> {
        return carpetaRepository.getCarpetasByUser(userId).map { result ->
            result.map { carpetas ->
                if (query.isBlank()) {
                    carpetas
                } else {
                    carpetas.filter { carpeta ->
                        carpeta.nombreCarpeta.contains(query, ignoreCase = true) ||
                                carpeta.descripcion?.contains(query, ignoreCase = true) == true
                    }
                }
            }
        }
    }
}
