package com.example.notespote.domain.usecases.auth

import com.example.notespote.domain.model.Usuario
import com.example.notespote.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) {
    operator fun invoke(): Flow<Result<Usuario?>> {
        return usuarioRepository.getUsuarioActual()
    }
}