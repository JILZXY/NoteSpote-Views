package com.example.notespote.domain.usecases.sync

import com.example.notespote.domain.repository.SyncRepository
import javax.inject.Inject

class SyncAllUseCase @Inject constructor(
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return syncRepository.syncAll()
    }
}