package com.example.notespote.domain.usecases.folders

import com.example.notespote.domain.model.Carpeta
import com.example.notespote.domain.repository.CarpetaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubcarpetasUseCase @Inject constructor(
    private val carpetaRepository: CarpetaRepository
) {
    operator fun invoke(carpetaPadreId: String): Flow<Result<List<Carpeta>>> {
        return carpetaRepository.getSubcarpetas(carpetaPadreId)
    }
}