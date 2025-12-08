package com.example.notespote.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.notespote.data.local.FileManager
import com.example.notespote.data.local.dao.ApunteDao
import com.example.notespote.data.local.dao.ArchivoAdjuntoDao
import com.example.notespote.data.local.dao.EtiquetaApunteDao
import com.example.notespote.data.local.dao.PostitDao
import com.example.notespote.data.local.entities.ArchivoAdjuntoEntity
import com.example.notespote.data.local.entities.EstadoDescarga
import com.example.notespote.data.local.entities.SyncStatus
import com.example.notespote.data.mapper.ApunteMapper
import com.example.notespote.data.mapper.ArchivoAdjuntoMapper
import com.example.notespote.data.mapper.EtiquetaMapper
import com.example.notespote.data.mapper.PostitMapper
import com.example.notespote.data.network.NetworkMonitor
import com.example.notespote.domain.model.Apunte
import com.example.notespote.domain.model.ApunteDetallado
import com.example.notespote.domain.model.FiltroApuntes
import com.example.notespote.domain.repository.ApunteRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApunteRepositoryImpl @Inject constructor(
    private val apunteDao: ApunteDao,
    private val archivoDao: ArchivoAdjuntoDao,
    private val postitDao: PostitDao,
    private val etiquetaApunteDao: EtiquetaApunteDao,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val fileManager: FileManager,
    private val networkMonitor: NetworkMonitor,
    private val apunteMapper: ApunteMapper,
    private val archivoMapper: ArchivoAdjuntoMapper,
    private val postitMapper: PostitMapper,
    private val etiquetaMapper: EtiquetaMapper,
    @ApplicationContext private val context: Context
) : ApunteRepository {

    override fun getApuntesByUser(userId: String): Flow<Result<List<com.example.notespote.domain.model.Apunte>>> {
        // Usamos el constructor flow para tener control total
        return kotlinx.coroutines.flow.flow {
            try {
                // Nos suscribimos al Flow del DAO dentro del bloque
                apunteDao.getApuntesByUser(userId).collect { entities ->
                    // Mapeamos los datos y los envolvemos en Result.success
                    val apuntes = entities.map { apunteMapper.toDomain(it) }
                    // Emitimos el resultado exitoso
                    emit(Result.success(apuntes))
                }
            } catch (e: Exception) {
                // Si ocurre cualquier error en el flujo (en el DAO o en el collect), lo capturamos
                Log.e("ApunteRepository", "Error en getApuntesByUser", e)
                // Emitimos el resultado de fallo
                emit(Result.failure(e))
            }
        }
    }

    override fun getApuntesByFolder(folderId: String): Flow<Result<List<com.example.notespote.domain.model.Apunte>>> {
        return apunteDao.getApuntesByFolder(folderId)
            .map { entities ->
                Result.success(entities.map { apunteMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override fun getApunteById(id: String): Flow<Result<com.example.notespote.domain.model.ApunteDetallado>> {
        return kotlinx.coroutines.flow.flow {
            val apunteWithDetails = apunteDao.getApunteWithDetails(id)
            if (apunteWithDetails != null) {
                val apunteDetallado = apunteMapper.toApunteDetallado(
                    apunteWithDetails,
                    postitMapper,
                    archivoMapper,
                    etiquetaMapper
                )
                emit(Result.success(apunteDetallado))
            } else {
                emit(Result.failure(Exception("Apunte no encontrado")))
            }
        }.catch { e ->
            emit(Result.failure(e))
        }
    }

    override suspend fun createApunte(
        apunte: com.example.notespote.domain.model.Apunte,
        archivos: List<Uri>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apunteId = UUID.randomUUID().toString()
            val entity = apunteMapper.toEntity(apunte).copy(
                idApunte = apunteId,
                syncStatus = SyncStatus.PENDING_UPLOAD
            )

            // Guardar apunte localmente
            apunteDao.insert(entity)

            // Guardar archivos localmente
            archivos.forEachIndexed { index, uri ->
                val fileName = "archivo_$index.${fileManager.getFileExtension(uri)}"
                val rutaLocal = fileManager.saveFileFromUri(apunteId, fileName, uri)

                val archivoEntity = ArchivoAdjuntoEntity(
                    idArchivo = UUID.randomUUID().toString(),
                    idApunte = apunteId,
                    nombreArchivo = fileName,
                    tipoArchivo = fileManager.getMimeType(uri),
                    rutaLocal = rutaLocal,
                    urlFirebase = null,
                    tamanoKb = fileManager.getFileSizeFromUri(uri),
                    fechaSubida = System.currentTimeMillis(),
                    estadoDescarga = EstadoDescarga.DESCARGADO,
                    syncStatus = SyncStatus.PENDING_UPLOAD,
                    isDeleted = false
                )
                archivoDao.insert(archivoEntity)
            }

            // Intentar sincronizar si hay conexión
            if (networkMonitor.isCurrentlyConnected()) {
                syncApunte(apunteId)
            }

            Result.success(apunteId)
        } catch (e: Exception) {
            Log.e("ApunteRepository", "Error creating apunte", e)
            Result.failure(e)
        }
    }

    override suspend fun updateApunte(apunte: com.example.notespote.domain.model.Apunte): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = apunteMapper.toEntity(apunte).copy(
                syncStatus = SyncStatus.PENDING_UPDATE,
                fechaActualizacion = System.currentTimeMillis()
            )

            apunteDao.update(entity)

            // Intentar sincronizar si hay conexión
            if (networkMonitor.isCurrentlyConnected()) {
                syncApunte(apunte.id)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ApunteRepository", "Error updating apunte", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteApunte(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Marcar como eliminado (soft delete)
            apunteDao.markAsDeleted(id)

            // Eliminar archivos locales
            fileManager.deleteApunteFiles(id)

            // Intentar sincronizar si hay conexión
            if (networkMonitor.isCurrentlyConnected()) {
                syncApunte(id)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ApunteRepository", "Error deleting apunte", e)
            Result.failure(e)
        }
    }

    override suspend fun guardarApunte(apunteId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Obtener el apunte original
            val apunteOriginal = apunteDao.getApunteById(apunteId)
                ?: return@withContext Result.failure(Exception("Apunte no encontrado"))

            // Crear una copia del apunte
            val nuevoApunteId = UUID.randomUUID().toString()
            val apunteCopia = apunteOriginal.copy(
                idApunte = nuevoApunteId,
                esOriginal = false,
                idApunteOriginal = apunteId,
                idUsuarioOriginal = apunteOriginal.idUsuario,
                // Cambiar el usuario al actual (se debería obtener del AuthRepository)
                // idUsuario = currentUserId,
                syncStatus = SyncStatus.PENDING_UPLOAD
            )

            apunteDao.insert(apunteCopia)

            // Actualizar contador de guardados
            apunteDao.update(
                apunteOriginal.copy(
                    totalGuardados = apunteOriginal.totalGuardados + 1
                )
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ApunteRepository", "Error saving apunte", e)
            Result.failure(e)
        }
    }

    override suspend fun toggleLike(apunteId: String, userId: String): Result<Unit> {
        return Result.success(Unit)
    }

    override fun getPublicApuntes(limit: Int): Flow<Result<List<com.example.notespote.domain.model.Apunte>>> {
        return apunteDao.getPublicApuntes(limit)
            .map { entities ->
                Result.success(entities.map { apunteMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override fun searchApuntes(filtro: FiltroApuntes): Flow<Result<List<Apunte>>> {
        return apunteDao.getPublicApuntes(50)
            .map { entities ->
                Result.success(entities.map { apunteMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override suspend fun syncApuntes(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!networkMonitor.isCurrentlyConnected()) {
                return@withContext Result.failure(Exception("Sin conexión a internet"))
            }

            // Obtener apuntes pendientes de sincronización
            val pendingApuntes = apunteDao.getPendingSyncApuntes()

            pendingApuntes.forEach { apunte ->
                syncApunte(apunte.idApunte)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ApunteRepository", "Error syncing apuntes", e)
            Result.failure(e)
        }
    }

    private suspend fun syncApunte(apunteId: String) {
        try {
            val apunte = apunteDao.getApunteById(apunteId) ?: return

            when (apunte.syncStatus) {
                SyncStatus.PENDING_UPLOAD -> {
                    // Subir archivos primero
                    val archivos = archivoDao.getArchivosByApunte(apunteId).first()
                    archivos.forEach { archivo ->
                        if (archivo.rutaLocal != null && archivo.urlFirebase == null) {
                            // Subir a Firebase Storage
                            val url = uploadFileToStorage(archivo)
                            archivoDao.update(
                                archivo.copy(
                                    urlFirebase = url,
                                    syncStatus = SyncStatus.SYNCED
                                )
                            )
                        }
                    }

                    // Subir apunte a Firestore
                    val dto = apunteMapper.entityToDto(apunte)
                    firestore.collection("apuntes")
                        .document(apunteId)
                        .set(dto)
                        .addOnSuccessListener {
                            Log.d("ApunteRepository", "Apunte $apunteId synced to Firestore successfully.")
                            // Marcar como sincronizado
                            kotlinx.coroutines.runBlocking {
                                apunteDao.updateSyncStatus(apunteId, SyncStatus.SYNCED)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("ApunteRepository", "Error uploading apunte", e)
                        }
                }
                SyncStatus.PENDING_UPDATE -> {
                    val dto = apunteMapper.entityToDto(apunte)
                    firestore.collection("apuntes")
                        .document(apunteId)
                        .set(dto)
                        .addOnSuccessListener {
                            Log.d("ApunteRepository", "Apunte $apunteId updated in Firestore successfully.")
                            kotlinx.coroutines.runBlocking {
                                apunteDao.updateSyncStatus(apunteId, SyncStatus.SYNCED)
                            }
                        }
                }
                SyncStatus.PENDING_DELETE -> {
                    firestore.collection("apuntes")
                        .document(apunteId)
                        .update("isDeleted", true)
                        .addOnSuccessListener {
                            kotlinx.coroutines.runBlocking {
                                apunteDao.delete(apunteId)
                            }
                        }
                }
                else -> {}
            }
        } catch (e: Exception) {
            Log.e("ApunteRepository", "Error syncing apunte $apunteId", e)
        }
    }

    private suspend fun uploadFileToStorage(archivo: ArchivoAdjuntoEntity): String {
        return withContext(Dispatchers.IO) {
            val file = fileManager.getFile(archivo.rutaLocal!!)
                ?: throw Exception("Archivo no encontrado")

            val storageRef = storage.reference
                .child("archivos/${archivo.idApunte}/${archivo.idArchivo}_${archivo.nombreArchivo}")

            val uri = fileManager.getFileUri(archivo.rutaLocal)
                ?: throw Exception("No se pudo obtener URI del archivo")

            val uploadTask = storageRef.putFile(uri)

            uploadTask.await()

            storageRef.downloadUrl.await().toString()
        }
    }
}