package com.example.notespote.domain.usecases.folders

import com.example.notespote.data.local.dao.UsuarioDao
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.local.entities.UsuarioEntity
import com.example.notespote.domain.model.Carpeta
import com.example.notespote.domain.repository.AuthRepository
import com.example.notespote.domain.repository.CarpetaRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class CreateCarpetaUseCase @Inject constructor(
    private val carpetaRepository: CarpetaRepository,
    private val authRepository: AuthRepository,
    private val usuarioDao: UsuarioDao,
    private val firebaseAuth: FirebaseAuth
) {
    suspend operator fun invoke(
        nombreCarpeta: String,
        colorCarpeta: String?,
        descripcion: String?,
        idCarpetaPadre: String?,
        idMateria: String?
    ): Result<String> {
        android.util.Log.d("CreateCarpetaUseCase", "invoke called with nombreCarpeta=$nombreCarpeta, colorCarpeta=$colorCarpeta")

        val userId = authRepository.getCurrentUserId()
        android.util.Log.d("CreateCarpetaUseCase", "Current userId: $userId")

        if (userId == null) {
            android.util.Log.e("CreateCarpetaUseCase", "Usuario no autenticado")
            return Result.failure(Exception("Usuario no autenticado"))
        }

        if (nombreCarpeta.isBlank()) {
            android.util.Log.e("CreateCarpetaUseCase", "Nombre de carpeta vacío")
            return Result.failure(Exception("El nombre de la carpeta no puede estar vacío"))
        }

        // Asegurar que el usuario existe en la BD local
        try {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                // Verificar si el usuario ya existe para evitar eliminación en cascada
                val existingUser = usuarioDao.getUsuarioById(userId)
                if (existingUser == null) {
                    // Solo insertar si el usuario NO existe
                    val usuarioEntity = UsuarioEntity(
                        idUsuario = userId,
                        email = firebaseUser.email ?: "",
                        nombreUsuario = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "Usuario",
                        nombre = firebaseUser.displayName,
                        apellido = null,
                        password = null,
                        fotoPerfil = firebaseUser.photoUrl?.toString(),
                        biografia = null,
                        fechaRegistro = System.currentTimeMillis(),
                        ultimaConexion = System.currentTimeMillis(),
                        syncStatus = SyncStatus.SYNCED,
                        lastSync = System.currentTimeMillis()
                    )
                    android.util.Log.d("CreateCarpetaUseCase", "Inserting new user in local DB: ${usuarioEntity.nombreUsuario}")
                    usuarioDao.insert(usuarioEntity)
                } else {
                    android.util.Log.d("CreateCarpetaUseCase", "User already exists in DB, skipping insert")
                }
            }
        } catch (e: Exception) {
            android.util.Log.w("CreateCarpetaUseCase", "Error ensuring user exists: ${e.message}", e)
        }

        val carpeta = Carpeta(
            idUsuario = userId,
            nombreCarpeta = nombreCarpeta,
            colorCarpeta = colorCarpeta,
            descripcion = descripcion,
            idCarpetaPadre = idCarpetaPadre,
            idMateria = idMateria
        )

        android.util.Log.d("CreateCarpetaUseCase", "Creating carpeta: $carpeta")
        val result = carpetaRepository.createCarpeta(carpeta)
        android.util.Log.d("CreateCarpetaUseCase", "Repository result: $result")

        return result
    }
}