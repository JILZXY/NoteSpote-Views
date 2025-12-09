package com.example.notespote.data.repository

import android.util.Log
import com.example.notespote.data.local.dao.UsuarioDao
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.local.entities.UsuarioEntity
import com.example.notespote.data.mapper.UsuarioMapper
import com.example.notespote.domain.model.SesionUsuario
import com.example.notespote.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val usuarioDao: UsuarioDao,
    private val usuarioMapper: UsuarioMapper
) : AuthRepository {

    override val sesionActual: Flow<SesionUsuario?> = kotlinx.coroutines.flow.flow {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val usuario = usuarioDao.getUsuarioById(userId)
            if (usuario != null) {
                emit(
                    SesionUsuario(
                        usuario = usuarioMapper.toDomain(usuario),
                        token = auth.currentUser?.getIdToken(false)?.await()?.token,
                        estaAutenticado = true
                    )
                )
            } else {
                emit(null)
            }
        } else {
            emit(null)
        }
    }

    override val estaAutenticado: Flow<Boolean> = kotlinx.coroutines.flow.flow {
        emit(auth.currentUser != null)
    }

    override suspend fun login(email: String, password: String): Result<SesionUsuario> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return@withContext Result.failure(Exception("Error al obtener usuario"))

            // Descargar datos del usuario desde Firestore
            val userDoc = firestore.collection("users").document(userId).get().await()
            val userDto = userDoc.toObject(com.example.notespote.data.remote.dto.UsuarioDto::class.java)
                ?: return@withContext Result.failure(Exception("Usuario no encontrado"))

            // Guardar localmente
            val userEntity = usuarioMapper.dtoToEntity(userDto)
            usuarioDao.insert(userEntity)

            val sesion = SesionUsuario(
                usuario = usuarioMapper.toDomain(userEntity),
                token = result.user?.getIdToken(false)?.await()?.token,
                estaAutenticado = true
            )

            Result.success(sesion)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en login", e)
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        nombreUsuario: String,
        nombre: String?,
        apellido: String?
    ): Result<SesionUsuario> = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return@withContext Result.failure(Exception("Error al crear usuario"))

            // Crear usuario en Firestore
            val userDto = com.example.notespote.data.remote.dto.UsuarioDto(
                id = userId,
                email = email,
                nombreUsuario = nombreUsuario,
                nombre = nombre,
                apellido = apellido
            )

            firestore.collection("users").document(userId).set(userDto).await()

            // Guardar localmente
            val userEntity = usuarioMapper.dtoToEntity(userDto)
            usuarioDao.insert(userEntity)

            val sesion = SesionUsuario(
                usuario = usuarioMapper.toDomain(userEntity),
                token = result.user?.getIdToken(false)?.await()?.token,
                estaAutenticado = true
            )

            Result.success(sesion)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en register", e)
            Result.failure(e)
        }
    }

    override suspend fun loginWithGoogle(): Result<SesionUsuario> {
        // TODO: Implementar login con Google
        return Result.failure(Exception("No implementado"))
    }

    override suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signOut()
            // TODO: Limpiar datos locales si es necesario
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en logout", e)
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en resetPassword", e)
            Result.failure(e)
        }
    }

    override suspend fun cambiarPassword(
        passwordActual: String,
        passwordNuevo: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser ?: return@withContext Result.failure(Exception("Usuario no autenticado"))

            // Reautenticar
            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, passwordActual)
            user.reauthenticate(credential).await()

            // Cambiar contraseña
            user.updatePassword(passwordNuevo).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en cambiarPassword", e)
            Result.failure(e)
        }
    }

    override suspend fun verificarEmail(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser ?: return@withContext Result.failure(Exception("Usuario no autenticado"))
            user.sendEmailVerification().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en verificarEmail", e)
            Result.failure(e)
        }
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun forceTokenRefresh(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.currentUser?.getIdToken(true)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error refreshing token", e)
            Result.failure(e)
        }
    }
}