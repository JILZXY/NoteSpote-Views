package com.example.notespote.data.repository

import android.net.Uri
import android.util.Log
import com.example.notespote.data.local.dao.ApunteDao
import com.example.notespote.data.local.dao.UsuarioDao
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.mapper.UsuarioMapper
import com.example.notespote.data.network.NetworkMonitor
import com.example.notespote.domain.model.EstadisticasUsuario
import com.example.notespote.domain.model.PerfilUsuario
import com.example.notespote.domain.model.Usuario
import com.example.notespote.domain.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepositoryImpl @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val apunteDao: ApunteDao,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
    private val networkMonitor: NetworkMonitor,
    private val usuarioMapper: UsuarioMapper
) : UsuarioRepository {

    override fun getUsuarioById(userId: String): Flow<Result<Usuario>> {
        return usuarioDao.getUsuarioByIdFlow(userId)
            .map { entity ->
                if (entity != null) {
                    Result.success(usuarioMapper.toDomain(entity))
                } else {
                    Result.failure(Exception("Usuario no encontrado"))
                }
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override fun getUsuarioActual(): Flow<Result<Usuario?>> {
        val userId = auth.currentUser?.uid
        return if (userId != null) {
            usuarioDao.getUsuarioByIdFlow(userId)
                .map { entity ->
                    Result.success(entity?.let { usuarioMapper.toDomain(it) })
                }
                .catch { e ->
                    emit(Result.failure(e))
                }
        } else {
            kotlinx.coroutines.flow.flow {
                emit(Result.success(null))
            }
        }
    }

    override fun getPerfilUsuario(userId: String): Flow<Result<PerfilUsuario>> {
        return kotlinx.coroutines.flow.flow {
            val usuario = usuarioDao.getUsuarioById(userId)
            if (usuario != null) {
                val apuntesPublicos = apunteDao.getPublicApuntes(10)
                    .map { entities -> entities.map { it } }
                    .catch { emptyList<com.example.notespote.data.local.entities.ApunteEntity>() }

                val perfil = PerfilUsuario(
                    usuario = usuarioMapper.toDomain(usuario),
                    apuntesPublicos = emptyList(), // TODO: mapear apuntes
                    siguiendo = false, // TODO: verificar si sigue
                    esPropio = usuario.idUsuario == auth.currentUser?.uid
                )
                emit(Result.success(perfil))
            } else {
                emit(Result.failure(Exception("Usuario no encontrado")))
            }
        }.catch { e ->
            emit(Result.failure(e))
        }
    }

    override fun searchUsuarios(query: String): Flow<Result<List<Usuario>>> {
        return kotlinx.coroutines.flow.flow {
            if (networkMonitor.isCurrentlyConnected()) {
                val result = firestore.collection("users")
                    .whereGreaterThanOrEqualTo("nombreUsuario", query)
                    .whereLessThanOrEqualTo("nombreUsuario", query + '\uf8ff')
                    .limit(20)
                    .get()
                    .await()

                val usuarios = result.documents.mapNotNull { doc ->
                    val dto = doc.toObject(com.example.notespote.data.remote.dto.UsuarioDto::class.java)
                    dto?.let { usuarioMapper.toDomain(usuarioMapper.dtoToEntity(it)) }
                }

                emit(Result.success(usuarios))
            } else {
                emit(Result.failure(Exception("Sin conexión a internet")))
            }
        }.catch { e ->
            emit(Result.failure(e))
        }
    }

    override suspend fun updateUsuario(usuario: Usuario): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = usuarioMapper.toEntity(usuario).copy(
                syncStatus = SyncStatus.PENDING_UPDATE
            )

            usuarioDao.update(entity)

            if (networkMonitor.isCurrentlyConnected()) {
                val dto = usuarioMapper.entityToDto(entity)
                firestore.collection("users")
                    .document(usuario.id)
                    .set(dto)
                    .await()

                usuarioDao.updateSyncStatus(usuario.id, SyncStatus.SYNCED)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Error updating usuario", e)
            Result.failure(e)
        }
    }

    override suspend fun updateFotoPerfil(userId: String, uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val storageRef = storage.reference
                .child("usuarios/$userId/foto_perfil.jpg")

            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()

            val usuario = usuarioDao.getUsuarioById(userId)
                ?: return@withContext Result.failure(Exception("Usuario no encontrado"))

            usuarioDao.update(
                usuario.copy(
                    fotoPerfil = downloadUrl,
                    syncStatus = SyncStatus.PENDING_UPDATE
                )
            )

            if (networkMonitor.isCurrentlyConnected()) {
                firestore.collection("users")
                    .document(userId)
                    .update("fotoPerfilUrl", downloadUrl)
                    .await()
            }

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Error updating foto perfil", e)
            Result.failure(e)
        }
    }

    override suspend fun updateBiografia(userId: String, biografia: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val usuario = usuarioDao.getUsuarioById(userId)
                ?: return@withContext Result.failure(Exception("Usuario no encontrado"))

            usuarioDao.update(
                usuario.copy(
                    biografia = biografia,
                    syncStatus = SyncStatus.PENDING_UPDATE
                )
            )

            if (networkMonitor.isCurrentlyConnected()) {
                firestore.collection("users")
                    .document(userId)
                    .update("biografia", biografia)
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Error updating biografia", e)
            Result.failure(e)
        }
    }

    override suspend fun getEstadisticas(userId: String): Result<EstadisticasUsuario> = withContext(Dispatchers.IO) {
        try {
            // TODO: Implementar estadísticas completas
            val usuario = usuarioDao.getUsuarioById(userId)
                ?: return@withContext Result.failure(Exception("Usuario no encontrado"))

            val estadisticas = EstadisticasUsuario(
                totalLikesRecibidos = usuario.totalLikesRecibidos,
                totalSeguidores = usuario.totalSeguidores,
                totalSeguidos = usuario.totalSeguidos
            )

            Result.success(estadisticas)
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Error getting estadisticas", e)
            Result.failure(e)
        }
    }

    override suspend fun updateUltimaConexion(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()
            usuarioDao.updateUltimaConexion(userId, timestamp)

            if (networkMonitor.isCurrentlyConnected()) {
                firestore.collection("users")
                    .document(userId)
                    .update("ultimaConexion", com.google.firebase.Timestamp(java.util.Date(timestamp)))
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Error updating ultima conexion", e)
            Result.failure(e)
        }
    }

    override suspend fun syncUsuario(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid
                ?: return@withContext Result.failure(Exception("Usuario no autenticado"))

            if (!networkMonitor.isCurrentlyConnected()) {
                return@withContext Result.failure(Exception("Sin conexión a internet"))
            }

            val usuario = usuarioDao.getUsuarioById(userId)
                ?: return@withContext Result.failure(Exception("Usuario no encontrado"))

            if (usuario.syncStatus != SyncStatus.SYNCED) {
                val dto = usuarioMapper.entityToDto(usuario)
                firestore.collection("users")
                    .document(userId)
                    .set(dto)
                    .await()

                usuarioDao.updateSyncStatus(userId, SyncStatus.SYNCED)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Error syncing usuario", e)
            Result.failure(e)
        }
    }
}