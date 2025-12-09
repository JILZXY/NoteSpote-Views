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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun getApuntesByUser(userId: String): Flow<Result<List<Apunte>>> {
        return apunteDao.getApuntesByUser(userId)
            .map { entities ->
                Result.success(entities.map { apunteMapper.toDomain(it) })
            }
            .catch { e ->
                Log.e("ApunteRepository", "Error en getApuntesByUser", e)
                emit(Result.failure(e))
            }
    }

    override fun getApuntesByFolder(folderId: String): Flow<Result<List<Apunte>>> {
        return apunteDao.getApuntesByFolder(folderId)
            .map { entities ->
                Result.success(entities.map { apunteMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override fun getApunteById(id: String): Flow<Result<ApunteDetallado>> {
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
        apunte: Apunte,
        archivos: List<Uri>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apunteId = UUID.randomUUID().toString()
            val entity = apunteMapper.toEntity(apunte).copy(
                idApunte = apunteId,
                syncStatus = SyncStatus.PENDING_UPLOAD
            )

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

            // ✅ CAMBIO: Esperar la sincronización
            if (networkMonitor.isCurrentlyConnected()) {
                try {
                    syncApunteNow(apunteId) // Renombrar y hacer pública
                } catch (e: Exception) {
                    Log.e("ApunteRepo", "Error en sync, quedará pendiente: ${e.message}")
                    // El apunte queda con PENDING_UPLOAD para sincronizar después
                }
            }

            Result.success(apunteId)
        } catch (e: Exception) {
            Log.e("ApunteRepository", "Error creating apunte", e)
            Result.failure(e)
        }
    }

    suspend fun syncApunteNow(apunteId: String) {
        Log.d("SyncApunte", "🔄 Iniciando sync para: $apunteId")

        val apunte = apunteDao.getApunteById(apunteId)
        if (apunte == null) {
            Log.e("SyncApunte", "❌ Apunte no encontrado")
            return
        }

        Log.d("SyncApunte", "📄 Apunte encontrado, syncStatus: ${apunte.syncStatus}")

        when (apunte.syncStatus) {
            SyncStatus.PENDING_UPLOAD -> {
                Log.d("SyncApunte", "📤 Subiendo archivos...")

                // Subir archivos primero
                val archivos = archivoDao.getArchivosByApunte(apunteId).first()
                archivos.forEach { archivo ->
                    if (archivo.rutaLocal != null && archivo.urlFirebase == null) {
                        Log.d("SyncApunte", "📎 Subiendo: ${archivo.nombreArchivo}")

                        val url = uploadFileToStorage(archivo)

                        Log.d("SyncApunte", "✅ Archivo subido: $url")

                        archivoDao.update(
                            archivo.copy(
                                urlFirebase = url,
                                syncStatus = SyncStatus.SYNCED
                            )
                        )
                    }
                }

                Log.d("SyncApunte", "📤 Subiendo apunte a Firestore...")

                // Subir apunte a Firestore
                val dto = apunteMapper.entityToDto(apunte)
                firestore.collection("apuntes")
                    .document(apunteId)
                    .set(dto)
                    .await()

                Log.d("SyncApunte", "✅ Apunte subido exitosamente")

                apunteDao.updateSyncStatus(apunteId, SyncStatus.SYNCED)
            }
            else -> {
                Log.d("SyncApunte", "⏭️ No requiere sync: ${apunte.syncStatus}")
            }
        }
    }

    override suspend fun updateApunte(apunte: Apunte): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = apunteMapper.toEntity(apunte).copy(
                syncStatus = SyncStatus.PENDING_UPDATE,
                fechaActualizacion = System.currentTimeMillis()
            )

            apunteDao.update(entity)

            if (networkMonitor.isCurrentlyConnected()) {
                repositoryScope.launch {
                    syncApunte(apunte.id)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ApunteRepository", "Error updating apunte", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteApunte(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            apunteDao.markAsDeleted(id)
            fileManager.deleteApunteFiles(id)

            if (networkMonitor.isCurrentlyConnected()) {
                repositoryScope.launch {
                    syncApunte(id)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ApunteRepository", "Error deleting apunte", e)
            Result.failure(e)
        }
    }

    override suspend fun guardarApunte(apunteId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val apunteOriginal = apunteDao.getApunteById(apunteId)
                ?: return@withContext Result.failure(Exception("Apunte no encontrado"))

            val nuevoApunteId = UUID.randomUUID().toString()
            val apunteCopia = apunteOriginal.copy(
                idApunte = nuevoApunteId,
                esOriginal = false,
                idApunteOriginal = apunteId,
                idUsuarioOriginal = apunteOriginal.idUsuario,
                syncStatus = SyncStatus.PENDING_UPLOAD
            )

            apunteDao.insert(apunteCopia)

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
        // This should probably be implemented with a background job as well
        return Result.success(Unit)
    }

    override fun getPublicApuntes(limit: Int): Flow<Result<List<Apunte>>> {
        return apunteDao.getPublicApuntes(limit)
            .map { entities ->
                Result.success(entities.map { apunteMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }

    override fun searchApuntes(filtro: FiltroApuntes): Flow<Result<List<Apunte>>> {
        return apunteDao.getPublicApuntes(50) // Placeholder
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

            val pendingApuntes = apunteDao.getPendingSyncApuntes()

            pendingApuntes.forEach { apunte ->
                repositoryScope.launch {
                    syncApunte(apunte.idApunte)
                }
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
                SyncStatus.PENDING_UPLOAD, SyncStatus.PENDING_UPDATE -> {
                    val archivos = archivoDao.getArchivosByApunte(apunteId).first()
                    archivos.forEach { archivo ->
                        if (archivo.rutaLocal != null && archivo.urlFirebase == null) {
                            val url = uploadFileToStorage(archivo)
                            archivoDao.update(
                                archivo.copy(
                                    urlFirebase = url,
                                    syncStatus = SyncStatus.SYNCED
                                )
                            )
                        }
                    }

                    val dto = apunteMapper.entityToDto(apunte)
                    firestore.collection("apuntes").document(apunteId).set(dto).await()
                    Log.d("ApunteRepository", "✅ Apunte $apunteId synced to Firestore.")
                    apunteDao.updateSyncStatus(apunteId, SyncStatus.SYNCED)
                }
                SyncStatus.PENDING_DELETE -> {
                    firestore.collection("apuntes").document(apunteId).update("isDeleted", true).await()
                    apunteDao.delete(apunteId)
                    Log.d("ApunteRepository", "✅ Apunte $apunteId deleted from Firestore.")
                }
                else -> {}
            }
        } catch (e: Exception) {
            Log.e("ApunteRepository", "❌ Error syncing apunte $apunteId", e)
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

            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        }
    }

    override fun getRecentApuntes(userId: String, limit: Int): Flow<Result<List<Apunte>>> {
        return apunteDao.getRecentApuntes(userId, limit)
            .map { entities ->
                Result.success(entities.map { apunteMapper.toDomain(it) })
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }
}