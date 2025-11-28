package com.example.notespote.domain.usecases.folders

import com.example.notespote.domain.model.Carpeta
import com.example.notespote.domain.repository.CarpetaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCarpetasRaizUseCase @Inject constructor(
    private val carpetaRepository: CarpetaRepository
) {
    operator fun invoke(userId: String): Flow<Result<List<Carpeta>>> {
        return carpetaRepository.getCarpetasRaiz(userId)
    }
}