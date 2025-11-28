package com.example.notespote.domain.usecases.sync

import com.example.notespote.domain.model.EstadoSincronizacion
import com.example.notespote.domain.repository.SyncRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEstadoSincronizacionUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    operator fun invoke(): Flow<EstadoSincronizacion> {
        return syncRepository.estadoSincronizacion
    }
}